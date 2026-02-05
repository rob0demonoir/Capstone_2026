import { useEffect, useState } from 'react';
import { avisoService } from '../services/avisoService';
import { AvisoForm } from './AvisoForm';
import type { AvisoResponse, TipoAviso } from '../../../types';
import { formatearFecha } from '../../../utils/dateUtils';
import { Trash2, Phone, DollarSign, Calendar, Megaphone, Image as ImageIcon } from 'lucide-react';
// 1. IMPORTAMOS EL MODAL
import { VerAvisoModal } from './VerAvisoModal';

export const AvisosList = () => {
    const [avisos, setAvisos] = useState<AvisoResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [filtro, setFiltro] = useState<TipoAviso | 'TODOS'>('TODOS');
    
    // 2. ESTADO PARA EL MODAL DE DETALLE
    const [avisoSeleccionado, setAvisoSeleccionado] = useState<AvisoResponse | null>(null);

    const cargarAvisos = async () => {
        try {
            setLoading(true);
            const data = await avisoService.obtenerTodos();
            setAvisos(data);
        } catch (error) {
            console.error("Error al cargar avisos:", error);
        } finally {
            setLoading(false);
        }
    };

    const eliminarAviso = async (id: number) => {
        if (!window.confirm("¿Estás seguro de que quieres eliminar este aviso?")) return;
        try {
            await avisoService.eliminar(id);
            cargarAvisos(); 
        } catch (error) {
            alert("No tienes permiso para eliminar este aviso o hubo un error en el servidor.");
        }
    };

    useEffect(() => {
        cargarAvisos();
    }, []);

    const avisosFiltrados = filtro === 'TODOS' 
        ? avisos 
        : avisos.filter(a => a.tipo === filtro);

    return (
        <div className="space-y-8">
            {/* Formulario de creación */}
            <AvisoForm onAvisoCreado={cargarAvisos} />

            {/* Encabezado y Filtros */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b pb-4">
                <div className="flex items-center space-x-2">
                    <Megaphone className="text-orange-600" size={28} />
                    <h2 className="text-2xl font-bold text-gray-800">Tablón de la Comunidad</h2>
                </div>
                
                <div className="flex items-center space-x-2 bg-gray-100 p-1 rounded-lg">
                    {['TODOS', 'VENTA', 'SERVICIO', 'EVENTO', 'BUSCO'].map((t) => (
                        <button
                            key={t}
                            onClick={() => setFiltro(t as any)}
                            className={`px-3 py-1 text-xs font-bold rounded-md transition-all ${
                                filtro === t ? 'bg-white shadow-sm text-orange-600' : 'text-gray-500 hover:text-gray-700'
                            }`}
                        >
                            {t}
                        </button>
                    ))}
                </div>
            </div>

            {/* Listado de Avisos */}
            {loading ? (
                <div className="text-center py-10">Cargando avisos...</div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {avisosFiltrados.map((a) => (
                        <div 
                            key={a.id} 
                            // 3. EVENTO CLICK PARA ABRIR MODAL
                            onClick={() => setAvisoSeleccionado(a)}
                            // Agregamos cursor-pointer para indicar que es clickeable
                            className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-all hover:-translate-y-1 cursor-pointer flex flex-col overflow-hidden"
                        >
                            
                            {/* PARTE SUPERIOR: TIPO Y BORRAR */}
                            <div className="p-4 flex justify-between items-start pb-2">
                                <span className={`text-[10px] font-black px-2 py-1 rounded-full ${
                                    a.tipo === 'VENTA' ? 'bg-green-100 text-green-700' :
                                    a.tipo === 'SERVICIO' ? 'bg-blue-100 text-blue-700' :
                                    'bg-purple-100 text-purple-700'
                                }`}>
                                    {a.tipo}
                                </span>
                                {a.esMio && (
                                    <button 
                                        onClick={(e) => {
                                            // 4. IMPORTANTE: Evita que se abra el modal al borrar
                                            e.stopPropagation(); 
                                            eliminarAviso(a.id);
                                        }}
                                        className="text-gray-300 hover:text-red-500 transition-colors p-1"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                )}
                            </div>

                            {/* BLOQUE DE IMAGEN */}
                            <div className="h-48 w-full bg-gray-50 relative border-y border-gray-100">
                                {a.urlImagen ? (
                                    <img 
                                        src={`http://192.168.100.19:8082${a.urlImagen}`} 
                                        alt={a.titulo} 
                                        className="w-full h-full object-cover"
                                        onError={(e) => {
                                            (e.target as HTMLImageElement).src = 'https://placehold.co/400x200?text=Sin+Foto';
                                        }}
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-gray-300">
                                        <ImageIcon size={48} />
                                    </div>
                                )}
                            </div>

                            <div className="p-5 pt-3 flex flex-col flex-1">
                                <h3 className="text-lg font-bold text-gray-900 mb-1 line-clamp-1">{a.titulo}</h3>
                                <p className="text-gray-600 text-sm mb-4 line-clamp-2 flex-1">{a.descripcion}</p>

                                <div className="mt-auto space-y-3">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center text-blue-700 font-black text-xl">
                                            <DollarSign size={20} />
                                            <span>{a.precio ? a.precio.toLocaleString('es-CL') : '0'}</span>
                                        </div>
                                        <div className="flex items-center text-gray-500 text-xs">
                                            <Calendar size={14} className="mr-1" />
                                            {formatearFecha(a.fechaPublicacion)}
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between pt-3 border-t border-gray-50 text-xs">
                                        <div className="flex items-center text-gray-700">
                                            <div className="w-6 h-6 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center mr-2 font-bold uppercase">
                                                {a.nombrePublicador.charAt(0)}
                                            </div>
                                            <span className="truncate max-w-[100px]">{a.nombrePublicador}</span>
                                        </div>
                                        <a 
                                            href={`tel:${a.telefonoContacto}`} 
                                            // 4. IMPORTANTE: Evita que se abra el modal al llamar
                                            onClick={(e) => e.stopPropagation()}
                                            className="flex items-center bg-green-50 text-green-700 px-3 py-1 rounded-full hover:bg-green-100 transition-colors"
                                        >
                                            <Phone size={12} className="mr-1" />
                                            {a.telefonoContacto}
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {!loading && avisosFiltrados.length === 0 && (
                <div className="text-center py-20 text-gray-400 italic">
                    No hay avisos en esta categoría.
                </div>
            )}

            {/* 5. RENDERIZADO DEL MODAL */}
            {avisoSeleccionado && (
                <VerAvisoModal 
                    aviso={avisoSeleccionado} 
                    onClose={() => setAvisoSeleccionado(null)} 
                />
            )}
        </div>
    );
};