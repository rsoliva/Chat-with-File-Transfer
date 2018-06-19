import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class FileReceive implements Runnable{

	int fPort;
	String fileName;
	ServerSocket ss;
	public FileReceive(int fPort, String fileName){
		this.fPort = fPort;
		this.fileName = fileName;
	}

	@Override
	public void run(){

		try{
			this.ss = new ServerSocket(fPort);
			Socket file_socket = ss.accept();
			System.out.println("file socket accepted");
			this.ss.close();

			System.out.println("File receive " + fPort + " " + fileName);

			DataInputStream dis = new DataInputStream(file_socket.getInputStream());

			File pFile = new File(fileName);
			FileOutputStream fos = new FileOutputStream(pFile);

			byte[] buffer = new byte[4096];
			int filesize = dis.readInt(); // Send file size in separate msg
			System.out.println(filesize);
			int read = 0;
			int totalRead = 0;
			int remaining = filesize;
			while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {

				totalRead += read;
				remaining -= read;
				
				fos.write(buffer, 0, read);
			}
			System.out.println("read " + totalRead + " bytes.");
			fos.close();
			dis.close();


		
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
}