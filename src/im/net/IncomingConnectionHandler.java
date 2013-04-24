package im.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import im.ui.Gui;

public class IncomingConnectionHandler implements Runnable {
	
	private Gui gui;
	
	private ServerSocket serverSocket;
	private Socket socket;
	
	private InputStream is;
	private DataInputStream in;
	private OutputStream os;
	private DataOutputStream out;
	
	private Thread t;
	
	private Messenger m = null;
	
	public IncomingConnectionHandler(Messenger m, Gui gui) {
		this.m = m;
		this.gui = gui;
	}
	
	public void execute() {
		this.t = new Thread(this);
		this.t.start();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("0.0.0.0", 3333));
			
			socket = serverSocket.accept();
			
			is = socket.getInputStream();
			in = new DataInputStream(is);
			os = socket.getOutputStream();
			out = new DataOutputStream(os);
			
			m = new Messenger(socket.getInetAddress().toString(), this.gui, this.socket, this.in, this.out);
			
			gui.setMessenger(m);
			
			m.execute();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}
	
	public void close() {
		try {
			is.close();
			in.close();
			os.close();
			out.close();
		} catch(SocketException sx) {
			
		} catch(IOException iox) {
			
		} finally {
			t.interrupt();
		}
	}
	
}
