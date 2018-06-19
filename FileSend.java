import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class FileSend implements Runnable{

	int fPort;
	String fileName;
	Socket file_socket;
	public FileSend(int fPort, String fileName){
		this.fPort = fPort;
		this.fileName = fileName;

	}

	@Override
	public void run(){
		try {

			System.out.println("File send " + fPort + " " + fileName);

			this.file_socket = new Socket("localhost", fPort);
	        DataOutputStream dos = new DataOutputStream(file_socket.getOutputStream());

	        File pFile = new File(fileName);
			FileInputStream fis = new FileInputStream(pFile);

			System.out.println(pFile.length());
			dos.writeInt((int)pFile.length());

			byte[] buffer = new byte[4096];
			int total = 0;
			int bytesRead = 0;
			while ((bytesRead = fis.read(buffer)) > 0) {
				total += bytesRead;
				dos.write(buffer);
			}
			System.out.println("total bytes read: " + total);
			fis.close();
			dos.close();
 
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	    
	}
}