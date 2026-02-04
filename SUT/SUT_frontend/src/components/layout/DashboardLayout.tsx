import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { authService } from '../../features/auth/services/authService';
import { 
    Newspaper, 
    Megaphone, 
    FileText, 
    Users, 
    LogOut, 
    LayoutDashboard 
} from 'lucide-react';

interface Props {
  children: React.ReactNode;
}

export const DashboardLayout = ({ children }: Props) => {
  const location = useLocation();

  // Definimos los items del menú para no repetir código
  const navItems = [
    { name: 'Noticias', path: '/noticias', icon: Newspaper },
    { name: 'Avisos Vecinales', path: '/avisos', icon: Megaphone },
    { name: 'Certificados', path: '/certificados', icon: FileText },
    { name: 'Usuarios (Admin)', path: '/usuarios', icon: Users },
  ];

  return (
    <div className="flex h-screen bg-gray-100 font-sans">
      {/* Sidebar Lateral */}
      <aside className="w-64 bg-blue-900 text-white flex flex-col shadow-2xl">
        <div className="p-6 border-b border-blue-800 flex items-center space-x-2">
          <LayoutDashboard className="text-blue-400" />
          <span className="text-xl font-bold tracking-tight">SUT Municipal</span>
        </div>
        
        <nav className="flex-1 p-4 space-y-2">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center space-x-3 w-full p-3 rounded-lg transition-all duration-200 ${
                  isActive 
                    ? 'bg-blue-700 text-white shadow-lg border-l-4 border-blue-400' 
                    : 'text-blue-100 hover:bg-blue-800 hover:text-white'
                }`}
              >
                <item.icon size={20} />
                <span className="font-medium">{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Botón de Logout al fondo */}
        <div className="p-4 border-t border-blue-800">
          <button 
            onClick={() => authService.logout()}
            className="flex items-center space-x-3 w-full p-3 rounded-lg text-red-300 hover:bg-red-900/50 hover:text-red-100 transition-colors"
          >
            <LogOut size={20} />
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* Área de Contenido Principal */}
      <main className="flex-1 overflow-y-auto">
        <header className="bg-white shadow-sm h-16 flex items-center px-8 border-b">
            <h1 className="text-sm font-semibold text-gray-400 uppercase tracking-widest">
                {navItems.find(i => i.path === location.pathname)?.name || 'Dashboard'}
            </h1>
        </header>

        <div className="p-8">
          <div className="max-w-6xl mx-auto">
            {children}
          </div>
        </div>
      </main>
    </div>
  );
};