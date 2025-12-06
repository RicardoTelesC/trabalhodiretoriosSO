# Simulador de Sistema de Arquivos

## Resumo

Este trabalho propõe o desenvolvimento de um simulador para compreender um sistema de arquivos. O simulador implementa funcionalidades básicas de manipulação de arquivos e diretórios, com suporte a Journaling para garantir a integridade dos dados.

## Introdução

O gerenciamento eficiente de arquivos é crucial para o funcionamento dos sistemas operacionais. Para isso, entender como é montado e organizado um sistema é a base para a compreensão dos sistemas operacionais.

Este projeto implementa um sistema de arquivos simulado em Java para fins educacionais. O simulador permite trabalhar com arquivos e diretórios de forma similar a um sistema operacional real, incluindo o mecanismo de journaling para garantir a integridade dos dados.

## Objetivo

Desenvolver um simulador de sistema de arquivos em Java que implemente funcionalidades básicas de manipulação de arquivos e diretórios, com suporte a Journaling para garantir a integridade dos dados. Este simulador permite a criação de um arquivo que simula o sistema de arquivos e realizar operações como copiar, apagar, renomear arquivos e diretórios, bem como listar o conteúdo de um diretório.

---

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### Descrição do Sistema de Arquivos

Um **sistema de arquivos** é uma estrutura de dados e conjunto de rotinas que um sistema operacional utiliza para organizar e gerenciar arquivos e diretórios em dispositivos de armazenamento. Ele fornece uma interface abstrata entre o sistema operacional e o hardware de armazenamento, permitindo que programas e usuários acessem dados de forma organizada e eficiente.

A importância dos sistemas de arquivos reside em:

- **Organização**: Permite estruturar dados de forma hierárquica através de diretórios
- **Persistência**: Garante que dados sejam armazenados permanentemente
- **Segurança**: Oferece mecanismos de controle de acesso e permissões
- **Eficiência**: Otimiza o acesso e a localização de dados no disco
- **Confiabilidade**: Fornece mecanismos para recuperação de dados e integridade

### Journaling

O **Journaling** (ou registro em diário) é um mecanismo de segurança usado em sistemas de arquivos modernos para garantir a integridade dos dados mesmo em caso de falhas do sistema (como quedas de energia ou travamentos).

#### Funcionamento do Journaling

O journaling funciona através de um **log de transações** onde todas as operações são registradas antes de serem efetivamente aplicadas ao sistema de arquivos. Isso permite que, em caso de falha, o sistema possa:

1. **Recuperar operações não completadas**: Reaplicando transações que estavam em andamento
2. **Reversão de operações**: Desfazendo mudanças que não foram completadas
3. **Consistência**: Garantindo que o sistema sempre esteja em um estado consistente

#### Tipos de Journaling

1. **Write-Ahead Logging (WAL)**: As operações são registradas no journal antes de serem aplicadas aos metadados do sistema de arquivos. Este é o método mais seguro e utilizado em sistemas como ext3/ext4.

2. **Log-Structured File System**: Todo o sistema é estruturado como um log sequencial, onde novas escritas são sempre adicionadas ao final.

3. **Metadata Journaling**: Apenas os metadados são registrados no journal, reduzindo o overhead mas oferecendo menos proteção.

4. **Full Journaling**: Tanto os metadados quanto os dados são registrados, oferecendo máxima proteção.

Neste simulador, implementamos o modelo **Write-Ahead Logging**, onde cada operação é registrada em memória antes de ser executada.

---

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

O simulador utiliza uma estrutura hierárquica baseada em árvore para representar o sistema de arquivos, onde:

- **Nós internos** são representados por diretórios (`Directory`)
- **Folhas** são representadas por arquivos (`File`)
- O **nó raiz** representa o diretório raiz do sistema (`/`)

#### Classes Principais

1. **File**: Representa um arquivo no sistema
   - Atributos: nome, conteúdo, datas de criação/modificação, diretório pai
   - Métodos: getters/setters, cópia, obtenção de caminho completo

2. **Directory**: Representa um diretório no sistema
   - Atributos: nome, listas de arquivos e subdiretórios, datas, diretório pai
   - Métodos: adicionar/remover arquivos e diretórios, busca, listagem

3. **FileSystemSimulator**: Classe principal do simulador
   - Gerencia a hierarquia de arquivos e diretórios
   - Implementa todas as operações solicitadas
   - Mantém referência ao diretório atual

4. **Journal**: Gerencia o log de operações
   - Registra todas as operações antes da execução (WAL)
   - Armazena o log em memória
   - Permite auditoria das operações realizadas

5. **Shell**: Interface de linha de comando interativa
   - Permite interação com o simulador através de comandos
   - Similar a um terminal Unix/Linux

### Journaling

