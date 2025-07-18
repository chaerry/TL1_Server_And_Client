# TL1 Server and Client Setup Guide

## Step 1: Project Structure
Create the following directory structure:
```
tl1-project/
├── src/
│   ├── TL1Message.java
│   ├── TL1Server.java
│   └── TL1Client.java
├── bin/
└── README.md
```

## Step 2: Compile the Java Files

### Option A: Using Command Line
```bash
# Navigate to project directory
cd tl1-project

# Compile all Java files
javac -d bin src/*.java

# Or compile individually
javac -d bin src/TL1Message.java
javac -d bin src/TL1Server.java
javac -d bin src/TL1Client.java
```

### Option B: Using Maven (Optional)
If you prefer Maven, create a `pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>tl1-server-client</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>
</project>
```

## Step 3: Start the TL1 Server

### Terminal 1 (Server)
```bash
# Navigate to project directory
cd tl1-project

# Run the server
java -cp bin TL1Server

# You should see output like:
# INFO: TL1 Server started on port 8080
```

## Step 4: Start the TL1 Client

### Terminal 2 (Client - Interactive Mode)
```bash
# Navigate to project directory
cd tl1-project

# Run the client in interactive mode
java -cp bin TL1Client

# You should see:
# Server: Welcome to TL1 Server - Session: SID-1234567890
# TL1 Client Interactive Session Started
# Type 'help' for sample commands, 'quit' to exit
# TL1>
```

### Alternative: Run Sample Commands
```bash
# Run client with sample commands
java -cp bin TL1Client --samples
```

## Step 5: Test TL1 Commands

### Basic Commands to Test
In the interactive client, try these commands:

1. **Retrieve Network Element:**
   ```
   RTRV-NE:TID001::001;
   ```

2. **Edit Configuration:**
   ```
   ED-OC48:TID001:AID001:002::STATUS=ACTIVE;
   ```

3. **Enter New Circuit:**
   ```
   ENT-T1:TID001:AID002:003::RATE=1544;
   ```

4. **Delete Entry:**
   ```
   DLT-T1:TID001:AID002:004;
   ```

5. **Activate Service:**
   ```
   ACT-SVC:TID001:AID003:005;
   ```

6. **Get Help:**
   ```
   help
   ```

7. **Exit Client:**
   ```
   quit
   ```

## Step 6: Understanding TL1 Message Format

### Command Format:
```
VERB-NOUN:TID:AID:CTAG:MODIFIER1:MODIFIER2:PARAMETERS;
```

- **VERB**: Action to perform (RTRV, ED, ENT, DLT, ACT, CANC)
- **NOUN**: Object type (NE, OC48, T1, SVC, etc.)
- **TID**: Target Identifier
- **AID**: Access Identifier
- **CTAG**: Correlation Tag
- **MODIFIER1/2**: Optional modifiers
- **PARAMETERS**: Command parameters

### Response Format:
```
   TID YY-MM-DD HH:MM:SS
M  CTAG COMPLD/DENY
   "Response text"
;
```

## Step 7: Advanced Testing

### Test Multiple Clients
You can run multiple client instances to test concurrent connections:

```bash
# Terminal 3
java -cp bin TL1Client

# Terminal 4
java -cp bin TL1Client
```

### Test Error Handling
Try invalid commands to test error handling:
```
INVALID-CMD:TID001::001;
```

## Step 8: Customization Options

### Modify Server Port
Edit the `TL1Server.java` main method:
```java
TL1Server server = new TL1Server(9090); // Change port
```

### Add Custom Commands
Extend the `processCommand` method in `TL1Server.java`:
```java
case "CUSTOM":
    return handleCustomCommand(command);
```

### Database Integration
For your PostgreSQL/Oracle experience, you can add database connectivity:
```java
// Add to TL1Server.java
private Connection dbConnection;
// Initialize in constructor and use in command handlers
```

## Step 9: Running with IDE

### IntelliJ IDEA
1. Create new Java project
2. Copy the source files to `src/` directory
3. Right-click on `TL1Server.java` → Run 'TL1Server.main()'
4. Right-click on `TL1Client.java` → Run 'TL1Client.main()'

### Eclipse
1. Create new Java project
2. Copy source files to `src/` folder
3. Right-click on file → Run As → Java Application

## Step 10: Logging Configuration

### Enable Detailed Logging
Add to your VM options:
```bash
java -Djava.util.logging.config.file=logging.properties -cp bin TL1Server
```

Create `logging.properties`:
```properties
handlers=java.util.logging.ConsoleHandler
.level=INFO
java.util.logging.ConsoleHandler.level=INFO
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

## Troubleshooting

### Common Issues:
1. **Port already in use**: Change port in server or kill existing process
2. **Connection refused**: Ensure server is running before starting client
3. **ClassNotFoundException**: Check classpath and compilation

### Debug Tips:
- Check server logs for connection issues
- Verify TL1 command format (must end with semicolon)
- Test with telnet: `telnet localhost 8080`

## Next Steps

1. **Add Authentication**: Implement user login/logout
2. **Database Integration**: Store configurations in PostgreSQL/Oracle
3. **SSL/TLS Support**: Add secure communication
4. **Configuration Management**: External config files
5. **Monitoring**: Add performance metrics
6. **GUI Client**: Create Swing/JavaFX interface
