import api from '../../../api/axiosConfig';
import type { NoticiaResponse, CrearNoticiaRequest } from '../../../types';

export const noticiaService = {
    obtenerTodas: async () => {
        const response = await api.get<NoticiaResponse[]>('/noticias');
        return response.data;
    },
    crear: async (nuevaNoticia: CrearNoticiaRequest) => {
        const response = await api.post<string>('/noticias', nuevaNoticia);
        return response.data;
    }
};


    

