# Gestor de passwords
Una aplicación de consola desarrollada en Java para gestionar passwords de forma segura y cifrada.

## Características Principales

- **Cifrado Avanzado**: Utiliza AES-256 con GCM para máxima seguridad
- **Generador de Passwords**: Crea passwords fuertes y seguras automáticamente
- **Almacenamiento Local**: Tus datos se guardan cifrados en tu máquina
- **Búsqueda Rápida**: Encuentra passwords por sitio o usuario
- **Gestión de Password Maestro**: Cambio seguro de la password principal
- **Listado de passwords**: Información sobre el almacenamiento 

## Tecnologías Utilizadas

- **Java 8+**
- **AES-256-GCM** para cifrado
- **PBKDF2** para derivación de claves
- **SHA-256** para hashing

## Prerrequisitos

- Java JDK 8 o superior
- Sistema operativo: Windows, Linux o macOS

## Instalación y Ejecución

### 1. Clonar o descargar el proyecto
```bash
git clone https://github.com/juampi7237/gestor-passwords
cd gestor-passwords
```

# Características de Seguridad
Cifrado Implementado
* AES-256-GCM: Cifrado autenticado
* PBKDF2 con 100,000 iteraciones: Derivación segura de claves
*  aleatorios: Únicos para cada password
* IVs (Vectores de Inicialización): Únicos para cada cifrado

## Configuración Inicial
Al ejecutar la aplicación por primera vez, se te pedirá crear un password maestro
el password debe tener al menos 8 caracteres el password maestro se usa para cifrar todas tus demás passwords

### ⚠️ Importante
Guarda tu password maestro en un lugar seguro sin ella, no podrás acceder a tus passwords guardadas
No hay recuperación de password maestro perdida

🎮 Uso de la Aplicación
Menú Principal

=========================================== <br/>
              MENÚ PRINCIPAL<br/>
===========================================<br/>
1. Guardar password
2. Listar todas las passwords
3. Buscar password
4. Eliminar password
5. Actualizar password maestro
6. Salir<br/>
============================================

**1 Guardar Nueva Password** -> crea una nueva password para un usuario

**2. Listar Passwords** -> Muestra todos los passwords guardados con:

**3. Buscar Passwords** ->  Busca por: Nombre del sitio o Usuario

**4. Eliminar Password** -> Elimina un password seleccionado de la lista

**5. Actualizar Password Maestro** ->  Requiere el password actual y cifra automaticamente la nueva clave
