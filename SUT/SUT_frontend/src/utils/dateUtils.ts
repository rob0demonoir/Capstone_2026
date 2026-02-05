// src/utils/dateUtils.ts

export const formatearFecha = (fecha: string | number[] | undefined | null): string => {
    if (!fecha) return "Fecha desconocida";

    // CASO 1: Viene como Array de Java [2026, 2, 5, 14, 30, 0]
    if (Array.isArray(fecha)) {
        const [anio, mes, dia, hora, minuto] = fecha;
        
        // IMPORTANTE: En Java los meses son 1-12, pero en JavaScript son 0-11.
        // Por eso hacemos (mes - 1).
        const fechaObj = new Date(anio, mes - 1, dia, hora || 0, minuto || 0);
        
        return fechaObj.toLocaleDateString() + ' ' + fechaObj.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
    }

    // CASO 2: Viene como String ISO o Timestamp normal
    try {
        const fechaObj = new Date(fecha as string);
        // Verificar si es válida
        if (isNaN(fechaObj.getTime())) return "Fecha inválida";
        
        return fechaObj.toLocaleDateString() + ' ' + fechaObj.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
    } catch (e) {
        return "Error en fecha";
    }
};