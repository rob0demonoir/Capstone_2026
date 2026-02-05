import { useEffect, useState } from 'react';
import { noticiaService } from '../services/noticiaService';
import { authService } from '../../auth/services/authService';
import type { Noticia } from '../../../types';
import { Calendar, User, Plus, ImageIcon, Newspaper } from 'lucide-react';
import { formatearFecha } from '../../../utils/dateUtils';
import { NuevaNoticiaModal } from './NuevaNoticiaModal';
import { VerNoticiaModal } from './VerNoticiaModal'// <--- IMPORTACIÓN NUEVA

export const NoticiasList = () => {
    const [noticias, setNoticias] = useState<Noticia[]>([]);
    const [loading, setLoading] = useState(true);
    
    // Estados para los modales
    const [isModalOpen, setIsModalOpen] = useState(false); // Crear
    const [noticiaSeleccionada, setNoticiaSeleccionada] = useState<Noticia | null>(null); // Ver Detalle

    const isAdmin = authService.isAdmin();

    const cargarNoticias = async () => {
        try {
            const data = await noticiaService.obtenerTodas();
            setNoticias(data);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarNoticias();
    }, []);

    if (loading) return (
        <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600"></div>
        </div>
    );

    return (
        <div className="space-y-8">
            {/* HEADER DE LA SECCIÓN */}
            <div className="flex flex-col md:flex-row justify-between items-center gap-4 bg-teal-50 p-6 rounded-2xl border border-teal-100">
                <div>
                    <h2 className="text-3xl font-extrabold text-teal-900">Noticias Publicadas</h2>
                    <p className="text-teal-600 mt-1">Entérate de lo que pasa en tu barrio.</p>
                </div>
                
                {isAdmin && (
                    <button 
                        onClick={() => setIsModalOpen(true)}
                        className="flex items-center space-x-2 bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-xl font-bold shadow-lg shadow-orange-500/30 transition-all transform hover:-translate-y-1 active:scale-95"
                    >
                        <Plus size={20} />
                        <span>Publicar Noticia</span>
                    </button>
                )}
            </div>

            {/* GRID DE NOTICIAS */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                {noticias.map((n) => (
                    <article 
                        key={n.id} 
                        onClick={() => setNoticiaSeleccionada(n)} // <--- CLICK PARA ABRIR
                        className="bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 border border-slate-100 overflow-hidden flex flex-col h-full group cursor-pointer hover:-translate-y-1"
                    >
                        {/* IMAGEN DE CABECERA */}
                        <div className="h-48 w-full bg-slate-200 relative overflow-hidden">
                            {n.urlImagen ? (
                                <img 
                                    src={`http://192.168.100.19:8082${n.urlImagen}`} 
                                    alt={n.titulo}
                                    className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                                    onError={(e) => {
                                        (e.target as HTMLImageElement).src = 'https://placehold.co/400x200?text=Sin+Foto';
                                    }}
                                />
                            ) : (
                                <div className="w-full h-full flex items-center justify-center text-slate-400 bg-slate-100">
                                    <ImageIcon size={48} opacity={0.5} />
                                </div>
                            )}
                            <div className="absolute top-0 right-0 bg-teal-600 text-white text-xs font-bold px-3 py-1 rounded-bl-lg">
                                OFICIAL
                            </div>
                        </div>

                        {/* CONTENIDO */}
                        <div className="p-6 flex-1 flex flex-col">
                            <h3 className="text-xl font-bold text-slate-800 mb-3 leading-tight group-hover:text-teal-700 transition-colors">
                                {n.titulo}
                            </h3>
                            
                            <p className="text-slate-600 text-sm mb-6 line-clamp-3 flex-1">
                                {n.contenido}
                            </p>

                            {/* FOOTER DE LA CARD */}
                            <div className="flex items-center justify-between pt-4 border-t border-slate-100 text-xs text-slate-500 mt-auto">
                                <div className="flex items-center space-x-2">
                                    <Calendar size={14} className="text-teal-500" />
                                    <span>{formatearFecha(n.fechaPublicacion || n.fecha)}</span>
                                </div>
                                <div className="flex items-center space-x-2" title="Autor">
                                    <User size={14} className="text-orange-500" />
                                    <span className="font-medium truncate max-w-[100px]">
                                        {typeof n.autor === 'object' ? n.autor.nombre : 'Admin'}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </article>
                ))}
            </div>

            {noticias.length === 0 && (
                <div className="text-center py-20 bg-white rounded-3xl border-2 border-dashed border-slate-200">
                    <Newspaper size={48} className="mx-auto text-slate-300 mb-4" />
                    <p className="text-slate-500 text-lg">No hay noticias publicadas aún.</p>
                </div>
            )}

            {/* MODAL CREAR (ADMIN) */}
            {isModalOpen && (
                <NuevaNoticiaModal 
                    isOpen={isModalOpen} 
                    onClose={() => setIsModalOpen(false)} 
                    onSuccess={cargarNoticias} 
                />
            )}

            {/* MODAL VER DETALLE (TODOS) */}
            {noticiaSeleccionada && (
                <VerNoticiaModal 
                    noticia={noticiaSeleccionada} 
                    onClose={() => setNoticiaSeleccionada(null)} 
                />
            )}
        </div>
    );
};