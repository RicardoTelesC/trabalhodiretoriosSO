import java.util.Scanner;

public class Shell {
    private FileSystemSimulator fileSystem;
    private Scanner scanner;
    private boolean running;

    public Shell() {
        this.fileSystem = new FileSystemSimulator();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        System.out.println("=== Simulador de Sistema de Arquivos ===");
        System.out.println("Digite 'help' para ver os comandos disponíveis");
        System.out.println("Digite 'exit' para sair\n");

        while (running) {
            try {
                System.out.print(fileSystem.getCurrentDirectory() + " $ ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                executeCommand(input);
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void executeCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "help":
                showHelp();
                break;
            case "exit":
            case "quit":
                running = false;
                System.out.println("Saindo do simulador...");
                break;
            case "pwd":
                System.out.println(fileSystem.getCurrentDirectory());
                break;
            case "cd":
                if (args.isEmpty()) {
                    fileSystem.changeDirectory("/");
                } else {
                    fileSystem.changeDirectory(args);
                }
                break;
            case "ls":
                System.out.println(fileSystem.listFiles(args.isEmpty() ? null : args));
                break;
            case "mkdir":
                if (args.isEmpty()) {
                    System.out.println("Uso: mkdir <caminho_do_diretorio>");
                } else {
                    fileSystem.createDirectory(args);
                }
                break;
            case "rmdir":
                if (args.isEmpty()) {
                    System.out.println("Uso: rmdir <caminho_do_diretorio>");
                } else {
                    fileSystem.deleteDirectory(args);
                }
                break;
            case "rm":
                if (args.isEmpty()) {
                    System.out.println("Uso: rm <caminho_do_arquivo>");
                } else {
                    fileSystem.deleteFile(args);
                }
                break;
            case "cp":
                String[] cpArgs = args.split("\\s+", 2);
                if (cpArgs.length != 2) {
                    System.out.println("Uso: cp <origem> <destino>");
                } else {
                    fileSystem.copyFile(cpArgs[0], cpArgs[1]);
                }
                break;
            case "mv":
                String[] mvArgs = args.split("\\s+", 2);
                if (mvArgs.length != 2) {
                    System.out.println("Uso: mv <origem> <novo_nome>");
                    System.out.println("Nota: Para mover entre diretórios, use 'cp <origem> <destino>' e depois 'rm <origem>'");
                } else {
                    String sourcePath = mvArgs[0];
                    String newName = mvArgs[1];
                    
                    // Tenta renomear como arquivo primeiro, depois como diretório
                    boolean fileRenamed = fileSystem.renameFile(sourcePath, newName);
                    if (!fileRenamed) {
                        // Se não conseguiu renomear como arquivo, tenta como diretório
                        fileSystem.renameDirectory(sourcePath, newName);
                    }
                }
                break;
            case "touch":
                if (args.isEmpty()) {
                    System.out.println("Uso: touch <caminho_do_arquivo>");
                } else {
                    fileSystem.createFile(args, "");
                }
                break;
            case "echo":
                // echo "texto" > arquivo
                if (args.contains(">")) {
                    String[] echoArgs = args.split(">", 2);
                    String text = echoArgs[0].trim().replaceAll("^\"|\"$", "");
                    String filePath = echoArgs[1].trim();
                    fileSystem.createFile(filePath, text);
                } else {
                    System.out.println(args);
                }
                break;
            case "journal":
                if (args.equals("show")) {
                    System.out.println(fileSystem.getJournal().displayJournal());
                } else if (args.equals("clear")) {
                    fileSystem.getJournal().clearJournal();
                    System.out.println("Journal limpo.");
                } else {
                    System.out.println("Uso: journal [show|clear]");
                }
                break;
            default:
                System.out.println("Comando não reconhecido: " + cmd);
                System.out.println("Digite 'help' para ver os comandos disponíveis");
        }
    }

    private void showHelp() {
        System.out.println("\n=== Comandos Disponíveis ===\n");
        System.out.println("  help              - Mostra esta ajuda");
        System.out.println("  exit / quit       - Sai do simulador");
        System.out.println("  pwd               - Mostra o diretório atual");
        System.out.println("  cd <caminho>      - Muda o diretório atual");
        System.out.println("  ls [caminho]      - Lista arquivos e diretórios");
        System.out.println("  mkdir <caminho>   - Cria um diretório");
        System.out.println("  rmdir <caminho>   - Remove um diretório (deve estar vazio)");
        System.out.println("  touch <arquivo>   - Cria um arquivo vazio");
        System.out.println("  echo \"texto\" > <arquivo> - Cria arquivo com conteúdo");
        System.out.println("  cp <origem> <destino> - Copia um arquivo");
        System.out.println("  mv <origem> <destino> - Move/renomeia arquivo ou diretório");
        System.out.println("  rm <arquivo>      - Remove um arquivo");
        System.out.println("  journal show      - Mostra o log de operações (journal)");
        System.out.println("  journal clear     - Limpa o journal\n");
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.start();
    }
}

