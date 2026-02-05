import React, { useState } from 'react';
import { avisoService } from '../services/avisoService';
import { Megaphone, Image as ImageIcon, X, DollarSign } from 'lucide-react';
import type { TipoAviso } from '../../../types';

interface Props { onAvisoCreado: () => void; }

export const AvisoForm = ({ onAvisoCreado }: Props) => {
    // Estados del formulario
    const [titulo, setTitulo] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [precio, setPrecio] = useState(''); // Usamos string para el input
    const [tipo, setTipo] = useState<TipoAviso>('VENTA');
    const [imagen, setImagen] = useState<File | null>(null); // Estado para el archivo real
    const [loading, setLoading] = useState(false);

    // Manejador de selección de archivo
    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setImagen(e.target.files[0]);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            // 1. CREAMOS EL FORM DATA
            const formData = new FormData();
            formData.append('titulo', titulo);
            formData.append('descripcion', descripcion);
            formData.append('precio', precio || '0'); // Enviamos 0 si está vacío
            formData.append('tipo', tipo);
            
            // 2. SOLO SI HAY IMAGEN, la agregamos
            if (imagen) {
                // 'imagen' debe llamarse igual que en tu @RequestParam("imagen") de Kotlin
                formData.append('imagen', imagen); 
            }

            // 3. ENVIAMOS AL SERVICIO
            await avisoService.crear(formData);
            
            // 4. LIMPIEZA
            setTitulo(''); 
            setDescripcion(''); 
            setPrecio(''); 
            setImagen(null);
            
            // 5. REFRESCO
            onAvisoCreado();
            alert("¡Aviso publicado correctamente!");
        } catch (error) {
            console.error(error);
            alert("Error al subir el aviso. Intenta con una imagen más pequeña.");
        } finally { 
            setLoading(false); 
        }
    };

    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-orange-100 mb-8">
            <div className="flex items-center space-x-2 mb-4 text-orange-600">
                <Megaphone size={24} />
                <h3 className="text-lg font-bold">Publicar Aviso Vecinal</h3>
            </div>
            
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Título */}
                <div className="col-span-2 md:col-span-1">
                    <label className="block text-xs font-bold text-gray-500 mb-1">Título</label>
                    <input 
                        className="w-full p-2 border rounded-md outline-none focus:ring-2 focus:ring-orange-500"
                        placeholder="Ej: Vendo bicicleta aro 29"
                        value={titulo} onChange={(e) => setTitulo(e.target.value)} required
                    />
                </div>

                {/* Tipo */}
                <div>
                    <label className="block text-xs font-bold text-gray-500 mb-1">Tipo de Aviso</label>
                    <select 
                        className="w-full p-2 border rounded-md outline-none bg-white"
                        value={tipo} onChange={(e) => setTipo(e.target.value as TipoAviso)}
                    >
                        <option value="VENTA">Venta</option>
                        <option value="SERVICIO">Servicio</option>
                        <option value="EVENTO">Evento</option>
                        <option value="BUSCO">Busco</option>
                    </select>
                </div>

                {/* Precio */}
                <div className="col-span-2 md:col-span-1 relative">
                     <label className="block text-xs font-bold text-gray-500 mb-1">Precio (Opcional)</label>
                    <div className="relative">
                        <DollarSign className="absolute left-2 top-2.5 text-gray-400" size={18} />
                        <input 
                            type="number" className="w-full pl-8 p-2 border rounded-md outline-none focus:ring-2 focus:ring-orange-500"
                            placeholder="0"
                            value={precio} onChange={(e) => setPrecio(e.target.value)}
                        />
                    </div>
                </div>

                {/* INPUT DE IMAGEN (El cambio visual importante) */}
                <div className="col-span-2 md:col-span-1">
                    <label className="block text-xs font-bold text-gray-500 mb-1">Fotografía</label>
                    <div className="border border-dashed border-gray-300 rounded-lg p-2 flex items-center justify-center bg-gray-50 hover:bg-gray-100 transition cursor-pointer relative h-[42px]">
                        <input 
                            type="file" 
                            accept="image/*"
                            onChange={handleImageChange}
                            className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                        />
                        {imagen ? (
                            <div className="flex items-center text-green-600 text-sm font-medium z-10 truncate px-2">
                                <ImageIcon size={16} className="mr-2 flex-shrink-0" />
                                <span className="truncate">{imagen.name}</span>
                                <button 
                                    type="button" 
                                    onClick={(e) => { e.stopPropagation(); setImagen(null); }}
                                    className="ml-2 text-red-500 hover:text-red-700"
                                >
                                    <X size={16} />
                                </button>
                            </div>
                        ) : (
                            <div className="text-gray-400 flex items-center text-sm pointer-events-none">
                                <ImageIcon size={16} className="mr-2" />
                                <span>Subir foto...</span>
                            </div>
                        )}
                    </div>
                </div>

                {/* Descripción */}
                <div className="col-span-2">
                    <label className="block text-xs font-bold text-gray-500 mb-1">Descripción Detallada</label>
                    <textarea 
                        className="w-full p-2 border rounded-md h-24 outline-none focus:ring-2 focus:ring-orange-500"
                        placeholder="Describe el estado, medidas, lugar de entrega, etc."
                        value={descripcion} onChange={(e) => setDescripcion(e.target.value)} required
                    />
                </div>

                {/* Botón Submit */}
                <button 
                    type="submit" disabled={loading}
                    className="col-span-2 bg-orange-600 text-white py-3 rounded-md hover:bg-orange-700 transition font-bold shadow-sm disabled:opacity-50"
                >
                    {loading ? 'Subiendo aviso...' : 'Publicar Aviso'}
                </button>
            </form>
        </div>
    );
};