O journaling é implementado através da classe `Journal`, que:

1. **Estrutura do Log**: Cada entrada contém:
   - Timestamp da operação
   - Tipo de operação (CREATE_FILE, DELETE_FILE, etc.)
   - Caminho de origem
   - Caminho de destino (quando aplicável)
   - Detalhes adicionais e status

2. **Operações Registradas**: Todas as operações críticas são registradas:
   - Criação de arquivos e diretórios
   - Exclusão de arquivos e diretórios
   - Renomeação
   - Cópia
   - Modificações de conteúdo

3. **Armazenamento**: O journal é mantido em memória durante a execução, permitindo:
   - Auditoria de operações em tempo real
   - Visualização do histórico de operações
   - Rastreamento de todas as ações realizadas

4. **Write-Ahead Logging**: Cada operação é registrada **antes** de ser executada, garantindo que, mesmo em caso de falha, seja possível saber o que estava sendo feito.

---

## Parte 3: Implementação em Java

### Classe FileSystemSimulator

A classe `FileSystemSimulator` implementa o simulador do sistema de arquivos, incluindo métodos para cada operação solicitada:

- `copyFile(String sourcePath, String destPath)`: Copia um arquivo
- `deleteFile(String filePath)`: Apaga um arquivo
- `renameFile(String filePath, String newName)`: Renomeia um arquivo
- `createDirectory(String dirPath)`: Cria um diretório
- `deleteDirectory(String dirPath)`: Apaga um diretório (deve estar vazio)
- `renameDirectory(String dirPath, String newName)`: Renomeia um diretório
- `listFiles(String dirPath)`: Lista arquivos de um diretório
- `createFile(String filePath, String content)`: Cria um arquivo com conteúdo
- `changeDirectory(String dirPath)`: Muda o diretório atual

Todas as operações utilizam journaling para registrar as ações antes da execução.

### Classes File e Directory

#### Classe File

Representa um arquivo no sistema de arquivos simulado:

```java
public class File {
    private String name;
    private String content;
    private Date createdDate;
    private Date modifiedDate;
    private Directory parent;
    
    // Métodos para manipulação...
}
```

Principais funcionalidades:
- Armazenamento de conteúdo
- Rastreamento de datas de criação e modificação
- Relacionamento com diretório pai
- Geração de caminho completo
- Criação de cópias

#### Classe Directory

Representa um diretório no sistema de arquivos simulado:

```java
public class Directory {
    private String name;
    private Date createdDate;
    private Date modifiedDate;
    private Directory parent;
    private List<File> files;
    private List<Directory> subdirectories;
    
    // Métodos para manipulação...
}
```

Principais funcionalidades:
- Gerenciamento de arquivos e subdiretórios
- Busca por nome
- Listagem de conteúdo
- Geração de caminho completo
- Estrutura hierárquica

### Classe Journal

A classe `Journal` gerencia o log de operações:

```java
public class Journal {
    private List<JournalEntry> entries;
    private static final String JOURNAL_FILE = "filesystem.journal";
    
    // Métodos para registro e consulta...
}
```

Funcionalidades:
- Registro de operações com timestamp
- Persistência em arquivo
- Consulta e exibição do log
- Suporte a múltiplos tipos de operações

#### Estrutura de JournalEntry

Cada entrada no journal contém:
- Timestamp da operação
- Tipo de operação (enum)
- Caminho de origem
- Caminho de destino (opcional)
- Detalhes e status da operação

---

## Parte 4: Instalação e Funcionamento

### Requisitos

- **Java**: Versão 8 ou superior
- **Sistema Operacional**: Windows, Linux ou macOS
- **Compilador**: JDK (Java Development Kit)

### Recursos Utilizados na Implementação

1. **Java SE**: Linguagem de programação base
2. **Collections Framework**: Listas e estruturas de dados
3. **I/O Streams**: Manipulação de arquivos (journal)
4. **Scanner**: Leitura de entrada do usuário (Shell)
5. **Date/Time API**: Rastreamento de timestamps

### Compilação

Para compilar o projeto, execute no diretório raiz:

```bash
javac -d bin src/main/java/*.java
```

Ou usando o diretório de classes diretamente:

```bash
cd src/main/java
javac *.java
```

### Execução

#### Modo Shell Interativo (Recomendado)

Para executar o modo Shell interativo:

```bash
cd src/main/java
java Shell
```

Ou a partir do diretório raiz:

```bash
java -cp src/main/java Shell
```

O Shell fornece uma interface interativa similar a um terminal Unix/Linux, com os seguintes comandos disponíveis:

