import React, { useState } from 'react';
import { X, Image as ImageIcon, Upload, Loader } from 'lucide-react';
import { noticiaService } from '../services/noticiaService';

interface Props {
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

export const NuevaNoticiaModal = ({ isOpen, onClose, onSuccess }: Props) => {
    const [titulo, setTitulo] = useState('');
    const [contenido, setContenido] = useState('');
    const [imagen, setImagen] = useState<File | null>(null);
    const [previewUrl, setPreviewUrl] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    if (!isOpen) return null;

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setImagen(file);
            setPreviewUrl(URL.createObjectURL(file)); // Para mostrar la foto antes de subirla
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            // Enviamos los datos al servicio
            await noticiaService.crear(titulo, contenido, imagen);
            onSuccess(); // Recargar la lista de noticias
            onClose();   // Cerrar modal
        } catch (error) {
            console.error("Error al publicar:", error);
            alert("Hubo un error al publicar la noticia.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-all">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in duration-200">
                
                {/* HEADER (TEAL) */}
                <div className="bg-teal-900 p-4 flex justify-between items-center text-white">
                    <h3 className="text-lg font-bold flex items-center gap-2">
                        <span className="bg-orange-500 p-1 rounded-md"><ImageIcon size={16} /></span>
                        Nueva Publicación
                    </h3>
                    <button onClick={onClose} className="text-teal-200 hover:text-white transition-colors">
                        <X size={24} />
                    </button>
                </div>

                {/* FORMULARIO */}
                <form onSubmit={handleSubmit} className="p-6 space-y-5">
                    
                    {/* TÍTULO */}
                    <div>
                        <label className="block text-sm font-bold text-slate-700 mb-1">Título</label>
                        <input 
                            type="text" 
                            className="w-full px-4 py-2 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent transition-all"
                            placeholder="Ej: Reunión de Junta de Vecinos"
                            value={titulo}
                            onChange={(e) => setTitulo(e.target.value)}
                            required
                        />
                    </div>

                    {/* CONTENIDO */}
                    <div>
                        <label className="block text-sm font-bold text-slate-700 mb-1">Contenido</label>
                        <textarea 
                            className="w-full px-4 py-2 border border-slate-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent transition-all h-32 resize-none"
                            placeholder="Describe los detalles..."
                            value={contenido}
                            onChange={(e) => setContenido(e.target.value)}
                            required
                        />
                    </div>

                    {/* SUBIDA DE IMAGEN */}
                    <div>
                        <label className="block text-sm font-bold text-slate-700 mb-2">Fotografía (Opcional)</label>
                        
                        <div className="relative group">
                            <input 
                                type="file" 
                                accept="image/*"
                                onChange={handleImageChange}
                                className="hidden" 
                                id="file-upload"
                            />
                            
                            <label 
                                htmlFor="file-upload"
                                className={`flex flex-col items-center justify-center w-full h-40 border-2 border-dashed rounded-xl cursor-pointer transition-all ${
                                    previewUrl ? 'border-teal-500 bg-teal-50' : 'border-slate-300 hover:border-teal-400 hover:bg-slate-50'
                                }`}
                            >
                                {previewUrl ? (
                                    <div className="relative w-full h-full">
                                        <img 
                                            src={previewUrl} 
                                            alt="Preview" 
                                            className="w-full h-full object-cover rounded-xl opacity-80 group-hover:opacity-100 transition-opacity" 
                                        />
                                        <div className="absolute inset-0 flex items-center justify-center bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity rounded-xl text-white font-bold">
                                            Cambiar Imagen
                                        </div>
                                    </div>
                                ) : (
                                    <div className="flex flex-col items-center text-slate-400">
                                        <Upload size={32} className="mb-2" />
                                        <span className="text-sm font-medium">Click para subir imagen</span>
                                    </div>
                                )}
                            </label>
                        </div>
                    </div>

                    {/* BOTONES */}
                    <div className="pt-4 flex justify-end gap-3">
                        <button 
                            type="button" 
                            onClick={onClose}
                            className="px-5 py-2.5 rounded-xl text-slate-600 font-bold hover:bg-slate-100 transition-colors"
                        >
                            Cancelar
                        </button>
                        
                        <button 
                            type="submit"
                            disabled={loading}
                            className={`bg-orange-500 hover:bg-orange-600 text-white px-6 py-2.5 rounded-xl font-bold shadow-lg shadow-orange-500/30 transition-all flex items-center gap-2 ${
                                loading ? 'opacity-70 cursor-wait' : ''
                            }`}
                        >
                            {loading && <Loader size={18} className="animate-spin" />}
                            <span>{loading ? 'Publicando...' : 'Publicar Noticia'}</span>
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
};