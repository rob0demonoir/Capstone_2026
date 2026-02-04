import api from '../../../api/axiosConfig';

export interface LoginRequest {
    email: string;
    contrasena: string;
}

export interface LoginResponse {
    token: string;
}

export const authService = {
    login: async (credentials: LoginRequest) => {
        const response = await api.post<LoginResponse>('/autenticacion/login', credentials);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
        }
        return response.data;
    },
    
    logout: () => {
        localStorage.removeItem('token');
        window.location.href = '/login';
    }
};
