#!/bin/bash
# -----------------------------------------------------
# 🎬 Movie Manager DMS - SQL Setup Script
# Crea la carpeta /fall 2025/sql y abre el archivo schema.sql
# Autor: Luis Augusto Monserratt Alvarado
# -----------------------------------------------------

# Ruta base de tu proyecto (ajústala si es necesario)
PROJECT_PATH="/Users/luisaugustomonserratt/IdeaProjects/phase 4"

# Ruta del destino donde se guardará el SQL
DEST_DIR="$PROJECT_PATH/fall 2025/sql"

# Crear carpetas si no existen
echo "📂 Creando carpeta destino..."
mkdir -p "$DEST_DIR"

# Crear archivo schema.sql si no existe
SCHEMA_FILE="$DEST_DIR/schema.sql"
if [ ! -f "$SCHEMA_FILE" ]; then
  echo "📝 Creando nuevo archivo schema.sql..."
  touch "$SCHEMA_FILE"
else
  echo "✅ El archivo schema.sql ya existe."
fi

# Mostrar la ruta final
echo "📍 Archivo ubicado en: $SCHEMA_FILE"

# Abrir el archivo en nano para editarlo
echo "✏️  Abriendo archivo en nano (usa Ctrl+O → Enter → Ctrl+X para guardar y salir)"
sleep 1
nano "$SCHEMA_FILE"

