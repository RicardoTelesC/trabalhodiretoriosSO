public class FileSystemSimulator {
    private Directory root;
    private Directory currentDirectory;
    private Journal journal;

    public FileSystemSimulator() {
        this.root = new Directory("/", null);
        this.currentDirectory = root;
        this.journal = new Journal();
        journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, "/", "Sistema de arquivos inicializado");
    }

    /**
     * Copia um arquivo de uma localização para outra
     * @param sourcePath Caminho do arquivo de origem
     * @param destPath Caminho de destino
     * @return true se a operação foi bem-sucedida
     */
    public boolean copyFile(String sourcePath, String destPath) {
        try {
            // Log antes da operação (write-ahead logging)
            journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Iniciando cópia");
            
            File sourceFile = findFile(sourcePath);
            if (sourceFile == null) {
                System.out.println("Erro: Arquivo de origem não encontrado: " + sourcePath);
                journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Falha: arquivo não encontrado");
                return false;
            }

            Directory destDir = findDirectory(destPath);
            if (destDir == null) {
                // Tenta extrair o diretório do caminho de destino
                String[] destParts = destPath.split("/");
                String fileName = destParts[destParts.length - 1];
                String dirPath = destPath.substring(0, destPath.length() - fileName.length());
                if (dirPath.isEmpty()) {
                    dirPath = currentDirectory.getFullPath();
                }
                
                destDir = findDirectory(dirPath);
                if (destDir == null) {
                    System.out.println("Erro: Diretório de destino não encontrado: " + dirPath);
                    journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Falha: diretório destino não encontrado");
                    return false;
                }
                
                // Verifica se já existe arquivo com o mesmo nome
                if (destDir.findFile(fileName) != null) {
                    System.out.println("Erro: Arquivo já existe no destino: " + fileName);
                    journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Falha: arquivo já existe");
                    return false;
                }
                
                File newFile = sourceFile.copy(destDir);
                newFile.setName(fileName);
                destDir.addFile(newFile);
                journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Cópia realizada com sucesso");
                System.out.println("Arquivo copiado com sucesso: " + sourcePath + " -> " + destPath);
                return true;
            }
            
            // Se destPath é um diretório, usa o nome original do arquivo
            File newFile = sourceFile.copy(destDir);
            destDir.addFile(newFile);
            journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Cópia realizada com sucesso");
            System.out.println("Arquivo copiado com sucesso: " + sourcePath + " -> " + destPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao copiar arquivo: " + e.getMessage());
            journal.logOperation(Journal.OperationType.COPY_FILE, sourcePath, destPath, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Apaga um arquivo
     * @param filePath Caminho do arquivo
     * @return true se a operação foi bem-sucedida
     */
    public boolean deleteFile(String filePath) {
        try {
            journal.logOperation(Journal.OperationType.DELETE_FILE, filePath, "Iniciando exclusão");
            
            File file = findFile(filePath);
            if (file == null) {
                System.out.println("Erro: Arquivo não encontrado: " + filePath);
                journal.logOperation(Journal.OperationType.DELETE_FILE, filePath, "Falha: arquivo não encontrado");
                return false;
            }

            Directory parent = file.getParent();
            if (parent != null && parent.removeFile(file)) {
                journal.logOperation(Journal.OperationType.DELETE_FILE, filePath, "Arquivo excluído com sucesso");
                System.out.println("Arquivo excluído: " + filePath);
                return true;
            }

            journal.logOperation(Journal.OperationType.DELETE_FILE, filePath, "Falha na exclusão");
            return false;
            
        } catch (Exception e) {
            System.out.println("Erro ao apagar arquivo: " + e.getMessage());
            journal.logOperation(Journal.OperationType.DELETE_FILE, filePath, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Renomeia um arquivo
     * @param filePath Caminho do arquivo
     * @param newName Novo nome do arquivo
     * @return true se a operação foi bem-sucedida
     */
    public boolean renameFile(String filePath, String newName) {
        try {
            journal.logOperation(Journal.OperationType.RENAME_FILE, filePath, newName, "Iniciando renomeação");
            
            File file = findFile(filePath);
            if (file == null) {
                System.out.println("Erro: Arquivo não encontrado: " + filePath);
                journal.logOperation(Journal.OperationType.RENAME_FILE, filePath, newName, "Falha: arquivo não encontrado");
                return false;
            }

            Directory parent = file.getParent();
            if (parent != null && parent.findFile(newName) != null) {
                System.out.println("Erro: Já existe um arquivo com o nome: " + newName);
                journal.logOperation(Journal.OperationType.RENAME_FILE, filePath, newName, "Falha: nome já existe");
                return false;
            }

            file.setName(newName);
            journal.logOperation(Journal.OperationType.RENAME_FILE, filePath, newName, "Renomeação realizada com sucesso");
            System.out.println("Arquivo renomeado: " + filePath + " -> " + newName);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao renomear arquivo: " + e.getMessage());
            journal.logOperation(Journal.OperationType.RENAME_FILE, filePath, newName, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cria um diretório
     * @param dirPath Caminho do novo diretório
     * @return true se a operação foi bem-sucedida
     */
    public boolean createDirectory(String dirPath) {
        try {
            journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, dirPath, "Iniciando criação");
            
            // Normaliza o caminho
            if (!dirPath.startsWith("/")) {
                dirPath = currentDirectory.getFullPath() + "/" + dirPath;
            }
            
            // Verifica se o diretório já existe
            Directory existing = findDirectory(dirPath);
            if (existing != null) {
                System.out.println("Erro: Diretório já existe: " + dirPath);
                journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, dirPath, "Falha: diretório já existe");
                return false;
            }
            
            // Determina o diretório pai e o nome do novo diretório
            String[] parts = dirPath.split("/");
            String dirName = parts[parts.length - 1];
            
            // Constrói o caminho do diretório pai
            StringBuilder parentPathBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) {
                    parentPathBuilder.append("/").append(parts[i]);
                }
            }
            String parentPath = parentPathBuilder.length() == 0 ? "/" : parentPathBuilder.toString();
            
            Directory parent = findDirectory(parentPath);
            if (parent == null) {
                System.out.println("Erro: Diretório pai não encontrado: " + parentPath);
                journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, dirPath, "Falha: diretório pai não encontrado");
                return false;
            }

            Directory newDir = new Directory(dirName, parent);
            parent.addSubdirectory(newDir);
            journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, dirPath, "Diretório criado com sucesso");
            System.out.println("Diretório criado: " + dirPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao criar diretório: " + e.getMessage());
            journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, dirPath, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Apaga um diretório (deve estar vazio)
     * @param dirPath Caminho do diretório
     * @return true se a operação foi bem-sucedida
     */
    public boolean deleteDirectory(String dirPath) {
        try {
            journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Iniciando exclusão");
            
            Directory dir = findDirectory(dirPath);
            if (dir == null) {
                System.out.println("Erro: Diretório não encontrado: " + dirPath);
                journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Falha: diretório não encontrado");
                return false;
            }

            if (dir == root) {
                System.out.println("Erro: Não é possível excluir o diretório raiz");
                journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Falha: não é possível excluir raiz");
                return false;
            }

            if (!dir.getFiles().isEmpty() || !dir.getSubdirectories().isEmpty()) {
                System.out.println("Erro: Diretório não está vazio. Use 'rm -r' para remover recursivamente (não implementado)");
                journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Falha: diretório não vazio");
                return false;
            }

            Directory parent = dir.getParent();
            if (parent != null && parent.removeSubdirectory(dir)) {
                journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Diretório excluído com sucesso");
                System.out.println("Diretório excluído: " + dirPath);
                return true;
            }

            journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Falha na exclusão");
            return false;
            
        } catch (Exception e) {
            System.out.println("Erro ao apagar diretório: " + e.getMessage());
            journal.logOperation(Journal.OperationType.DELETE_DIRECTORY, dirPath, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Renomeia um diretório
     * @param dirPath Caminho do diretório
     * @param newName Novo nome do diretório
     * @return true se a operação foi bem-sucedida
     */
    public boolean renameDirectory(String dirPath, String newName) {
        try {
            journal.logOperation(Journal.OperationType.RENAME_DIRECTORY, dirPath, newName, "Iniciando renomeação");
            
            Directory dir = findDirectory(dirPath);
            if (dir == null) {
                System.out.println("Erro: Diretório não encontrado: " + dirPath);
                journal.logOperation(Journal.OperationType.RENAME_DIRECTORY, dirPath, newName, "Falha: diretório não encontrado");
                return false;
            }

            Directory parent = dir.getParent();
            if (parent != null && parent.findSubdirectory(newName) != null) {
                System.out.println("Erro: Já existe um diretório com o nome: " + newName);
                journal.logOperation(Journal.OperationType.RENAME_DIRECTORY, dirPath, newName, "Falha: nome já existe");
                return false;
            }

            dir.setName(newName);
            journal.logOperation(Journal.OperationType.RENAME_DIRECTORY, dirPath, newName, "Renomeação realizada com sucesso");
            System.out.println("Diretório renomeado: " + dirPath + " -> " + newName);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao renomear diretório: " + e.getMessage());
            journal.logOperation(Journal.OperationType.RENAME_DIRECTORY, dirPath, newName, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista os arquivos de um diretório
     * @param dirPath Caminho do diretório (null para diretório atual)
     * @return String com a listagem
     */
    public String listFiles(String dirPath) {
        Directory targetDir;
        
        if (dirPath == null || dirPath.isEmpty()) {
            targetDir = currentDirectory;
        } else {
            targetDir = findDirectory(dirPath);
            if (targetDir == null) {
                return "Erro: Diretório não encontrado: " + dirPath;
            }
        }

        journal.logOperation(Journal.OperationType.CREATE_DIRECTORY, targetDir.getFullPath(), "Listagem solicitada");
        return targetDir.listContents();
    }

    /**
     * Cria um arquivo com conteúdo
     * @param filePath Caminho do arquivo
     * @param content Conteúdo do arquivo
     * @return true se a operação foi bem-sucedida
     */
    public boolean createFile(String filePath, String content) {
        try {
            journal.logOperation(Journal.OperationType.CREATE_FILE, filePath, "Iniciando criação");
            
            // Normaliza o caminho
            if (!filePath.startsWith("/")) {
                filePath = currentDirectory.getFullPath() + "/" + filePath;
            }
            
            // Verifica se o arquivo já existe
            File existing = findFile(filePath);
            if (existing != null) {
                System.out.println("Erro: Arquivo já existe: " + filePath);
                journal.logOperation(Journal.OperationType.CREATE_FILE, filePath, "Falha: arquivo já existe");
                return false;
            }
            
            String[] parts = filePath.split("/");
            String fileName = parts[parts.length - 1];
            
            // Constrói o caminho do diretório pai
            StringBuilder dirPathBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (!parts[i].isEmpty()) {
                    dirPathBuilder.append("/").append(parts[i]);
                }
            }
            String dirPath = dirPathBuilder.length() == 0 ? "/" : dirPathBuilder.toString();
            
            Directory parent = findDirectory(dirPath);
            if (parent == null) {
                System.out.println("Erro: Diretório pai não encontrado: " + dirPath);
                journal.logOperation(Journal.OperationType.CREATE_FILE, filePath, "Falha: diretório pai não encontrado");
                return false;
            }

            File newFile = new File(fileName, content, parent);
            parent.addFile(newFile);
            journal.logOperation(Journal.OperationType.CREATE_FILE, filePath, "Arquivo criado com sucesso");
            System.out.println("Arquivo criado: " + filePath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao criar arquivo: " + e.getMessage());
            journal.logOperation(Journal.OperationType.CREATE_FILE, filePath, "Falha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca um arquivo pelo caminho
     * @param filePath Caminho do arquivo
     * @return Arquivo encontrado ou null
     */
    private File findFile(String filePath) {
        if (filePath.startsWith("/")) {
            // Caminho absoluto
            return findFileAbsolute(filePath);
        } else {
            // Caminho relativo
            return findFileRelative(filePath);
        }
    }

    /**
     * Busca um arquivo usando caminho absoluto
     * @param filePath Caminho absoluto
     * @return Arquivo encontrado ou null
     */
    private File findFileAbsolute(String filePath) {
        String[] parts = filePath.split("/");
        Directory current = root;
        
        for (int i = 1; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                current = current.findSubdirectory(parts[i]);
                if (current == null) {
                    return null;
                }
            }
        }
        
        return current.findFile(parts[parts.length - 1]);
    }

    /**
     * Busca um arquivo usando caminho relativo
     * @param filePath Caminho relativo
     * @return Arquivo encontrado ou null
     */
    private File findFileRelative(String filePath) {
        String[] parts = filePath.split("/");
        Directory current = currentDirectory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                if (parts[i].equals("..")) {
                    current = current.getParent();
                    if (current == null) current = root;
                } else if (!parts[i].equals(".")) {
                    current = current.findSubdirectory(parts[i]);
                    if (current == null) {
                        return null;
                    }
                }
            }
        }
        
        return current.findFile(parts[parts.length - 1]);
    }

    /**
     * Busca um diretório pelo caminho
     * @param dirPath Caminho do diretório
     * @return Diretório encontrado ou null
     */
    private Directory findDirectory(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            return currentDirectory;
        }
        
        if (dirPath.equals("/")) {
            return root;
        }
        
        if (dirPath.startsWith("/")) {
            return findDirectoryAbsolute(dirPath);
        } else {
            return findDirectoryRelative(dirPath);
        }
    }

    /**
     * Busca um diretório usando caminho absoluto
     * @param dirPath Caminho absoluto
     * @return Diretório encontrado ou null
     */
    private Directory findDirectoryAbsolute(String dirPath) {
        String[] parts = dirPath.split("/");
        Directory current = root;
        
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                current = current.findSubdirectory(parts[i]);
                if (current == null) {
                    return null;
                }
            }
        }
        
        return current;
    }

    /**
     * Busca um diretório usando caminho relativo
     * @param dirPath Caminho relativo
     * @return Diretório encontrado ou null
     */
    private Directory findDirectoryRelative(String dirPath) {
        String[] parts = dirPath.split("/");
        Directory current = currentDirectory;
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (part.equals("..")) {
                    current = current.getParent();
                    if (current == null) current = root;
                } else if (!part.equals(".")) {
                    current = current.findSubdirectory(part);
                    if (current == null) {
                        return null;
                    }
                }
            }
        }
        
        return current;
    }

    /**
     * Muda o diretório atual
     * @param dirPath Caminho do novo diretório
     * @return true se a operação foi bem-sucedida
     */
    public boolean changeDirectory(String dirPath) {
        Directory targetDir = findDirectory(dirPath);
        if (targetDir != null) {
            currentDirectory = targetDir;
            return true;
        }
        System.out.println("Erro: Diretório não encontrado: " + dirPath);
        return false;
    }

    /**
     * Retorna o diretório atual
     * @return Caminho do diretório atual
     */
    public String getCurrentDirectory() {
        return currentDirectory.getFullPath();
    }

    /**
     * Retorna a instância do journal
     * @return Journal
     */
    public Journal getJournal() {
        return journal;
    }

    /**
     * Retorna o diretório raiz
     * @return Diretório raiz
     */
    public Directory getRoot() {
        return root;
    }
}

