import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Gerencia o log de operações (journaling)
public class Journal {
    private List<JournalEntry> entries;

    public Journal() {
        this.entries = new ArrayList<>();
    }

    public enum OperationType {
        CREATE_FILE,
        DELETE_FILE,
        RENAME_FILE,
        COPY_FILE,
        CREATE_DIRECTORY,
        DELETE_DIRECTORY,
        RENAME_DIRECTORY,
        WRITE_FILE
    }

    public static class JournalEntry {
        private OperationType operation;
        private String source;
        private String destination;
        private String timestamp;
        private String details;

        public JournalEntry(OperationType operation, String source, String destination, String details) {
            this.operation = operation;
            this.source = source;
            this.destination = destination;
            this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.details = details;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(timestamp).append(" | ");
            sb.append(operation.toString()).append(" | ");
            sb.append("Source: ").append(source != null ? source : "N/A");
            if (destination != null && !destination.isEmpty()) {
                sb.append(" | Destination: ").append(destination);
            }
            if (details != null && !details.isEmpty()) {
                sb.append(" | ").append(details);
            }
            return sb.toString();
        }

        public OperationType getOperation() { return operation; }
        public String getSource() { return source; }
        public String getDestination() { return destination; }
        public String getTimestamp() { return timestamp; }
        public String getDetails() { return details; }
    }

    public void logOperation(OperationType operation, String source, String destination, String details) {
        JournalEntry entry = new JournalEntry(operation, source, destination, details);
        entries.add(entry);
    }

    public void logOperation(OperationType operation, String source, String details) {
        logOperation(operation, source, null, details);
    }

    public List<JournalEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public List<JournalEntry> getLastEntries(int n) {
        int start = Math.max(0, entries.size() - n);
        return new ArrayList<>(entries.subList(start, entries.size()));
    }

    public void clearJournal() {
        entries.clear();
    }

    public String displayJournal() {
        if (entries.isEmpty()) {
            return "Journal está vazio.\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== JOURNAL LOG ===\n");
        sb.append("Total de operações: ").append(entries.size()).append("\n\n");
        
        for (JournalEntry entry : entries) {
            sb.append(entry.toString()).append("\n");
        }
        
        return sb.toString();
    }
}

