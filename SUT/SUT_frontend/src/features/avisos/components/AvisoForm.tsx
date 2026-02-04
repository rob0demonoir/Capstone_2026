import React, { useState } from 'react';
import { avisoService } from '../services/avisoService';
import { Megaphone, Tag, DollarSign } from 'lucide-react';
import type { TipoAviso } from '../../../types';

interface Props { onAvisoCreado: () => void; }

export const AvisoForm = ({ onAvisoCreado }: Props) => {
    const [titulo, setTitulo] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [precio, setPrecio] = useState<number>(0);
    const [tipo, setTipo] = useState<TipoAviso>('VENTA');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            await avisoService.crear({ titulo, descripcion, precio, tipo });
            setTitulo(''); setDescripcion(''); setPrecio(0);
            onAvisoCreado();
            alert("Aviso publicado correctamente");
        } catch (error) {
            alert("Error al publicar aviso");
        } finally { setLoading(false); }
    };

    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-orange-100 mb-8">
            <div className="flex items-center space-x-2 mb-4 text-orange-600">
                <Megaphone size={24} />
                <h3 className="text-lg font-bold">¿Quieres vender o anunciar algo?</h3>
            </div>
            
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <input 
                    className="col-span-2 p-2 border rounded-md outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="Título del aviso (ej: Vendo bicicleta)"
                    value={titulo} onChange={(e) => setTitulo(e.target.value)} required
                />
                <select 
                    className="p-2 border rounded-md outline-none"
                    value={tipo} onChange={(e) => setTipo(e.target.value as TipoAviso)}
                >
                    <option value="VENTA">Venta</option>
                    <option value="SERVICIO">Servicio</option>
                    <option value="EVENTO">Evento</option>
                    <option value="BUSCO">Busco</option>
                </select>
                <div className="relative">
                    <DollarSign className="absolute left-2 top-2.5 text-gray-400" size={18} />
                    <input 
                        type="number" className="w-full pl-8 p-2 border rounded-md outline-none"
                        placeholder="Precio (opcional)"
                        value={precio} onChange={(e) => setPrecio(Number(e.target.value))}
                    />
                </div>
                <textarea 
                    className="col-span-2 p-2 border rounded-md h-24 outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="Descripción detallada..."
                    value={descripcion} onChange={(e) => setDescripcion(e.target.value)} required
                />
                <button 
                    type="submit" disabled={loading}
                    className="col-span-2 bg-orange-600 text-white py-2 rounded-md hover:bg-orange-700 transition"
                >
                    {loading ? 'Publicando...' : 'Publicar Aviso'}
                </button>
            </form>
        </div>
    );
};