import api from '../../../api/axiosConfig';
import type { Usuario } from '../../../types';

export const usuarioService = {
    getAll: async () => {
        const response = await api.get<Usuario[]>('/api/usuarios');
        return response.data;
    },

    cambiarRol: async (id: number, nuevoRol: string) => {
        // Asumiendo que tu backend recibe el string directo ("ADMIN" o "VECINO")
        // Ajusta el Content-Type si es necesario
        const response = await api.put(`/api/usuarios/${id}/rol`, nuevoRol, {
            headers: { 'Content-Type': 'text/plain' }
        });
        return response.data;
    },

    eliminar: async (id: number) => {
        // Asumiendo que el endpoint es DELETE /api/usuarios/{id}
        await api.delete(`/api/usuarios/${id}`);
    }
};