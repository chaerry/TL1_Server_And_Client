
// TL1Client.java - TL1 Client implementation
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TL1Client {
    private static final Logger logger = Logger.getLogger(TL1Client.class.getName());
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String serverHost;
    private int serverPort;
    
    public TL1Client(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }
    
    public void connect() throws IOException {
        socket = new Socket(serverHost, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        
        // Read welcome message
        String welcomeMessage = reader.readLine();
        System.out.println("Server: " + welcomeMessage);
        
        logger.info("Connected to TL1 Server at " + serverHost + ":" + serverPort);
    }
    
    public void disconnect() throws IOException {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (socket != null) socket.close();
        logger.info("Disconnected from TL1 Server");
    }
    
    public void sendCommand(String command) throws IOException {
        writer.println(command);
        logger.info("Sent: " + command);
    }
    
    public String readResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        
        // Read until we get a complete response (ends with semicolon)
        boolean responseComplete = false;
        while (!responseComplete && (line = reader.readLine()) != null) {
            response.append(line).append("\n");
            if (line.trim().equals(";")) {
                responseComplete = true;
            }
        }
        
        return response.toString();
    }
    
    public void startInteractiveSession() {
        Scanner scanner = new Scanner(System.in);
        
        try {
            connect();
            
            System.out.println("\nTL1 Client Interactive Session Started");
            System.out.println("Type 'help' for sample commands, 'quit' to exit");
            System.out.println("==========================================");
            
            while (true) {
                System.out.print("\nTL1> ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("quit")) {
                    break;
                } else if (input.equalsIgnoreCase("help")) {
                    showHelp();
                } else if (!input.isEmpty()) {
                    try {
                        sendCommand(input);
                        String response = readResponse();
                        System.out.println("Response:" + response);
                    } catch (IOException e) {
                        System.err.println("Error communicating with server: " + e.getMessage());
                        break;
                    }
                }
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error", e);
            System.err.println("Failed to connect to server: " + e.getMessage());
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error during disconnect", e);
            }
            scanner.close();
        }
    }
    
    private void showHelp() {
        System.out.println("\nSample TL1 Commands:");
        System.out.println("-------------------");
        System.out.println("RTRV-NE:TID001::001;");
        System.out.println("  - Retrieve network element information");
        System.out.println("ED-OC48:TID001:AID001:002::STATUS=ACTIVE;");
        System.out.println("  - Edit OC-48 configuration");
        System.out.println("ENT-T1:TID001:AID002:003::RATE=1544;");
        System.out.println("  - Enter new T1 circuit");
        System.out.println("DLT-T1:TID001:AID002:004;");
        System.out.println("  - Delete T1 circuit");
        System.out.println("ACT-SVC:TID001:AID003:005;");
        System.out.println("  - Activate service");
        System.out.println("CANC-OP:TID001::006;");
        System.out.println("  - Cancel operation");
        System.out.println("\nCommand Format: VERB-NOUN:TID:AID:CTAG:MODIFIER1:MODIFIER2:PARAMETERS;");
    }
    
    // Utility method to send predefined commands
    public void sendSampleCommands() {
        try {
            connect();
            
            // Send sample commands
            String[] commands = {
                "RTRV-NE:TID001::001;",
                "ED-OC48:TID001:AID001:002::STATUS=ACTIVE;",
                "ENT-T1:TID001:AID002:003::RATE=1544;",
                "ACT-SVC:TID001:AID003:004;"
            };
            
            for (String command : commands) {
                System.out.println("\nSending: " + command);
                sendCommand(command);
                String response = readResponse();
                System.out.println("Response:" + response);
                Thread.sleep(1000); // Wait between commands
            }
            
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error sending sample commands", e);
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error during disconnect", e);
            }
        }
    }
    
    // Main method to start the client
    public static void main(String[] args) {
        TL1Client client = new TL1Client("localhost", 8080);
        
        if (args.length > 0 && args[0].equals("--samples")) {
            client.sendSampleCommands();
        } else {
            client.startInteractiveSession();
        }
    }
}
