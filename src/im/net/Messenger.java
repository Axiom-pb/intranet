package im.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import im.ui.Gui;

public class Messenger implements Runnable {
	
	private String ip;
	private Gui gui;
	
	private Socket socket = null;
	private InputStream is = null;
	private OutputStream os = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	private Thread t;
	
	public Messenger(String ip, Gui gui) {
		this.ip = ip;
		this.gui = gui;
	}
	
	public Messenger(String ip, Gui gui, Socket socket, DataInputStream in, DataOutputStream out) {
		this(ip, gui);
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	
	public void execute() {
		this.t = new Thread(this);
		this.t.start();
	}

	@Override
	public void run() {
		try {
			if(this.socket == null)
				socket = new Socket(ip, 3333);
			
			if(this.in == null || this.out == null) {
				is = socket.getInputStream();
				os = socket.getOutputStream();
				
				in = new DataInputStream(is);
				out = new DataOutputStream(os);
			}
			
			gui.addMessage("Connected to: " + ip);
			gui.addMessage("on (local) port " + "("+socket.getLocalPort()+")"+" "+socket.getPort()+"\n");
			
			String msg;
			while((msg = in.readUTF()) != null) {
				receiveMessage(msg);
				if(this.t.isInterrupted()) return;
			}			
		} catch (UnknownHostException uhx) {
			gui.addMessage("Host not found: " + ip);
			uhx.printStackTrace();
		} catch (IOException iox) {
			gui.addMessage("Couldn't get/create I/O for " + ip);
			iox.printStackTrace();
		}
	}
	
	public synchronized void sendMessage() {
		String msg = gui.getMessageArea().getText();
		gui.addMessage(">You:\n" + msg);
		
		try {
			out.writeUTF(msg);
			gui.getMessageArea().setText("");
			out.flush();
		} catch(IOException iox) {
			gui.addMessage(">>ERROR SENDING MESSAGE");
			iox.printStackTrace();
		}
	}
	
	public synchronized void receiveMessage(String msg) {
		gui.addMessage(">"+ ip + ":\n" + msg);
		
		if(gui.getTabbedPane().getSelectedIndex() != 1) {
			JOptionPane.showMessageDialog(gui, "Message from " + ip + ":\n" + msg);
			gui.enableMessenger(true);
			gui.getTabbedPane().setSelectedIndex(1);
		}
	}
	
	public void close() {
		try {
			this.socket.close();
			this.in.close();
			this.is.close();
			this.out.close();
			this.os.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		} finally {
			this.t.interrupt();
		}
	}

}
