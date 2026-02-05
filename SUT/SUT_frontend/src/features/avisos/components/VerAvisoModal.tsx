import { X, Calendar, User, Image as ImageIcon, } from 'lucide-react';
// IMPORTANTE: Ajusta la ruta de importación según donde tengas tus types
import type { AvisoResponse } from '../../../types'; 
import { formatearFecha } from '../../../utils/dateUtils';

interface Props {
    // Aquí usamos el tipo correcto
    aviso: AvisoResponse | null;
    onClose: () => void;
}

export const VerAvisoModal = ({ aviso, onClose }: Props) => {
    if (!aviso) return null;

    // Helper para obtener el nombre del publicador de forma segura
    const nombrePublicador = typeof aviso.nombrePublicador === 'object' && aviso.nombrePublicador 
        ? (aviso.nombrePublicador as any).nombre 
        : 'Vecino';

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/70 backdrop-blur-sm animate-in fade-in duration-200">
            
            {/* Contenedor Principal */}
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-hidden flex flex-col relative">
                
                {/* Botón Cerrar (Flotante sobre la imagen) */}
                <button 
                    onClick={onClose}
                    className="absolute top-4 right-4 z-10 bg-black/50 hover:bg-black/70 text-white p-2 rounded-full transition-colors backdrop-blur-md"
                >
                    <X size={24} />
                </button>

                {/* IMAGEN DE CABECERA */}
                <div className="w-full h-64 md:h-80 bg-slate-200 relative flex-shrink-0">
                    {aviso.urlImagen ? (
                        <img 
                            src={`http://192.168.100.19:8082${aviso.urlImagen}`} 
                            alt={aviso.titulo}
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-400">
                            <ImageIcon size={64} opacity={0.5} />
                            <span className="ml-2 font-medium">Sin imagen</span>
                        </div>
                    )}
                    
                    {/* Badge de Categoría (Si tuvieras tipos de aviso) */}
                    <div className="absolute top-0 left-0 bg-orange-500 text-white text-xs font-bold px-3 py-1 rounded-br-lg uppercase tracking-wide">
                        AVISO VECINAL
                    </div>
                </div>

                {/* CONTENIDO SCROLLEABLE */}
                <div className="p-8 overflow-y-auto custom-scrollbar">
                    
                    {/* Metadatos (Fecha y Autor) */}
                    <div className="flex flex-wrap gap-3 mb-6 text-sm text-slate-500">
                        <div className="flex items-center space-x-1 bg-teal-50 text-teal-700 px-3 py-1 rounded-full border border-teal-100">
                            <Calendar size={14} />
                            <span>{formatearFecha(aviso.fechaPublicacion)}</span>
                        </div>
                        <div className="flex items-center space-x-1 bg-slate-100 text-slate-700 px-3 py-1 rounded-full border border-slate-200">
                            <User size={14} />
                            <span className="font-medium">{nombrePublicador}</span>
                        </div>
                    </div>

                    {/* Título */}
                    <h2 className="text-2xl md:text-3xl font-bold text-slate-900 mb-4 leading-tight">
                        {aviso.titulo}
                    </h2>

                    {/* Descripción con formato preservado */}
                    <div className="prose prose-slate max-w-none text-slate-700 whitespace-pre-wrap leading-relaxed text-lg">
                        {aviso.descripcion}
                    </div>

                    {/* Sección de Contacto (Opcional, si tienes estos campos en el futuro) */}
                    {/* <div className="mt-8 pt-6 border-t border-slate-100">
                        <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-3">Información de Contacto</h4>
                        <div className="flex gap-4">
                            <button className="flex items-center gap-2 bg-teal-600 text-white px-4 py-2 rounded-lg font-bold hover:bg-teal-700 transition-colors">
                                Contactar al Vecino
                            </button>
                        </div>
                    </div> 
                    */}
                </div>

                {/* Footer */}
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