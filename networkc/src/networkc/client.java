package networkc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Stream;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;

public class client extends JFrame implements ActionListener, KeyListener{
	Socket clients;
	BufferedReader read;
	PrintStream write;
	String    name,IP;
	int times=0;	
	String msg;
	String tmp="",last=" ",first=" ";
	//
	JTextArea   input = new JTextArea(15,20);
	JTextField   send = new JTextField(20);
	JLabel    uname = new JLabel("user name");
	JTextField   inname = new JTextField("user",10);
	JLabel    ip = new JLabel("ip");
	JTextField   ipaddr = new JTextField("127.0.0.1",10);
	JPanel panel  = new JPanel();
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JButton button = new JButton("connect");
	JButton button1 = new JButton("send");
	
	
	JScrollPane roll = new JScrollPane(input);
	ImageIcon  i1 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\1.png");
	ImageIcon  i2 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\2.png");
	ImageIcon  i3 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\3.png");
	ImageIcon  i4 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\4.png");
	ImageIcon  i5 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\5.png");
	ImageIcon  i6 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\6.png");
	ImageIcon  i7 = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\7.png");
	ImageIcon  win = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\win.png");
	ImageIcon  lose = new ImageIcon("C:\\Users\\hydrish\\eclipse-workspace\\networkc\\src\\networkc\\8.png");
	JLabel LA = new JLabel();
	
	public class tclientrd implements Runnable{
		
		@Override
		public void run() {
			// TODO Ato-generated method stub
			int flag=0,count=0;
			try {
				while ((msg = read.readLine())!=null){
					flag=0;
					System.out.println(msg);
					input.append(msg+"\n");
					for(int i=0;i<msg.length();i++) {
						if(msg.charAt(i)==':') {
							flag=1;
						}
					}
					if(msg.equals("------------WIN------------")||times==-1) {
						LA.setIcon(win);
						times=-1;
					}
					else if(msg.equals("------------LOSE-------------")||times==-2) {
						LA.setIcon(lose);
						times=-2;
					}
					else if(flag==1) {
						if(tmp.equals(last))
							changeIm();
					}						
					else if(count>0){
						if(count==1) {
							last = tmp = first = msg;
						}
						last = tmp;
						tmp = msg;
					}
					count++;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void changeIm() {
		try {
			if(times==0) {
				LA.setIcon(i1);
				times++;
			}						
			else if(times==1) {
				LA.setIcon(i2);
				times++;
			}
			else if(times==2) {
				LA.setIcon(i3);
				times++;
			}
			else if(times==3) {
				LA.setIcon(i4);
				times++;
			}
			else if(times==4) {
				LA.setIcon(i5);
				times++;
			}
			else if(times==5) {
				LA.setIcon(i6);
				times++;
			}
			else if(times==6) {
				LA.setIcon(i7);
				times++;
			}
		}catch(Exception e){
			System.out.println("change Image failed");
		}
	}
	
	public void askcon() {
		try {
			clients = new Socket(IP,6666);
			InputStreamReader cread = new InputStreamReader(clients.getInputStream());
			read = new BufferedReader(cread);
			write =  new PrintStream(clients.getOutputStream());
			//
			System.out.println("success");
			times = read.read()-48;
			changeIm();
		}catch(IOException e){
			System.out.println("client can't connect");
		}
	}
	
	client(){
		super("client");	
		
		panel.add(ip);         
		panel.add(ipaddr);
		panel.add(uname);         
		panel.add(inname);
		panel.add(button);
		
		panel1.add(roll);
		panel1.add(send);
		panel1.add(button1);
		
		LA.setIcon(i1);
		LA.setBounds(20,20,100,50);
		panel2.add(LA);
		
		getContentPane().add(BorderLayout.NORTH,panel);	
		getContentPane().add(BorderLayout.CENTER,panel1); 
		getContentPane().add(BorderLayout.EAST,panel2); 

		button.addActionListener(this);
		button1.addActionListener(this);
		inname.addKeyListener(this);
		send.addKeyListener(this);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setEditable(false);
		
		roll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		roll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		
		setSize(450,450);
		setVisible(true);
		
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.out.println("exit");System.exit(0);}});
		
	}	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		client Client = new client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String get=e.getActionCommand();
		if(get.equals("connect")) {
			name = inname.getText();
			IP = ipaddr.getText();
			askcon();
			Thread tread = new Thread(new tclientrd());
			tread.start();
			inname.setVisible(false);
			button.setVisible(false);
			ip.setVisible(false);
			ipaddr.setVisible(false);
			uname.setText(name);
		}
		else if(get.equals("send")) {
			if(send.getText()!="") {
				try {
					write.println((name+":"+send.getText()));
					write.flush();
				}catch(Exception e1) {
					System.out.println("send msg failed");
				}
				send.setText("");
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if(send.getText()!="") {
					try {
						write.println((name+":"+send.getText()));
						write.flush();
					}catch(Exception e1) {
						System.out.println("send msg failed");
					}
					send.setText("");
				}
			
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
