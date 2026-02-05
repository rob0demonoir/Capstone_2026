import api from '../../../api/axiosConfig';
import type { SolicitudResponse } from '../../../types'; // Asegúrate de tener este tipo en types/index.ts

export const solicitudService = {
    // Usado en AdminSolicitudesPage
    getTodas: async () => {
        const response = await api.get<SolicitudResponse[]>('/api/solicitudes');
        // Ordenamos: PENDIENTE primero
        return response.data.sort((a) => (a.estado === 'PENDIENTE' ? -1 : 1));
    },

    // Usado en CertificadosPage
    getMisSolicitudes: async () => {
        const response = await api.get<SolicitudResponse[]>('/api/solicitudes/mis-solicitudes');
        return response.data.reverse(); // Las más nuevas primero
    },

    // Usado en CertificadosPage (Nombre exacto: crearSolicitud)
    crearSolicitud: async (tipo: string) => {
        const response = await api.post('/api/solicitudes', { tipo, comentario: "Solicitud desde Web" });
        return response.data;
    },

    // Usado en AdminSolicitudesPage
    responder: async (id: number, estado: string, comentarioAdmin: string) => {
        const response = await api.put(`/api/solicitudes/${id}/responder`, {
            estado,
            comentarioAdmin
        });
        return response.data;
    },

    // Usado en CertificadosPage: Lógica para descargar PDF real
    descargarPdf: async (id: number) => {
        try {
            const response = await api.get(`/api/solicitudes/${id}/descargar`, { 
                responseType: 'blob' // Importante para archivos binarios
            });
            
            // Crear un link temporal en el navegador para forzar la descarga
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `certificado_${id}.pdf`); // Nombre del archivo
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            console.error("Error al descargar PDF", error);
            alert("No se pudo descargar el documento");
        }
    }
};