import api from '../../../api/axiosConfig';
import type { Noticia } from '../../../types'; // Asegúrate de tener la interfaz Noticia

export const noticiaService = {
    obtenerTodas: async () => {
        const response = await api.get<Noticia[]>('/api/noticias');
        return response.data;
    },

    // CAMBIO CLAVE: Recibimos FormData
    crear: async (titulo: string, contenido: string, file: File | null) => {
        const formData = new FormData();
        
        // Agregar campos de texto. 
        // IMPORTANTE: Si tu backend espera un JSON en un campo específico (ej: "request"), 
        // avísame. Pero como lo configuramos para Android, espera @RequestParam individuales.
        formData.append('titulo', titulo);
        formData.append('contenido', contenido);
        
        if (file) {
            formData.append('imagen', file); // 'imagen' debe coincidir con el @RequestParam del backend
        }

        const response = await api.post('/api/noticias', formData, {
            headers: {
                'Content-Type': 'multipart/form-data', // Axios suele poner esto auto, pero es bueno ser explícito
            },
        });
        return response.data;
    },
    
    // Si tienes eliminar
    eliminar: async (id: number) => {
        await api.delete(`/api/noticias/${id}`);
    }
};