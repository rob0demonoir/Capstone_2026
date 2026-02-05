import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../services/authService';
import { Mail, Lock, ArrowRight } from 'lucide-react';

export const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await authService.login({ email, contrasena: password });
            window.location.href = '/noticias';
        } catch (err: any) {
            setError('Credenciales incorrectas o error de conexión.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-teal-900 to-slate-900 p-4">
            
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden flex flex-col md:flex-row">
                
                {/* FORMULARIO */}
                <div className="w-full p-8 md:p-10">
                    <div className="text-center mb-8">
                        <img src="/neighborhood.png" alt="Logo SUT" className="w-20 h-20 mx-auto mb-4 object-contain"/>
                        <h2 className="text-3xl font-bold text-slate-800">Bienvenido</h2>
                        <p className="text-slate-500 mt-2">Sistema Unidad Territorial</p>
                    </div>

                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 text-sm rounded">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div className="relative group">
                            <Mail className="absolute left-3 top-3.5 text-slate-400 group-focus-within:text-teal-500 transition-colors" size={20} />
                            <input 
                                type="email" 
                                placeholder="Correo electrónico"
                                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:bg-white transition-all"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                        
                        <div className="relative group">
                            <Lock className="absolute left-3 top-3.5 text-slate-400 group-focus-within:text-teal-500 transition-colors" size={20} />
                            <input 
                                type="password" 
                                placeholder="Contraseña"
                                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:bg-white transition-all"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button 
                            type="submit"
                            disabled={loading}
                            className={`w-full bg-teal-600 text-white py-3.5 rounded-xl font-bold text-lg hover:bg-teal-700 active:scale-95 transition-all shadow-lg shadow-teal-600/30 flex items-center justify-center space-x-2 ${
                                loading ? 'opacity-70 cursor-wait' : ''
                            }`}
                        >
                            <span>{loading ? 'Ingresando...' : 'Iniciar Sesión'}</span>
                            {!loading && <ArrowRight size={20} />}
                        </button>
                    </form>

                    <div className="mt-8 pt-6 border-t border-slate-100 text-center">
                        <p className="text-sm text-slate-500 mb-2">¿Nuevo en el barrio?</p>
                        <Link 
                            to="/registro" 
                            className="inline-block text-orange-600 font-bold hover:text-orange-700 hover:underline transition-colors"
                        >
                            Crear cuenta de vecino
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};