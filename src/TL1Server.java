// TL1Server.java - TL1 Server implementation
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TL1Server {
    private static final Logger logger = Logger.getLogger(TL1Server.class.getName());
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running = false;
    private int port;
    
    public TL1Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }
    
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        logger.info("TL1 Server started on port " + port);
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new TL1ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    logger.log(Level.SEVERE, "Error accepting client connection", e);
                }
            }
        }
    }
    
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        threadPool.shutdown();
        logger.info("TL1 Server stopped");
    }
    
    // Inner class to handle client connections
    private class TL1ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String sessionId;
        
        public TL1ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.sessionId = "SID-" + System.currentTimeMillis();
        }
        
        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                
                logger.info("Client connected: " + clientSocket.getInetAddress() + " - Session: " + sessionId);
                
                // Send welcome message
                writer.println("Welcome to TL1 Server - Session: " + sessionId);
                
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    logger.info("Received: " + inputLine);
                    
                    try {
                        TL1Message command = TL1Message.parseCommand(inputLine);
                        TL1Message response = processCommand(command);
                        writer.print(response.formatResponse());
                        writer.flush();
                    } catch (Exception e) {
                        // Send error response
                        TL1Message errorResponse = new TL1Message("UNKNOWN", "001", "DENY", 
                            "Invalid command format: " + e.getMessage());
                        writer.print(errorResponse.formatResponse());
                        writer.flush();
                    }
                }
                
            } catch (IOException e) {
                logger.log(Level.WARNING, "Client connection error", e);
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (writer != null) writer.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing client connection", e);
                }
                logger.info("Client disconnected: " + sessionId);
            }
        }
        
        private TL1Message processCommand(TL1Message command) {
            // Simple command processing - you can extend this
            switch (command.getVerb().toUpperCase()) {
                case "RTRV":
                    return handleRetrieve(command);
                case "ED":
                    return handleEdit(command);
                case "ENT":
                    return handleEnter(command);
                case "DLT":
                    return handleDelete(command);
                case "ACT":
                    return handleActivate(command);
                case "CANC":
                    return handleCancel(command);
                default:
                    return new TL1Message(command.getTid(), command.getCtag(), "DENY", 
                        "Unknown command: " + command.getVerb());
            }
        }
        
        private TL1Message handleRetrieve(TL1Message command) {
            // Simulate retrieving network element information
            String responseText = "Network Element Status: ACTIVE, Type: OC-48, Location: Site-A";
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", responseText);
        }
        
        private TL1Message handleEdit(TL1Message command) {
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", 
                "Configuration updated successfully");
        }
        
        private TL1Message handleEnter(TL1Message command) {
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", 
                "New entry created successfully");
        }
        
        private TL1Message handleDelete(TL1Message command) {
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", 
                "Entry deleted successfully");
        }
        
        private TL1Message handleActivate(TL1Message command) {
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", 
                "Service activated successfully");
        }
        
        private TL1Message handleCancel(TL1Message command) {
            return new TL1Message(command.getTid(), command.getCtag(), "COMPLD", 
                "Operation cancelled successfully");
        }
    }
    
    // Main method to start the server
    public static void main(String[] args) {
        TL1Server server = new TL1Server(8080);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error stopping server", e);
            }
        }));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start server", e);
        }
    }
}
