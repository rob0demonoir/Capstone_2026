import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { UserPlus, Mail, Lock, User, MapPin, Phone } from 'lucide-react';

export const RegistroPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        nombre: '',
        email: '',
        contrasena: '',
        direccion: '',
        telefono: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await authService.registrar(formData);
            alert("¡Cuenta creada exitosamente! Ahora puedes iniciar sesión.");
            navigate('/login');
        } catch (err: any) {
            console.error(err);
            setError('Error al registrarse. Verifique los datos o intente más tarde.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-lg">
                <div className="text-center">
                    <UserPlus className="mx-auto h-12 w-12 text-blue-600" />
                    <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
                        Únete a tu Comunidad
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Crea tu cuenta para acceder a los servicios vecinales.
                    </p>
                </div>
                
                {error && (
                    <div className="bg-red-50 border-l-4 border-red-500 p-4">
                        <p className="text-red-700 text-sm">{error}</p>
                    </div>
                )}

                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm space-y-4">
                        
                        {/* Nombre */}
                        <div className="relative">
                            <User className="absolute left-3 top-3 text-gray-400" size={20} />
                            <input
                                name="nombre"
                                type="text"
                                required
                                className="appearance-none rounded-lg relative block w-full pl-10 px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Nombre Completo"
                                value={formData.nombre}
                                onChange={handleChange}
                            />
                        </div>

                        {/* Email */}
                        <div className="relative">
                            <Mail className="absolute left-3 top-3 text-gray-400" size={20} />
                            <input
                                name="email"
                                type="email"
                                required
                                className="appearance-none rounded-lg relative block w-full pl-10 px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Correo Electrónico"
                                value={formData.email}
                                onChange={handleChange}
                            />
                        </div>

                        {/* Contraseña */}
                        <div className="relative">
                            <Lock className="absolute left-3 top-3 text-gray-400" size={20} />
                            <input
                                name="contrasena"
                                type="password"
                                required
                                className="appearance-none rounded-lg relative block w-full pl-10 px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Contraseña"
                                value={formData.contrasena}
                                onChange={handleChange}
                            />
                        </div>

                        {/* Dirección */}
                        <div className="relative">
                            <MapPin className="absolute left-3 top-3 text-gray-400" size={20} />
                            <input
                                name="direccion"
                                type="text"
                                required
                                className="appearance-none rounded-lg relative block w-full pl-10 px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Dirección (Calle y Número)"
                                value={formData.direccion}
                                onChange={handleChange}
                            />
                        </div>

                        {/* Teléfono */}
                        <div className="relative">
                            <Phone className="absolute left-3 top-3 text-gray-400" size={20} />
                            <input
                                name="telefono"
                                type="text"
                                required
                                className="appearance-none rounded-lg relative block w-full pl-10 px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Teléfono (+569...)"
                                value={formData.telefono}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={loading}
                            className={`group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${loading ? 'opacity-70 cursor-wait' : ''}`}
                        >
                            {loading ? 'Registrando...' : 'Crear Cuenta'}
                        </button>
                    </div>

                    <div className="text-sm text-center">
                        <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
                            ¿Ya tienes cuenta? Inicia sesión aquí
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};