import { X, Calendar, User, Image as ImageIcon } from 'lucide-react';
import type { Noticia } from '../../../types';
import { formatearFecha } from '../../../utils/dateUtils';

interface Props {
    noticia: Noticia | null;
    onClose: () => void;
}

export const VerNoticiaModal = ({ noticia, onClose }: Props) => {
    if (!noticia) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-sm animate-in fade-in duration-200">
            {/* Contenedor Modal */}
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-hidden flex flex-col relative">
                
                {/* Botón Cerrar Flotante */}
                <button 
                    onClick={onClose}
                    className="absolute top-4 right-4 z-10 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors backdrop-blur-md"
                >
                    <X size={24} />
                </button>

                {/* IMAGEN GRANDE */}
                <div className="w-full h-64 md:h-80 bg-slate-200 relative flex-shrink-0">
                    {noticia.urlImagen ? (
                        <img 
                            src={`http://192.168.100.19:8082${noticia.urlImagen}`} 
                            alt={noticia.titulo}
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-400">
                            <ImageIcon size={64} opacity={0.5} />
                            <span className="ml-2 font-medium">Sin imagen</span>
                        </div>
                    )}
                </div>

                {/* CONTENIDO SCROLLEABLE */}
                <div className="p-8 overflow-y-auto custom-scrollbar">
                    {/* Metadatos */}
                    <div className="flex items-center space-x-4 text-sm text-slate-500 mb-4">
                        <div className="flex items-center space-x-1 bg-teal-50 text-teal-700 px-3 py-1 rounded-full">
                            <Calendar size={14} />
                            <span>{formatearFecha(noticia.fechaPublicacion || noticia.fecha)}</span>
                        </div>
                        <div className="flex items-center space-x-1">
                            <User size={14} />
                            <span>{typeof noticia.autor === 'object' ? noticia.autor.nombre : 'Admin'}</span>
                        </div>
                    </div>

                    <h2 className="text-2xl md:text-3xl font-bold text-slate-900 mb-6 leading-tight">
                        {noticia.titulo}
                    </h2>

                    {/* whitespace-pre-wrap respeta los saltos de línea que puso el usuario */}
                    <div className="prose prose-slate max-w-none text-slate-700 whitespace-pre-wrap leading-relaxed">
                        {noticia.contenido}
                    </div>
                </div>

                {/* Footer simple */}
                <div className="p-4 border-t border-slate-100 bg-slate-50 flex justify-end">
                    <button 
                        onClick={onClose}
                        className="px-6 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 rounded-xl font-bold transition-colors"
                    >
                        Cerrar
                    </button>
                </div>
            </div>
        </div>
    );
};