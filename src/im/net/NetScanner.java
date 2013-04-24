package im.net;

import im.lock.Lock;
import im.ui.Gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetScanner implements Runnable {

	private String ip;
	private int range;
	private Gui gui;
	
	private Lock lock;

	private List<String> list;

	private Thread t;

	private boolean stop = false;

	public NetScanner(String ip, int range, Gui gui) {
		if(isIpValid(ip)) {
			this.ip = ip;
			this.range = range;
			this.gui = gui;
			this.list = null;
		} else {
			this.ip = "0.0.0.0";
			this.range = 255;
			this.gui = gui;
		}
	}

	public void execute() {
		this.t = new Thread(this);
		this.t.start();
	}

	private boolean isIpValid(String ip) {
		Pattern pattern = Pattern
				.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
						+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		Matcher matcher = pattern.matcher(ip);

		return matcher.find() && matcher.group().equals(ip);
	}

	public String getIp() {
		return this.ip;
	}

	public int getRange() {
		return this.range;
	}

	public void scan() {
		final List<String> list = new ArrayList<String>();

		String ipPart = this.ip.substring(0, this.ip.lastIndexOf(".") + 1);
		String sBase = this.ip.substring(this.ip.lastIndexOf(".") + 1);
		int base = Integer.parseInt(sBase);
		
		lock = new Lock();

		for(int i = base ; i <= this.range ; i++) {
			if(stop)
				return;

			final String IP = ipPart + i;

			new Thread() {
				public void run() {
					try {
						lock.addRunningThread();
						
						if(InetAddress.getByName(IP).isReachable(4000)) {
							list.add(IP);
							gui.getListModel().addElement(IP);
						}
						
						lock.removeRunningThread();
						
						synchronized(lock) {
							lock.notify();
						}
					} catch(UnknownHostException e) {
						e.printStackTrace();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

		}

		this.list = list;
	}

	public ArrayList<String> getAliveHosts() {
		if(this.list == null)
			this.scan();

		return (ArrayList<String>) this.list;
	}

	@Override
	public void run() {
		this.scan();
		
		while(lock.getRunningThreads() > 0) {
			synchronized(lock) {
				try {
					lock.wait();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.gui.enableScan(true);
		this.gui.enableStop(false);
		this.gui.setStatus("Done scanning");
	}

	public void stop() {
		this.stop = true;
		this.gui.enableScan(true);
		this.gui.enableStop(false);
		this.gui.setStatus("Scanning stopped");
		this.t.interrupt();
	}

}
