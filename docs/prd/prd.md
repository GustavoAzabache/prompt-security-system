# PRD — Sistema de Consultas Seguras sobre Documentos (Prompt Security System)

## Título

**Prompt Security System**: preguntas y respuestas sobre documentos internos con control de acceso por rol, área y nivel de confidencialidad.

## Qué es

Una plataforma donde una organización carga sus documentos internos (manuales, políticas, procedimientos) y las personas les hacen preguntas en lenguaje natural para obtener respuestas redactadas a partir del contenido real de esos documentos, con la fuente citada.

Sus piezas, en lenguaje de producto:

- **Gestión de personas y accesos**: empleados organizados por área y jerarquía, más usuarios externos con acceso limitado. Cada persona ve únicamente lo que su nivel le permite.
- **Gestión de documentos**: carga, consulta y descarga de archivos, cada uno etiquetado con su área dueña y su nivel de confidencialidad (público, privado por área, confidencial, etc.).
- **Indexación**: preparación de cada documento para que su contenido sea buscable por significado, no solo por palabras exactas.
- **Preguntas y respuestas**: el usuario pregunta, el sistema recupera los fragmentos permitidos para esa persona y redacta una respuesta anclada en ellos. Si la documentación no cubre la pregunta, lo dice en vez de inventar. Si la persona no tiene permiso sobre un documento, ese documento no existe para ella: ni aparece, ni se cita, ni se resume.

## Problema que soluciona

1. **El conocimiento interno está enterrado en PDFs que nadie lee completos.** Responder "¿qué dice el manual sobre X?" hoy exige buscar el archivo, abrirlo y leerlo. El sistema responde en segundos con la cita exacta.
2. **Los asistentes genéricos inventan y no respetan permisos.** Preguntarle a un chatbot común sobre documentos internos tiene dos fallas: alucina respuestas cuando no sabe, y no distingue quién puede ver qué. Este sistema se niega antes de inventar y filtra por permisos antes de responder.
3. **Los permisos viven en la cabeza de las personas, no en el sistema.** Al etiquetar cada documento con área y confidencialidad, la regla "solo TI ve esto" o "solo directores ven aquello" se cumple automáticamente en cada lista, cada descarga y cada respuesta generada.
4. **La información cambia y las copias mienten.** Cuando un documento cambia de confidencialidad, el índice debe enterarse: el producto prevé la re-indexación para que lo que se responde hoy refleje los permisos vigentes, no los de ayer.

## A quién va dirigido

- **Empresas con documentación interna sensible**: manuales del trabajador, políticas de seguridad, procedimientos por área, donde no todos deben ver todo.
- **Equipos de RR.HH., legal y cumplimiento**: necesitan que las respuestas al personal sean trazables (con fuente citada) y que la información confidencial no se filtre por el canal de consultas.
- **Directivos**: consultan documentos de su ámbito y material confidencial sin intermediarios, con la garantía de que el resto de niveles no accede a lo mismo.
- **Usuarios externos (clientes, proveedores, auditores)**: acceden únicamente a respuestas derivadas de documentación pública, sin ver ni descargar archivos internos.
- **Equipos de tecnología que construyen sobre esto**: el sistema expone sus capacidades por API versionada, lista para conectarle una interfaz web u otros canales de consulta.

## Fuera del alcance de este documento

Detalles de implementación (lenguajes, frameworks, infraestructura, modelos de IA y decisiones de arquitectura) — pertenecen a la documentación técnica, no a este PRD.