#### Comandos Disponíveis

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `help` | Mostra ajuda | `help` |
| `pwd` | Mostra diretório atual | `pwd` |
| `cd <caminho>` | Muda diretório | `cd /home/docs` |
| `ls [caminho]` | Lista arquivos | `ls` ou `ls /home` |
| `mkdir <caminho>` | Cria diretório | `mkdir /home/teste` |
| `rmdir <caminho>` | Remove diretório vazio | `rmdir /home/teste` |
| `touch <arquivo>` | Cria arquivo vazio | `touch arquivo.txt` |
| `echo "texto" > <arquivo>` | Cria arquivo com conteúdo | `echo "Olá" > teste.txt` |
| `cp <origem> <destino>` | Copia arquivo | `cp file1.txt file2.txt` |
| `mv <origem> <destino>` | Move/renomeia | `mv old.txt new.txt` |
| `rm <arquivo>` | Remove arquivo | `rm arquivo.txt` |
| `journal show` | Mostra o log | `journal show` |
| `journal clear` | Limpa o journal | `journal clear` |
| `exit` / `quit` | Sai do simulador | `exit` |

#### Exemplo de Uso

```
=== Simulador de Sistema de Arquivos ===
Digite 'help' para ver os comandos disponíveis
Digite 'exit' para sair

/ $ mkdir /home
Diretório criado: /home
/ $ mkdir /home/docs
Diretório criado: /home/docs
/ $ cd /home/docs
/home/docs $ echo "Conteúdo do arquivo" > teste.txt
Arquivo criado: teste.txt
/home/docs $ ls
Contents of /home/docs:
Directories:

Files:
  teste.txt
/home/docs $ cp teste.txt /home/backup.txt
Arquivo copiado com sucesso: teste.txt -> /home/backup.txt
/home/docs $ journal show
=== JOURNAL LOG ===
Total de operações: 5
...
```

#### Uso Programático

Também é possível usar o simulador de forma programática:

```java
import java.io.*;

public class ExemploUso {
    public static void main(String[] args) {
        FileSystemSimulator fs = new FileSystemSimulator();
        
        // Criar diretórios
        fs.createDirectory("/home");
        fs.createDirectory("/home/docs");
        
        // Criar arquivo
        fs.createFile("/home/docs/teste.txt", "Conteúdo do arquivo");
        
        // Listar arquivos
        System.out.println(fs.listFiles("/home/docs"));
        
        // Copiar arquivo
        fs.copyFile("/home/docs/teste.txt", "/home/backup.txt");
        
        // Mostrar journal
        System.out.println(fs.getJournal().displayJournal());
    }
}
```

### Journal

O journal mantém um log de todas as operações realizadas durante a execução do simulador. Este log é mantido em memória e pode ser visualizado a qualquer momento através do comando `journal show`.

### Estrutura de Diretórios do Projeto

```
SO/
├── src/
│   └── main/
│       └── java/
│           ├── File.java
│           ├── Directory.java
│           ├── Journal.java
│           ├── FileSystemSimulator.java
│           └── Shell.java
├── README.md
└── contexto.md
```

---

## Resultados Esperados

Espera-se que o simulador forneça insights sobre o funcionamento de um sistema de arquivos. Com base nos resultados obtidos, poderemos avaliar e entender como funciona esse elemento de um SO.

O simulador permite:

- ✅ Compreender a estrutura hierárquica de sistemas de arquivos
- ✅ Entender como operações básicas são implementadas
- ✅ Aprender sobre journaling e sua importância para integridade de dados
- ✅ Experimentar com comandos de sistema de arquivos
- ✅ Visualizar o log de operações em tempo real

### Funcionalidades Implementadas

- ✅ Copiar arquivos
- ✅ Apagar arquivos
- ✅ Renomear arquivos
- ✅ Criar diretórios
- ✅ Apagar diretórios
- ✅ Renomear diretórios
- ✅ Listar arquivos de um diretório
- ✅ Sistema de Journaling (Write-Ahead Logging)
- ✅ Modo Shell interativo
- ✅ Persistência do journal em arquivo

---

## Contribuições

Este projeto foi desenvolvido como trabalho acadêmico para a disciplina de Sistemas Operacionais.

### Autores

[Adicione os nomes dos autores aqui]

---

## Link do Repositório GitHub

🔗 **[Link do Repositório GitHub](https://github.com/seu-usuario/simulador-sistema-arquivos)**

*Nota: Atualize este link com o endereço real do seu repositório no GitHub.*

---

## Licença

Este projeto é de uso educacional. Sinta-se livre para usar e modificar conforme necessário para fins de aprendizado.

---

## Conclusão

Este simulador permite entender como funciona a organização de arquivos e diretórios em um sistema operacional. Através da implementação, foi possível compreender os conceitos de estrutura hierárquica, operações básicas de manipulação de arquivos e a importância do journaling para garantir a integridade dos dados.

---

**Desenvolvido para a disciplina de Sistemas Operacionais**

*Última atualização: 2024*

