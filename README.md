# FocusMeal 🥗

FocusMeal es una aplicación móvil Android diseñada para ayudar a los usuarios a llevar un control detallado de su alimentación, macronutrientes y progreso hacia sus objetivos de salud. La aplicación integra servicios de nutrición y soporte mediante un chat y sistema de PQRS.

## 🚀 Características Principales

*   **Autenticación segura**: Registro e inicio de sesión integrados con Firebase Auth.
*   **Seguimiento Nutricional**: Registro de comidas diarias con cálculo automático de calorías, proteínas, carbohidratos y grasas.
*   **Gestión de Planes**: Visualización y creación de planes nutricionales personalizados (General, Keto, Vegetariana, etc.).
*   **Monitoreo de Progreso**: Gráficas y reportes sobre la evolución del peso y cumplimiento de metas.
*   **Interacción Directa**: Chat integrado para comunicación con asesores o nutricionistas.
*   **Perfil Personalizado**: Configuración de datos físicos (peso, altura, edad) y objetivos personales.
*   **Sistema de PQRS**: Módulo para envío de peticiones, quejas, reclamos y sugerencias.

## 🛠️ Tecnologías Utilizadas

*   **Lenguaje**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) con **Material 3**.
*   **Arquitectura**: MVVM (Model-View-ViewModel).
*   **Backend**:
    *   **Firebase Authentication**: Gestión de usuarios.
    *   **Firebase Firestore**: Base de datos NoSQL para almacenamiento en tiempo real.
    *   **Firebase Storage**: Almacenamiento de imágenes (recetas, fotos de perfil).
    *   **Firebase Analytics**: Seguimiento de métricas de uso.
*   **Inyección de Dependencias**: Patrón manual mediante `AppModule`.
*   **Navegación**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
*   **Carga de Imágenes**: [Coil](https://coil-kt.github.io/coil/compose/).
*   **Concurrencia**: Kotlin Coroutines & Flow.

## 📁 Estructura del Proyecto

*   `data/`: Contiene los modelos de datos, repositorios y utilidades de Firebase.
*   `ui/`:
    *   `screens/`: Implementación de cada pantalla (Login, Home, Profile, etc.).
    *   `components/`: Componentes de UI reutilizables (Campos de texto personalizados, botones, etc.).
    *   `theme/`: Definición de colores, tipografía y tema de la aplicación.
*   `di/`: Configuración global de dependencias (`AppModule`).

## ⚙️ Instalación y Configuración

1.  Clona el repositorio:
    ```bash
    git clone https://github.com/cervantessaralucia4-dotcom/FocusMealApp.git
    ```
2.  Abre el proyecto en **Android Studio (Ladybug o superior)**.
3.  Asegúrate de tener configurado el archivo `google-services.json` en el directorio `app/` (necesario para Firebase).
4.  Sincroniza el proyecto con Gradle.
5.  Ejecuta la aplicación en un emulador o dispositivo físico.

---
Desarrollado con ❤️ para mejorar la salud nutricional.
