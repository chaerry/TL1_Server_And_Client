// TL1Message.java - Message structure for TL1 communication
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TL1Message {
    private String verb;
    private String noun;
    private String modifier1;
    private String modifier2;
    private String tid;
    private String aid;
    private String ctag;
    private String parameters;
    private String timestamp;
    private String completionCode;
    private String responseText;
    
    // Constructor for command messages
    public TL1Message(String verb, String noun, String modifier1, String modifier2, 
                     String tid, String aid, String ctag, String parameters) {
        this.verb = verb;
        this.noun = noun;
        this.modifier1 = modifier1;
        this.modifier2 = modifier2;
        this.tid = tid;
        this.aid = aid;
        this.ctag = ctag;
        this.parameters = parameters;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"));
    }
    
    // Constructor for response messages
    public TL1Message(String tid, String ctag, String completionCode, String responseText) {
        this.tid = tid;
        this.ctag = ctag;
        this.completionCode = completionCode;
        this.responseText = responseText;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"));
    }
    
    // Format command message as TL1 string
    public String formatCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append(verb);
        if (noun != null && !noun.isEmpty()) {
            sb.append("-").append(noun);
        }
        sb.append(":").append(tid);
        if (aid != null && !aid.isEmpty()) {
            sb.append(":").append(aid);
        }
        sb.append(":").append(ctag);
        if (modifier1 != null && !modifier1.isEmpty()) {
            sb.append(":").append(modifier1);
        }
        if (modifier2 != null && !modifier2.isEmpty()) {
            sb.append(":").append(modifier2);
        }
        if (parameters != null && !parameters.isEmpty()) {
            sb.append(":").append(parameters);
        }
        sb.append(";");
        return sb.toString();
    }
    
    // Format response message as TL1 string
    public String formatResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n   ").append(tid).append(" ").append(timestamp).append("\n");
        sb.append("M  ").append(ctag).append(" ").append(completionCode).append("\n");
        if (responseText != null && !responseText.isEmpty()) {
            sb.append("   \"").append(responseText).append("\"\n");
        }
        sb.append(";\n");
        return sb.toString();
    }
    
    // Parse TL1 command string
    public static TL1Message parseCommand(String command) {
        try {
            command = command.trim();
            if (command.endsWith(";")) {
                command = command.substring(0, command.length() - 1);
            }
            
            String[] parts = command.split(":");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid TL1 command format");
            }
            
            String verbNoun = parts[0];
            String[] verbNounParts = verbNoun.split("-");
            String verb = verbNounParts[0];
            String noun = verbNounParts.length > 1 ? verbNounParts[1] : "";
            
            String tid = parts[1];
            String aid = parts.length > 2 ? parts[2] : "";
            String ctag = parts.length > 3 ? parts[3] : "";
            String modifier1 = parts.length > 4 ? parts[4] : "";
            String modifier2 = parts.length > 5 ? parts[5] : "";
            String parameters = parts.length > 6 ? parts[6] : "";
            
            return new TL1Message(verb, noun, modifier1, modifier2, tid, aid, ctag, parameters);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse TL1 command: " + e.getMessage());
        }
    }
    
    // Getters
    public String getVerb() { return verb; }
    public String getNoun() { return noun; }
    public String getTid() { return tid; }
    public String getAid() { return aid; }
    public String getCtag() { return ctag; }
    public String getParameters() { return parameters; }
    public String getCompletionCode() { return completionCode; }
    public String getResponseText() { return responseText; }
}
