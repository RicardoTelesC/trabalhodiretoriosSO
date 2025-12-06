@echo off
REM Script para compilar o projeto no Windows

echo Compilando o projeto...

REM Cria diretório de saída se não existir
if not exist bin mkdir bin

REM Compila todos os arquivos Java
javac -d bin -sourcepath src/main/java src/main/java/*.java

if %errorlevel% == 0 (
    echo Compilação concluída com sucesso!
    echo Para executar o Shell, use: java -cp bin Shell
    echo Para executar o exemplo, use: java -cp bin ExemploUso
) else (
    echo Erro na compilação!
    exit /b 1
)

