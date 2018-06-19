import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.ArrayList;

public class ChatServer {

    private static ArrayList<String> names = new ArrayList<String>();
    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
    private static ArrayList<Socket> sockets = new ArrayList<Socket>();
    static Thread handle;

    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        int portNum = Integer.valueOf(args[0]);
       // System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(portNum);

        try {
            while (true) {
                Socket socket = listener.accept();
                sockets.add(socket);
                handle = new Handler(socket);
                handle.start();
            }
        } 
        catch(Exception e){}
    }

    //handle threads created when listening for clients
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        //asks for unique name
        //then starts the messaging
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                //ask for name
                while (true) {
                    //out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            writers.add(out);
                            // System.out.println(name + " has joined the chat room");
                            break;
                        }
                    }
                }

                for(int i = 0; i < names.size(); i++){
                    System.out.println(names.get(i));
                }
                //out.println("NAMEACCEPTED");
                //writers.add(out);

                while (true) {
                    String input = in.readLine();
                    //System.out.println(input);
                    if(input.equalsIgnoreCase("x")){           
                        System.out.println("null message recieved");
                        for(int i = 0; i < names.size(); i++){
                            if(name == names.get(i)){
                                writers.get(i).println("null");
                                System.out.println("null message sent back");

                                socket.shutdownInput();
                                System.out.println("input closed");
                                
                                socket.shutdownOutput();
                                System.out.println("output closed");

                                socket.close();
                                System.out.println("socket closed");
                                break;
                            }
                        }

                        break;
                    }
                    else if(input.equalsIgnoreCase("f")){
                        // System.out.println("in server send part");
                        String tempName = in.readLine();

                        String fileName = in.readLine();
                        int filePort = Integer.parseInt(in.readLine());
                        System.out.println(tempName + " " + fileName + " " + filePort);

                        for(int i = 0; i < names.size(); i++){
                            System.out.println(tempName + " comapare to " + names.get(i));
                            if(tempName.equalsIgnoreCase(names.get(i))){
                                System.out.println("sending info the client");
                                writers.get(i).println("f");
                                writers.get(i).println(fileName);
                                writers.get(i).println(filePort);
                                // FileSend fS = new FileSend(filePort, fileName);
                                // Thread sFile = new Thread(fS);
                                // sFile.start();
                            }
                        }
                    }
                    else if(!input.equalsIgnoreCase("NULL")){
                        for(int i = 0; i < names.size(); i++){
                            if(name != names.get(i)){
                                writers.get(i).println(name + ": " + input);
                            }
                        }
                    }
                   
                }
            } 
            catch (IOException e) {
                System.out.println(e);
            } 
            finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    // System.out.println(name + " has left the chat room");
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                    // System.out.println("writer removed");
                }
                // socket.close(); //i think this is redundant
                if (socket != null){
                    sockets.remove(socket);
                    // System.out.println("socket removed");
                }
            }
        }
    }
}