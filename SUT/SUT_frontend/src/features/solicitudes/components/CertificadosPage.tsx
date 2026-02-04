import React, { useEffect, useState } from 'react';
import { solicitudService } from '../services/solicitudService';
import type { SolicitudResponse } from '../../../types';
import { FileText, Download, Clock, CheckCircle, XCircle } from 'lucide-react';

export const CertificadosPage = () => {
    const [solicitudes, setSolicitudes] = useState<SolicitudResponse[]>([]);
    const [loading, setLoading] = useState(false);

    const cargar = async () => {
        const data = await solicitudService.getMisSolicitudes();
        setSolicitudes(data);
    };

    useEffect(() => { cargar(); }, []);

    const solicitar = async () => {
        setLoading(true);
        try {
            await solicitudService.crearSolicitud('RESIDENCIA');
            alert("Solicitud enviada. Un administrador la revisará pronto.");
            cargar();
        } catch (e) { alert("Error al solicitar"); }
        finally { setLoading(false); }
    };

    return (
        <div className="space-y-8">
            <div className="bg-blue-50 border border-blue-200 p-6 rounded-xl flex justify-between items-center">
                <div>
                    <h2 className="text-xl font-bold text-blue-900">Certificado de Residencia</h2>
                    <p className="text-blue-700">Obtén tu documento oficial firmado por la municipalidad.</p>
                </div>
                <button 
                    onClick={solicitar}
                    disabled={loading}
                    className="bg-blue-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-blue-700 disabled:bg-gray-400 transition"
                >
                    {loading ? 'Procesando...' : 'Solicitar Nuevo'}
                </button>
            </div>

            <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-gray-50 border-b">
                        <tr>
                            <th className="p-4 font-bold text-gray-600">Fecha</th>
                            <th className="p-4 font-bold text-gray-600">Tipo</th>
                            <th className="p-4 font-bold text-gray-600">Estado</th>
                            <th className="p-4 font-bold text-gray-600">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        {solicitudes.map(s => (
                            <tr key={s.id} className="border-b hover:bg-gray-50">
                                <td className="p-4 text-sm">{new Date(s.fechaSolicitud).toLocaleDateString()}</td>
                                <td className="p-4 font-medium">{s.tipo}</td>
                                <td className="p-4">
                                    <span className={`flex items-center space-x-1 text-xs font-bold uppercase ${
                                        s.estado === 'APROBADA' ? 'text-green-600' : 
                                        s.estado === 'PENDIENTE' ? 'text-orange-500' : 'text-red-500'
                                    }`}>
                                        {s.estado === 'APROBADA' ? <CheckCircle size={14}/> : 
                                         s.estado === 'PENDIENTE' ? <Clock size={14}/> : <XCircle size={14}/>}
                                        <span>{s.estado}</span>
                                    </span>
                                </td>
                                <td className="p-4">
                                    {s.estado === 'APROBADA' ? (
                                        <button 
                                            onClick={() => solicitudService.descargarPdf(s.id)}
                                            className="flex items-center space-x-1 text-blue-600 hover:underline font-bold"
                                        >
                                            <Download size={16}/> <span>Descargar PDF</span>
                                        </button>
                                    ) : (
                                        <span className="text-gray-400 text-xs italic">
                                            {s.estado === 'RECHAZADA' ? s.comentarioAdmin : 'En espera'}
                                        </span>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};