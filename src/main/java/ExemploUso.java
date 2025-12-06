public class ExemploUso {
    public static void main(String[] args) {
        System.out.println("=== Exemplo de Uso do FileSystemSimulator ===\n");
        
        // Cria uma instância do simulador
        FileSystemSimulator fs = new FileSystemSimulator();
        
        // Exemplo 1: Criar estrutura de diretórios
        System.out.println("1. Criando estrutura de diretórios...");
        fs.createDirectory("/home");
        fs.createDirectory("/home/usuario");
        fs.createDirectory("/home/usuario/documentos");
        fs.createDirectory("/home/usuario/imagens");
        System.out.println();
        
        // Exemplo 2: Criar arquivos
        System.out.println("2. Criando arquivos...");
        fs.createFile("/home/usuario/documentos/readme.txt", "Este é um arquivo de exemplo");
        fs.createFile("/home/usuario/documentos/notas.txt", "Notas importantes:\n- Item 1\n- Item 2");
        fs.createFile("/home/usuario/imagens/descricao.txt", "Descrição das imagens");
        System.out.println();
        
        // Exemplo 3: Listar conteúdo de diretórios
        System.out.println("3. Listando conteúdo de diretórios:");
        System.out.println(fs.listFiles("/home/usuario"));
        System.out.println(fs.listFiles("/home/usuario/documentos"));
        System.out.println();
        
        // Exemplo 4: Copiar arquivo
        System.out.println("4. Copiando arquivo...");
        fs.copyFile("/home/usuario/documentos/readme.txt", "/home/usuario/documentos/readme_backup.txt");
        System.out.println();
        
        // Exemplo 5: Renomear arquivo
        System.out.println("5. Renomeando arquivo...");
        fs.renameFile("/home/usuario/documentos/notas.txt", "notas_importantes.txt");
        System.out.println();
        
        // Exemplo 6: Renomear diretório
        System.out.println("6. Renomeando diretório...");
        fs.renameDirectory("/home/usuario/imagens", "fotos");
        System.out.println();
        
        // Exemplo 7: Listar novamente após modificações
        System.out.println("7. Listando após modificações:");
        System.out.println(fs.listFiles("/home/usuario/documentos"));
        System.out.println();
        
        // Exemplo 8: Mostrar o journal
        System.out.println("8. Exibindo o journal de operações:");
        System.out.println(fs.getJournal().displayJournal());
        
        System.out.println("\n=== Exemplo concluído ===");
    }
}

