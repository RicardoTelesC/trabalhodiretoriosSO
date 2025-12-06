# Instruções Rápidas de Uso

## Compilação Rápida

### Windows
```bash
compile.bat
```

### Linux/Mac
```bash
chmod +x compile.sh
./compile.sh
```

### Compilação Manual
```bash
cd src/main/java
javac *.java
```

## Execução

### Modo Shell Interativo (Recomendado)
```bash
cd src/main/java
java Shell
```

### Exemplo Programático
```bash
cd src/main/java
java ExemploUso
```

## Comandos do Shell

- `help` - Mostra ajuda
- `pwd` - Mostra diretório atual
- `cd <caminho>` - Muda diretório
- `ls [caminho]` - Lista arquivos
- `mkdir <caminho>` - Cria diretório
- `rmdir <caminho>` - Remove diretório vazio
- `touch <arquivo>` - Cria arquivo vazio
- `echo "texto" > <arquivo>` - Cria arquivo com conteúdo
- `cp <origem> <destino>` - Copia arquivo
- `mv <origem> <destino>` - Move/renomeia
- `rm <arquivo>` - Remove arquivo
- `journal show` - Mostra o log
- `exit` - Sai do simulador

## Exemplo de Uso no Shell

```
/ $ mkdir /home
/ $ mkdir /home/docs
/ $ cd /home/docs
/home/docs $ echo "Olá mundo" > teste.txt
/home/docs $ ls
/home/docs $ journal show
/home/docs $ exit
```

## Estrutura do Projeto

```
SO/
├── src/main/java/
│   ├── File.java
│   ├── Directory.java
│   ├── Journal.java
│   ├── FileSystemSimulator.java
│   ├── Shell.java
│   └── ExemploUso.java
├── README.md
├── INSTRUCOES.md
└── contexto.md
```

