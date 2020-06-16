package networks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

public class server {
	Vector vec;
	static String answer;
	int times=0,correct=0,maxc=0;
	String msg,tmp="",cletter="",user="",last="";
	//thread負責處理此class
	public class tclient implements Runnable{
		BufferedReader read;
		Socket tsocket;
		//起始值
		public tclient(Socket s) {
			try {
				tsocket = s;
				InputStreamReader tread = new InputStreamReader(tsocket.getInputStream());
				read = new BufferedReader(tread);
			}catch(Exception e) {
				System.out.println("failed");
			}
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int flag=0;
			cletter="";
			try {
				PrintStream write = new PrintStream(tsocket.getOutputStream());
				write.print(answer.length());
				write.flush();
				write.print(" words\n");
				write.flush();
				for(int i=0;i<answer.length();i++) {
						cletter += "_";
						cletter += " ";
				}
				last = cletter;
				write.print(cletter+"\n");
				write.flush();
				
				while ((msg = read.readLine())!=null){ 
					tmp="";cletter="";user="";
					flag=0;
					times++;
					System.out.println("received : "+msg);
					for(int i=0;i<msg.length();i++) {
						if(msg.charAt(i)==':'&&i==msg.length()-1) {
							tmp =" ";
						}
						else if(msg.charAt(i)==':') {
							flag=1;
							i++;
						}
						else if(flag==0) {
							user+=msg.charAt(i);
						}
						if(flag==1) {
							tmp+=msg.charAt(i);
						}
					}
					for(int i=tmp.length();i<answer.length();i++) {
						tmp+=" ";
					}
					correct=0;
					for(int i=0;i<answer.length();i++) {
						if(answer.charAt(i)==tmp.charAt(i)) {
							cletter += answer.charAt(i);
							correct++;
							
						}
						else {
							cletter += "_";
							cletter += " ";
						}
					}
					
					
					
					System.out.print(cletter);
					if(correct>maxc) {
						broadcast(cletter);
						last = cletter;
						times--;
						maxc = correct;
					}
					else {
						broadcast(last);
					}
					
					
					broadcast(msg);
					if(tmp.equals(answer)&&times<7) {
						broadcast("------------WIN------------\n");
						broadcast("congradulation, "+user+" !");
					}
					else if(times==7) {
						broadcast("------------LOSE-------------\n");
					}
					//else {
						
						
					//}
				}
			}catch(Exception e) {
				System.out.println("a client left");
			}
		}
		
		public void broadcast(String s) {
			 Iterator iterate = vec.iterator();
			 while(iterate.hasNext()) {
				 try {
					 PrintStream write = (PrintStream) iterate.next();
					 write.println(s);
					 write.flush();
				 }catch(Exception e){
					 System.out.println("broadcast failed");
				 }
			 }
		}
	}
	
	public void connect() {
		vec = new Vector();
		try(ServerSocket server = new ServerSocket(6666)){
			while(true) {
				Socket socket = server.accept();
				PrintStream write = new PrintStream(socket.getOutputStream());
				vec.add(write);
				//call thread
				Thread t = new Thread(new tclient(socket));
				t.start();
				System.out.println("alive : "+(t.activeCount()-1));
				write.print(times);
				write.flush();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}finally {
			System.out.println("Server shutdown");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.print("Set the answer : \n");
		answer = scanner.next();		
		new server().connect();//call class : server的connect function
	}

}
