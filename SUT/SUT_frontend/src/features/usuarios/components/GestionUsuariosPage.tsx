import { useEffect, useState } from 'react';
import { usuarioService } from '../services/usuarioService';
import type { Usuario } from '../../../types';
import { Users, Shield, ShieldAlert, Trash2 } from 'lucide-react'; // Importamos Trash2

export const GestionUsuariosPage = () => {
    const [usuarios, setUsuarios] = useState<Usuario[]>([]);
    const [loading, setLoading] = useState(true);

    // Cargar usuarios al inicio
    const cargar = async () => {
        try {
            setLoading(true);
            const data = await usuarioService.getAll();
            
            const ordenados = data.sort((a, b) => {
                if (a.rol === b.rol) return a.nombre.localeCompare(b.nombre);
                return a.rol.includes('ADMIN') ? -1 : 1;
            });
            setUsuarios(ordenados);
        } catch (e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { cargar(); }, []);

    // Función: Cambiar Rol
    const toggleRol = async (u: Usuario) => {
        const esAdmin = u.rol.toUpperCase().includes('ADMIN');
        const nuevoRol = esAdmin ? 'VECINO' : 'ADMINISTRADOR'; 
        
        if (!window.confirm(`¿Cambiar el rol de "${u.nombre}" a ${nuevoRol}?`)) return;

        try {
            await usuarioService.cambiarRol(u.id, nuevoRol);
            alert(`Rol de ${u.nombre} actualizado a ${nuevoRol}`);
            cargar(); // Recargar la lista
        } catch (e) {
            alert("Error al cambiar rol");
        }
    };

    // Función: Eliminar Usuario
    const eliminarUsuario = async (id: number, nombre: string) => {
        // Confirmación de seguridad
        if (!window.confirm(`⚠️ ¿Estás seguro de ELIMINAR al usuario "${nombre}"?\n\nEsta acción borrará sus datos y no podrá deshacerse.`)) return;

        try {
            await usuarioService.eliminar(id);
            // Actualizamos el estado local quitando el usuario borrado (más rápido que recargar todo)
            setUsuarios(prev => prev.filter(user => user.id !== id));
            alert(`Usuario "${nombre}" eliminado correctamente.`);
        } catch (e) {
            console.error(e);
            alert("Error al eliminar. Puede que el usuario tenga datos asociados (noticias, solicitudes) que impiden el borrado.");
        }
    };

    if (loading) return <div className="p-10 text-center text-gray-500">Cargando usuarios...</div>;

    return (
        <div className="space-y-6">
            <header className="flex justify-between items-center border-b pb-4">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 flex items-center">
                        <Users className="mr-3 text-blue-600" /> Gestión de Usuarios
                    </h2>
                    <p className="text-gray-500 text-sm mt-1">Administra los permisos y cuentas de la comunidad.</p>
                </div>
                <div className="bg-blue-50 text-blue-800 px-4 py-2 rounded-lg font-bold">
                    Total: {usuarios.length}
                </div>
            </header>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {usuarios.map(u => {
                    const esAdmin = u.rol.toUpperCase().includes('ADMIN');
                    
                    return (
                        <div key={u.id} className={`relative group p-6 rounded-xl border shadow-sm flex flex-col items-center text-center transition-all duration-200 hover:shadow-md ${
                            esAdmin ? 'bg-orange-50 border-orange-200' : 'bg-white border-gray-200'
                        }`}>
                            
                            {/* --- BOTÓN ELIMINAR (Esquina Superior Derecha) --- */}
                            <button 
                                onClick={() => eliminarUsuario(u.id, u.nombre)}
                                className="absolute top-3 right-3 p-2 text-gray-300 hover:text-red-600 hover:bg-red-50 rounded-full transition-colors opacity-0 group-hover:opacity-100 focus:opacity-100"
                                title="Eliminar usuario permanentemente"
                            >
                                <Trash2 size={18} />
                            </button>
                            {/* ------------------------------------------------ */}

                            <div className={`w-16 h-16 rounded-full flex items-center justify-center mb-4 text-2xl font-bold shadow-sm ${
                                esAdmin ? 'bg-orange-100 text-orange-600' : 'bg-blue-100 text-blue-600'
                            }`}>
                                {u.nombre.charAt(0).toUpperCase()}
                            </div>
                            
                            <h3 className="font-bold text-gray-900 text-lg">{u.nombre}</h3>
                            <p className="text-sm text-gray-500 mb-1">{u.email}</p>
                            
                            <span className={`text-xs px-2 py-1 rounded-full font-bold mb-6 ${
                                esAdmin ? 'bg-orange-200 text-orange-800' : 'bg-gray-100 text-gray-600'
                            }`}>
                                {u.rol}
                            </span>

                            <div className="mt-auto w-full">
                                <button
                                    onClick={() => toggleRol(u)}
                                    className={`w-full py-2 px-4 rounded-lg flex items-center justify-center space-x-2 font-medium transition-colors ${
                                        esAdmin 
                                        ? 'bg-white text-orange-600 border border-orange-200 hover:bg-orange-100' 
                                        : 'bg-gray-50 text-gray-700 hover:bg-gray-200 border border-transparent'
                                    }`}
                                >
                                    {esAdmin ? <ShieldAlert size={16} /> : <Shield size={16} />}
                                    <span>{esAdmin ? 'Degradar a Vecino' : 'Ascender a Admin'}</span>
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>
            
            {usuarios.length === 0 && (
                <div className="text-center py-10 text-gray-400 bg-gray-50 rounded-lg border border-dashed border-gray-300">
                    No hay usuarios registrados en el sistema.
                </div>
            )}
        </div>
    );
};