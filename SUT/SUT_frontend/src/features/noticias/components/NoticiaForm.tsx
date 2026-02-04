import React, { useState } from 'react';
import { noticiaService } from '../services/noticiaService';
import { PlusCircle, Send } from 'lucide-react';

interface Props {
    onNoticiaCreada: () => void; // Para avisarle a la lista que se actualice
}

export const NoticiaForm = ({ onNoticiaCreada }: Props) => {
    const [titulo, setTitulo] = useState('');
    const [contenido, setContenido] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            await noticiaService.crear({ titulo, contenido });
            setTitulo('');
            setContenido('');
            onNoticiaCreada(); // Refrescar la lista
            alert("¡Noticia publicada con éxito!");
        } catch (error) {
            console.error("Error al publicar", error);
            alert("Error al publicar la noticia");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 mb-8">
            <div className="flex items-center space-x-2 mb-4 text-blue-600">
                <PlusCircle size={24} />
                <h3 className="text-lg font-bold">Publicar Nueva Noticia</h3>
            </div>
            
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <input 
                        type="text" 
                        placeholder="Título de la noticia"
                        className="w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
                        value={titulo}
                        onChange={(e) => setTitulo(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <textarea 
                        placeholder="Escribe el contenido aquí..."
                        className="w-full p-2 border rounded-md h-32 focus:ring-2 focus:ring-blue-500 outline-none"
                        value={contenido}
                        onChange={(e) => setContenido(e.target.value)}
                        required
                    />
                </div>
                <button 
                    type="submit" 
                    disabled={loading}
                    className="flex items-center justify-center space-x-2 bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition disabled:bg-gray-400"
                >
                    <Send size={18} />
                    <span>{loading ? 'Publicando...' : 'Publicar Noticia'}</span>
                </button>
            </form>
        </div>
    );
};