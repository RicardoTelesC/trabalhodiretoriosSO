#!/bin/bash

# Script para compilar o projeto

echo "Compilando o projeto..."

# Cria diretório de saída se não existir
mkdir -p bin

# Compila todos os arquivos Java
javac -d bin -sourcepath src/main/java src/main/java/*.java

if [ $? -eq 0 ]; then
    echo "Compilação concluída com sucesso!"
    echo "Para executar o Shell, use: java -cp bin Shell"
    echo "Para executar o exemplo, use: java -cp bin ExemploUso"
else
    echo "Erro na compilação!"
    exit 1
fi

