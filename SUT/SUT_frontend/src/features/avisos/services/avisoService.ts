import api from '../../../api/axiosConfig';
import type { AvisoResponse, CrearAvisoRequest } from '../../../types';

export const avisoService = {
    obtenerTodos: async () => {
        const response = await api.get<AvisoResponse[]>('/avisos');
        return response.data;
    },

    crear: async (nuevoAviso: CrearAvisoRequest) => {
        const response = await api.post<string>('/avisos', nuevoAviso);
        return response.data;
    },

    eliminar: async (id: number) => {
        const response = await api.delete<string>(`/avisos/${id}`);
        return response.data;
    }
};