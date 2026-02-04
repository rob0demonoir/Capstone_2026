import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from './features/auth/components/LoginPage';
import { DashboardLayout } from './components/layout/DashboardLayout';
import { NoticiasList } from './features/noticias/components/NoticiasList';
import { AvisosList } from './features/avisos/components/AvisosList';
import { CertificadosPage } from './features/solicitudes/components/CertificadosPage';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    setIsAuthenticated(!!token);
  }, []);

  if (isAuthenticated === null) {
    return <div className="min-h-screen flex items-center justify-center">Cargando...</div>;
  }

  return (
    <BrowserRouter>
      <Routes>
        {/* CASO 1: Si NO estoy logueado */}
        {!isAuthenticated ? (
          <>
            <Route path="/login" element={<LoginPage />} />
            <Route path="*" element={<Navigate to="/login" replace />} />
          </>
        ) : (
          /* CASO 2: Si ESTOY logueado */
          <>
            <Route path="/noticias" element={<DashboardLayout><NoticiasList /></DashboardLayout>} />
            <Route path="/avisos" element={<DashboardLayout><AvisosList /></DashboardLayout>} />
            <Route path="/certificados" element={<DashboardLayout><CertificadosPage /></DashboardLayout>} />
            
            {/* Rutas de apoyo */}
            <Route path="/" element={<Navigate to="/noticias" replace />} />
            <Route path="*" element={<Navigate to="/noticias" replace />} />
          </>
        )}
      </Routes>
    </BrowserRouter>
  );
}