import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { authService } from '../../features/auth/services/authService';
import { 
    Newspaper, 
    Megaphone, 
    FileText, 
    Users, 
    LogOut,
    UserCircle,
    ShieldCheck
} from 'lucide-react';

interface Props {
  children: React.ReactNode;
}

export const DashboardLayout = ({ children }: Props) => {
  const location = useLocation();
  const user = authService.getUser();
  const isAdmin = authService.isAdmin();

  const commonItems = [
    { name: 'Noticias', path: '/noticias', icon: Newspaper },
    { name: 'Avisos Vecinales', path: '/avisos', icon: Megaphone },
  ];

  const adminItems = [
    { name: 'Gestión Certificados', path: '/admin/solicitudes', icon: FileText },
    { name: 'Gestión Usuarios', path: '/admin/usuarios', icon: Users },
  ];

  const vecinoItems = [
    { name: 'Mis Certificados', path: '/certificados', icon: FileText },
  ];

  const navItems = [...commonItems, ...(isAdmin ? adminItems : vecinoItems)];

  return (
    <div className="flex h-screen bg-slate-50 font-sans text-slate-800">
      
      {/* --- SIDEBAR (TEAL OSCURO) --- */}
      <aside className="w-64 bg-teal-900 text-white flex flex-col shadow-2xl transition-all z-20">
        
        {/* LOGO */}
        <div className="p-6 border-b border-teal-800">
          <div className="flex items-center space-x-3 mb-6">
            <img src="/neighborhood.png" alt="SUT Logo" className="w-10 h-10 object-contain drop-shadow-md"/>
            <span className="text-xl font-bold tracking-tight text-white">SUT Vecinos</span>
          </div>
          
          {/* TARJETA USUARIO MINI */}
          <div className="bg-teal-800/50 p-3 rounded-xl border border-teal-700/50 backdrop-blur-sm">
            <div className="flex items-center space-x-3">
              {isAdmin ? (
                  <ShieldCheck size={32} className="text-orange-400" />
              ) : (
                  <UserCircle size={32} className="text-teal-200" />
              )}
              
              <div className="overflow-hidden">
                <div className="text-sm font-bold text-white truncate" title={user?.nombre}>
                  {user?.nombre || 'Usuario'}
                </div>
                <div className={`text-[10px] font-bold uppercase tracking-wider ${isAdmin ? 'text-orange-400' : 'text-teal-200'}`}>
                  {user?.rol || 'Invitado'}
                </div>
              </div>
            </div>
          </div>
        </div>
        
        {/* NAVEGACIÓN */}
        <nav className="flex-1 p-4 space-y-2 overflow-y-auto">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center space-x-3 w-full p-3 rounded-lg transition-all duration-200 group ${
                  isActive 
                    ? 'bg-teal-700 text-white shadow-md border-r-4 border-orange-500' 
                    : 'text-teal-100 hover:bg-teal-800 hover:text-white'
                }`}
              >
                <item.icon size={20} className={isActive ? 'text-orange-400' : 'text-teal-300 group-hover:text-white'} />
                <span className="font-medium">{item.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* LOGOUT */}
        <div className="p-4 border-t border-teal-800">
          <button 
            onClick={() => authService.logout()}
            className="flex items-center space-x-3 w-full p-3 rounded-lg text-teal-200 hover:bg-red-900/30 hover:text-red-200 transition-colors"
          >
            <LogOut size={20} />
            <span className="font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      {/* --- ÁREA PRINCIPAL --- */}
      <main className="flex-1 overflow-y-auto flex flex-col relative">
        
        {/* HEADER SUPERIOR (BLANCO) */}
        <header className="bg-white shadow-sm h-16 flex items-center justify-between px-8 sticky top-0 z-10">
            <h1 className="text-lg font-bold text-teal-900 flex items-center">
                {navItems.find(i => i.path === location.pathname)?.name || 'Dashboard'}
            </h1>
            <div className="text-xs font-medium text-slate-400 bg-slate-100 px-3 py-1 rounded-full">
                {new Date().toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
            </div>
        </header>

        {/* CONTENIDO CENTRADO */}
        <div className="p-8 flex-1">
          <div className="max-w-6xl mx-auto w-full">
            {children}
          </div>
        </div>
      </main>
    </div>
  );
};