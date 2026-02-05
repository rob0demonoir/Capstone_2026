package cl.duoc.sut_backend.config // Ajusta al paquete de tu proyecto

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Configuración para Linux/Mac/Windows
        // "file:uploads/" le dice que busque en la carpeta uploads en la raíz del proyecto

        registry.addResourceHandler("/api/uploads/**")
            .addResourceLocations("file:/app/uploads/")
    }
}