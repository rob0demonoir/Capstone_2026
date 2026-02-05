import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Auth
import { LoginPage } from './features/auth/components/LoginPage';
import { authService } from './features/auth/services/authService';
import { RegistroPage } from './features/auth/components/RegistroPage';

// Layout
import { DashboardLayout } from './components/layout/DashboardLayout';

// Features existentes
import { NoticiasList } from './features/noticias/components/NoticiasList';
import { AvisosList } from './features/avisos/components/AvisosList';

// Features Solicitudes (TUS COMPONENTES REALES)
import { AdminSolicitudesPage } from './features/solicitudes/components/AdminSolicitudesPage'; // <--- Nombre corregido
import { CertificadosPage } from './features/solicitudes/components/CertificadosPage'; // <--- Nombre corregido

// Feature Usuarios (LA NUEVA)
import { GestionUsuariosPage } from './features/usuarios/components/GestionUsuariosPage'; 

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    setIsAuthenticated(!!localStorage.getItem('token'));
  }, []);

  if (isAuthenticated === null) return null;

  const isAdmin = authService.isAdmin();

  return (
    <BrowserRouter>
      <Routes>
        {!isAuthenticated ? (
          <>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/registro" element={<RegistroPage />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </>
        ) : (
          <>
            {/* RUTAS COMUNES */}
            <Route path="/noticias" element={<DashboardLayout><NoticiasList /></DashboardLayout>} />
            <Route path="/avisos" element={<DashboardLayout><AvisosList /></DashboardLayout>} />

            {/* RUTA VECINO (CertificadosPage) */}
            {!isAdmin && (
              <Route path="/certificados" element={<DashboardLayout><CertificadosPage /></DashboardLayout>} />
            )}

            {/* RUTAS ADMIN */}
            {isAdmin && (
              <>
                {/* AdminSolicitudesPage */}
                <Route path="/admin/solicitudes" element={<DashboardLayout><AdminSolicitudesPage /></DashboardLayout>} />
                {/* GestionUsuariosPage */}
                <Route path="/admin/usuarios" element={<DashboardLayout><GestionUsuariosPage /></DashboardLayout>} />
              </>
            )}

            <Route path="/" element={<Navigate to="/noticias" replace />} />
            <Route path="*" element={<Navigate to="/noticias" replace />} />
          </>
        )}
      </Routes>
    </BrowserRouter>
  );
}

export default App;