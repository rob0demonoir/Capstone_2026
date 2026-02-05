// Asegúrate de que CADA interfaz tenga la palabra 'export' al principio

export type Rol = 'ADMINISTRADOR' | 'VECINO';
export type TipoAviso = 'VENTA' | 'SERVICIO' | 'EVENTO' | 'BUSCO';
export type EstadoSolicitud = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA';

export interface Usuario {
    id: number;
    nombre: string;
    apellido: string;
    email: string;
    rut: string;
    rol: Rol;
    telefono: string;
    direccion: string;
    habilitado: boolean;
}

export interface Noticia {
    id: number;
    titulo: string;
    contenido: string;
    fechaPublicacion: string | number[];
    fecha?: string | number[];
    urlImagen?: string;
    autor: Usuario | string;
}

export interface NoticiaResponse {
    id: number;
    titulo: string;
    contenido: string;
    fecha: string; 
    autor: string; 
    urlImagen?: string;
}

export interface AvisoResponse {
    id: number;
    titulo: string;
    descripcion: string;
    precio?: number;
    tipo: TipoAviso;
    fechaPublicacion: string | number[];
    nombrePublicador: string;
    telefonoContacto: string;
    urlImagen?: string;
    esMio: boolean;
}

export interface CrearNoticiaRequest {
    titulo: string;
    contenido: string;
    urlImagen?: string;
}

export interface CrearAvisoRequest {
    titulo: string;
    descripcion: string;
    precio?: number; // El '?' es porque en tu SQL el precio puede ser NULL
    tipo: TipoAviso; // Usamos el tipo que ya definimos arriba (VENTA, SERVICIO, etc.)
    urlImagen?: string;
}


export interface SolicitudResponse {
    id: number;
    tipo: 'RESIDENCIA'; // Por ahora solo manejas este tipo según tu SQL
    fechaSolicitud: string;
    estado: EstadoSolicitud; // PENDIENTE, APROBADA o RECHAZADA
    comentarioAdmin?: string;
    nombreSolicitante: string; // El backend concatena nombre + apellido del solicitante
    rutaCertificado?: string; // Solo viene si está aprobada
}

// También necesitaremos este para la respuesta del Admin
export interface ResponderSolicitudRequest {
    estado: EstadoSolicitud;
    comentarioAdmin: string;
}

export interface LoginResponse {
    token: string;
    usuario: Usuario; // Esto usa la interfaz Usuario que ya definimos antes
}
