import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ChatClient{
    static FromStd fromStd;
    static Thread fStd;

    static Echo echo;
    static Thread e;

    static BufferedReader stdInput;
    static PrintWriter output;
    static String source = "temp";
    static int fPort;
    public static void main(String[] args){

        runAsClient(args);

    }

    public static void runAsClient(String[] args){

        try {
            int port_number = Integer.valueOf( args[3] );
            fPort = Integer.valueOf(args[1]);
            
            Socket client_socket= new Socket( "localhost", port_number );

            echo = new Echo(client_socket);
            e = new Thread(echo);
            e.start();

            fromStd = new FromStd(client_socket, fPort);
            fStd = new Thread(fromStd);
            fStd.start();
            
        }
        catch ( Exception e ){
        }

    }

}

class FromStd implements Runnable{

    BufferedReader stdInput;
    PrintWriter output;
    String source = "temp";
    Socket client_socket;
    boolean started = false;
    int fPort;
    public FromStd(Socket client_socket, int fPort){
        this.client_socket = client_socket;
        this.fPort = fPort;
        try{
            //handles stdInput
            stdInput= new BufferedReader(new InputStreamReader(System.in));
            //outputs to other
            output = new PrintWriter( client_socket.getOutputStream(), true );
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    @Override 
    public void run(){
        try{
            source = stdInput.readLine();
            output.println(source);
            while(!client_socket.isClosed()){
                if(true){
                    System.out.println("Enter an option (m, f, x): ");
                    System.out.println("   (M)essage (send)");
                    System.out.println("   (F)ile (request)");
                    System.out.println("  e(X)it");
                }
                source = stdInput.readLine();
                if(source.equals("m")){
                    System.out.println("Enter your message: ");
                    source = stdInput.readLine();
                    output.println(source);
                }
                else if(source.equals("f")){
                    output.println(source); //send f to other side
                    System.out.println("Who owns the file?");
                    source = stdInput.readLine();
                    output.println(source);

                    System.out.println("Which file do you want?");
                    source = stdInput.readLine();
                    output.println(source);

                    //send port number
                    output.println(fPort);

                    // String destF = destFolder + "/" + source;
                    // System.out.println(destFolder);

                    FileReceive fR = new FileReceive(fPort, source);
                    Thread rFile = new Thread(fR);
                    rFile.start();
                }
                else if(source.equals("x")){
                    System.out.println("Closing your sockets....goodbye");
                    output.println(source);
                    break;
                }
    
                    
            }

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}

class Echo implements Runnable{

    BufferedReader input;
    String source = "temp";
    Socket client_socket;
    public Echo(Socket client_socket){
        this.client_socket = client_socket;
        try{
            //handles input from other
            input= new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        }
        catch (Exception e){
        }
        
    }

    @Override 
    public void run(){
        try{
            source = input.readLine();
            // System.out.println(input);
            while(!source.equalsIgnoreCase("NULL")){
                //source = input.readLine();
                if(source.equalsIgnoreCase("f")){
                    String fName = input.readLine();
                    int filePort = Integer.parseInt(input.readLine());

                    FileSend fS = new FileSend(filePort, fName);
                    Thread sFile = new Thread(fS);
                    sFile.start();
                }

                System.out.println(source);
                source = input.readLine();
            }
            System.exit(0);
        }
        catch(Exception e){}
        
    }
}