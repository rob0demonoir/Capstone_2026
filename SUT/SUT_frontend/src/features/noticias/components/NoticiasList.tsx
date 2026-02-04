import React, { useEffect, useState } from 'react';
import { noticiaService } from '../services/noticiaService';
import { NoticiaForm } from './NoticiaForm';
import type { NoticiaResponse } from '../../../types';
import { Calendar, User, Image as ImageIcon, Newspaper } from 'lucide-react';

export const NoticiasList = () => {
    const [noticias, setNoticias] = useState<NoticiaResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Función para cargar noticias (la definimos aparte para poder reutilizarla)
    const cargarNoticias = async () => {
        try {
            setLoading(true);
            const data = await noticiaService.obtenerTodas();
            setNoticias(data);
            setError(null);
        } catch (err) {
            console.error("Error al cargar noticias:", err);
            setError("No se pudieron cargar las noticias. Revisa la conexión con el servidor.");
        } finally {
            setLoading(false);
        }
    };

    // Se ejecuta una sola vez al montar el componente
    useEffect(() => {
        cargarNoticias();
    }, []);

    return (
        <div className="max-w-6xl mx-auto space-y-8">
            {/* Formulario para crear noticias (se le pasa la función de recarga como prop) */}
            <section>
                <NoticiaForm onNoticiaCreada={cargarNoticias} />
            </section>

            {/* Encabezado de la lista */}
            <div className="flex items-center space-x-3 border-b pb-4">
                <Newspaper className="text-blue-600" size={28} />
                <h2 className="text-2xl font-bold text-gray-800">Muro de Noticias Municipales</h2>
            </div>

            {/* Estado de carga o error */}
            {loading && noticias.length === 0 && (
                <div className="text-center py-10 text-gray-500">Cargando noticias reales desde el servidor...</div>
            )}

            {error && (
                <div className="bg-red-50 text-red-600 p-4 rounded-lg border border-red-200">
                    {error}
                </div>
            )}

            {/* Grid de Noticias */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {noticias.length > 0 ? (
                    noticias.map((n) => (
                        <article 
                            key={n.id} 
                            className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden flex flex-col hover:shadow-md transition-shadow"
                        >
                            {/* Imagen de la noticia */}
                            <div className="h-48 w-full bg-gray-100">
                                {n.urlImagen ? (
                                    <img 
                                        src={`http://localhost:8082${n.urlImagen}`} 
                                        alt={n.titulo} 
                                        className="w-full h-full object-cover"
                                        onError={(e) => {
                                            // Si la imagen falla (ej. el server no la encuentra), ponemos un placeholder
                                            (e.target as HTMLImageElement).src = 'https://via.placeholder.com/400x200?text=Sin+Imagen';
                                        }}
                                    />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-gray-400">
                                        <ImageIcon size={48} />
                                    </div>
                                )}
                            </div>

                            {/* Contenido de la noticia */}
                            <div className="p-5 flex-1 flex flex-col">
                                <h3 className="text-lg font-bold text-gray-900 mb-2 line-clamp-2">{n.titulo}</h3>
                                <p className="text-gray-600 text-sm line-clamp-4 mb-4 flex-1">
                                    {n.contenido}
                                </p>

                                {/* Meta data (Autor y Fecha) */}
                                <div className="pt-4 border-t border-gray-100 space-y-2">
                                    <div className="flex items-center text-xs text-gray-500 space-x-2">
                                        <User size={14} className="text-blue-500" />
                                        <span className="font-medium">{n.autor}</span>
                                    </div>
                                    <div className="flex items-center text-xs text-gray-400 space-x-2">
                                        <Calendar size={14} />
                                        <span>{new Date(n.fecha).toLocaleDateString('es-CL', {
                                            day: '2-digit',
                                            month: 'long',
                                            year: 'numeric'
                                        })}</span>
                                    </div>
                                </div>
                            </div>
                        </article>
                    ))
                ) : (
                    !loading && <div className="col-span-full text-center py-20 text-gray-400">No hay noticias publicadas todavía.</div>
                )}
            </div>
        </div>
    );
};