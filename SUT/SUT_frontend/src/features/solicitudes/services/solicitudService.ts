import api from '../../../api/axiosConfig';
import type { SolicitudResponse, EstadoSolicitud } from '../../../types';

export const solicitudService = {
    // Para el Vecino
    crearSolicitud: async (tipo: string) => {
        return api.post('/solicitudes', { tipo });
    },
    
    getMisSolicitudes: async () => {
        const response = await api.get<SolicitudResponse[]>('/solicitudes/mis-solicitudes');
        return response.data;
    },

    // Para el Admin
    getTodas: async () => {
        const response = await api.get<SolicitudResponse[]>('/solicitudes');
        return response.data;
    },

    responder: async (id: number, estado: EstadoSolicitud, comentario: string) => {
        return api.put(`/solicitudes/${id}/responder`, { estado, comentarioAdmin: comentario });
    },

    // Descarga de PDF
    descargarPdf: async (id: number) => {
        const response = await api.get(`/solicitudes/${id}/descargar`, {
            responseType: 'blob' // CRÍTICO: Para manejar archivos binarios
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `certificado_${id}.pdf`);
        document.body.appendChild(link);
        link.click();
    }
};