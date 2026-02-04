import React, { useEffect, useState } from 'react';
import { solicitudService } from '../services/solicitudService';
import type { SolicitudResponse } from '../../../types';
import { Check, X, User, MessageSquare } from 'lucide-react';

export const AdminSolicitudesPage = () => {
    const [solicitudes, setSolicitudes] = useState<SolicitudResponse[]>([]);

    const cargarTodas = async () => {
        const data = await solicitudService.getTodas();
        setSolicitudes(data);
    };

    useEffect(() => { cargarTodas(); }, []);

    const gestionar = async (id: number, aprobado: boolean) => {
        const comentario = aprobado 
            ? "Certificado generado correctamente." 
            : prompt("Motivo del rechazo:");
        
        if (comentario === null) return; // Si cancela el prompt

        try {
            await solicitudService.responder(id, aprobado ? 'APROBADA' : 'RECHAZADA', comentario);
            alert(aprobado ? "Solicitud Aprobada y PDF Generado" : "Solicitud Rechazada");
            cargarTodas();
        } catch (e) { alert("Error al procesar"); }
    };

    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-gray-800">Gestión de Certificados (Admin)</h2>
            <div className="grid gap-4">
                {solicitudes.map(s => (
                    <div key={s.id} className="bg-white p-5 rounded-xl border shadow-sm flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                            <div className="bg-blue-100 p-3 rounded-full text-blue-600">
                                <User size={24} />
                            </div>
                            <div>
                                <h3 className="font-bold text-gray-900">{s.nombreSolicitante}</h3>
                                <p className="text-sm text-gray-500">Solicitó: {s.tipo} - {new Date(s.fechaSolicitud).toLocaleDateString()}</p>
                                {s.estado !== 'PENDIENTE' && (
                                    <p className="text-xs mt-1 italic text-gray-400">
                                        Estado: <span className="font-bold">{s.estado}</span>
                                    </p>
                                )}
                            </div>
                        </div>

                        {s.estado === 'PENDIENTE' ? (
                            <div className="flex space-x-2">
                                <button 
                                    onClick={() => gestionar(s.id, true)}
                                    className="flex items-center space-x-1 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition"
                                >
                                    <Check size={18}/> <span>Aprobar</span>
                                </button>
                                <button 
                                    onClick={() => gestionar(s.id, false)}
                                    className="flex items-center space-x-1 bg-red-50 text-red-600 px-4 py-2 rounded-lg hover:bg-red-100 transition"
                                >
                                    <X size={18}/> <span>Rechazar</span>
                                </button>
                            </div>
                        ) : (
                            <div className="text-gray-400 flex items-center space-x-1">
                                <MessageSquare size={16} />
                                <span className="text-sm font-medium">Gestionada</span>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};