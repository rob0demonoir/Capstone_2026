import React, { useEffect, useState } from 'react';
import { avisoService } from '../services/avisoService';
import { AvisoForm } from './AvisoForm';
import type { AvisoResponse, TipoAviso } from '../../../types';
import { Trash2, Phone, Tag, DollarSign, Calendar, Megaphone } from 'lucide-react';

export const AvisosList = () => {
    const [avisos, setAvisos] = useState<AvisoResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [filtro, setFiltro] = useState<TipoAviso | 'TODOS'>('TODOS');

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
            cargarAvisos(); // Recargar la lista tras eliminar
        } catch (error) {
            alert("No tienes permiso para eliminar este aviso o hubo un error en el servidor.");
        }
    };

    useEffect(() => {
        cargarAvisos();
    }, []);

    // Lógica de filtrado en el frontend
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
                    <h2 className="text-2xl font-bold text-gray-800">Mercadito Vecinal</h2>
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
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {avisosFiltrados.map((a) => (
                        <div key={a.id} className="bg-white rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow p-5 flex flex-col">
                            <div className="flex justify-between items-start mb-3">
                                <span className={`text-[10px] font-black px-2 py-1 rounded-full ${
                                    a.tipo === 'VENTA' ? 'bg-green-100 text-green-700' :
                                    a.tipo === 'SERVICIO' ? 'bg-blue-100 text-blue-700' :
                                    'bg-purple-100 text-purple-700'
                                }`}>
                                    {a.tipo}
                                </span>
                                {a.esMio && (
                                    <button 
                                        onClick={() => eliminarAviso(a.id)}
                                        className="text-gray-300 hover:text-red-500 transition-colors"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                )}
                            </div>

                            <h3 className="text-lg font-bold text-gray-900 mb-1">{a.titulo}</h3>
                            <p className="text-gray-600 text-sm mb-4 line-clamp-2">{a.descripcion}</p>

                            <div className="mt-auto space-y-3">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center text-blue-700 font-black text-xl">
                                        <DollarSign size={20} />
                                        <span>{a.precio ? a.precio.toLocaleString('es-CL') : '0'}</span>
                                    </div>
                                    <div className="flex items-center text-gray-500 text-xs">
                                        <Calendar size={14} className="mr-1" />
                                        {new Date(a.fechaPublicacion).toLocaleDateString()}
                                    </div>
                                </div>

                                <div className="flex items-center justify-between pt-3 border-t border-gray-50 text-xs">
                                    <div className="flex items-center text-gray-700">
                                        <div className="w-6 h-6 bg-orange-100 text-orange-600 rounded-full flex items-center justify-center mr-2 font-bold">
                                            {a.nombrePublicador.charAt(0)}
                                        </div>
                                        {a.nombrePublicador}
                                    </div>
                                    <a 
                                        href={`tel:${a.telefonoContacto}`} 
                                        className="flex items-center bg-green-50 text-green-700 px-3 py-1 rounded-full hover:bg-green-100 transition-colors"
                                    >
                                        <Phone size={12} className="mr-1" />
                                        {a.telefonoContacto}
                                    </a>
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
        </div>
    );
};