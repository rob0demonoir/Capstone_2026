import api from '../../../api/axiosConfig';
import type { AvisoResponse } from '../../../types';

export const avisoService = {
    obtenerTodos: async () => {
        const response = await api.get<AvisoResponse[]>('/api/avisos');
        return response.data;
    },

    crear: async (formData: FormData) => {
        // Axios detecta automáticamente el FormData y ajusta los headers
        // para enviar 'multipart/form-data'
        const response = await api.post<string>('/api/avisos', formData);
        return response.data;
    },

    eliminar: async (id: number) => {
        const response = await api.delete<string>(`/api/avisos/${id}`);
        return response.data;
    }
};