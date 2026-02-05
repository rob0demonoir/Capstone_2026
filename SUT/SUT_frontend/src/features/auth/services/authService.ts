import api from '../../../api/axiosConfig';
import type { LoginResponse, Usuario } from '../../../types';

export interface LoginRequest {
    email: string;
    contrasena: string;
}

export const authService = {
    login: async (credenciales: { email: string; contrasena: string }) => {
        // PASO 1: Obtener el token
        const response = await api.post<LoginResponse>('/api/autenticacion/login', credenciales);
        const { token } = response.data;

        // PASO 2: Guardar el token INMEDIATAMENTE
        // Esto es crucial para que la siguiente petición (getPerfil) lleve el token en el header
        localStorage.setItem('token', token);

        try {
            // PASO 3: "La Doble Llamada". Pedimos el perfil del usuario recién logueado
            const perfilResponse = await api.get<Usuario>('/api/usuarios/perfil');
            const usuario = perfilResponse.data;

            // PASO 4: Guardar los datos del usuario en localStorage
            localStorage.setItem('user', JSON.stringify(usuario));

            // Devolvemos ambos por si el componente los necesita
            return { token, usuario };

        } catch (error) {
            // Si falla obtener el perfil, hacemos rollback (logout) para no dejar un estado corrupto
            authService.logout();
            throw error;
        }
    },
    
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
    },

    getUser: (): Usuario | null => {
        const userStr = localStorage.getItem('user');
        if (!userStr) return null;
        try {
            return JSON.parse(userStr) as Usuario;
        } catch (e) {
            return null;
        }
    },

    // Mejoramos esta función para que sea más robusta con los roles (Mayúsculas/Minúsculas)
    isAdmin: () => {
        const user = authService.getUser();
        // Verifica si el rol existe y si contiene la palabra "ADMIN" (ej: "ROLE_ADMIN", "ADMINISTRADOR", "ADMIN")
        return user?.rol ? user.rol.toUpperCase().includes('ADMIN') : false;
    },

    registrar: async (datos: { nombre: string, email: string, contrasena: string, direccion: string, telefono: string }) => {
        // Asumiendo que el endpoint es POST /api/autenticacion/registro
        const response = await api.post('/api/autenticacion/registro', datos);
        return response.data;
    }
};