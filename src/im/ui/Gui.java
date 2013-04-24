package im.ui;

import im.net.IncomingConnectionHandler;
import im.net.Messenger;
import im.net.NetScanner;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;

public class Gui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ipFrom;
	private JTextField ipTo;
	private JList<String> list;
	private JButton btnScan;
	private JLabel status;
	private JButton btnStop;
	private JButton btnConnect;
	private JTabbedPane tabbedPane;
	private JTextArea chatArea;
	private JTextArea messArea;
	private JPanel panel_1;
	private JButton btnSend;
	private JScrollPane chatScrollPane;
	private JScrollPane messScrollPane;
	
	private DefaultListModel<String> listModel;

	private final String textIpFrom = "from ip...";
	private final String textIpTo = "to ip...";
	
	private NetScanner scanner;
	private Messenger m;
	private IncomingConnectionHandler ich;;

	public Gui() {
		setTitle("Intranet Messenger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Scanner", null, panel, null);
		panel.setLayout(new MigLayout("", "[168.00,grow,left][grow][80.00,grow,left][]", "[24.00,grow][-21.00][][][][][][]"));
		
		listModel = new DefaultListModel<String>();
		
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, "cell 0 0 4 6,grow");
		
		btnScan = new JButton("Scan");
		btnScan.addActionListener(this);
		panel.add(btnScan, "flowx,cell 0 7,alignx center");
		
		status = new JLabel("Status: Idle");
		panel.add(status, "cell 0 6,alignx center");
		
		ipFrom = new JTextField(textIpFrom);
		ipFrom.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(ipFrom.getText().equals(textIpFrom)) {
					ipFrom.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(ipFrom.getText().equals("")) {
					ipFrom.setText(textIpFrom);
				}
			}
		});
		panel.add(ipFrom, "cell 1 7");
		ipFrom.setColumns(10);
		
		ipTo = new JTextField(textIpTo);
		ipTo.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if(ipTo.getText().equals(textIpTo)) {
					ipTo.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(ipTo.getText().equals("")) {
					ipTo.setText(textIpTo);
				}
			}
		});
		panel.add(ipTo, "cell 2 7,growx");
		ipTo.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		btnConnect.setEnabled(false);
		panel.add(btnConnect, "cell 3 7");
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(this);
		panel.add(btnStop, "cell 0 7");
		
		panel_1 = new JPanel();
		tabbedPane.addTab("Messenger", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[grow][grow][][][][][][][][][][][][]", "[grow][][][][][47.00][grow][]"));
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		
		DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScrollPane = new JScrollPane(chatArea);
		chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel_1.add(chatScrollPane, "cell 0 0 14 6,grow");
		
		messArea = new JTextArea();
		
		DefaultCaret messCaret = (DefaultCaret) messArea.getCaret();
		messCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		messScrollPane = new JScrollPane(messArea);
		panel_1.add(messScrollPane, "cell 0 6 13 2,grow");
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(this);
		panel_1.add(btnSend, "cell 13 6 1 2,growy");
		
		enableMessenger(false);
		
		ich = new IncomingConnectionHandler(this.m, this);
		ich.execute();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btnScan) {
			if(ipFrom.getText().equals(textIpFrom) && ipTo.getText().equals(textIpTo) 
					|| ipFrom.getText().equals(textIpFrom))
				return;
			
			String ip1 = ipFrom.getText();
			String ip2;
			
			int lastDot = ip1.lastIndexOf(".");
			
			if(ipTo.getText().equals(textIpTo))
				ip2 = ip1.substring(lastDot+1);
			else if(Integer.parseInt(ip1.substring(lastDot+1))>Integer.parseInt(ipTo.getText()))
				ip2 = ip1.substring(lastDot+1);
			else
				ip2 = ipTo.getText();
			
			setStatus("Scanning");
			enableConnect(true);
			enableScan(false);
			enableStop(true);
			listModel.clear();
			scanner = new NetScanner(ip1, Integer.parseInt(ip2), this);
			scanner.execute();
		} else if(source == btnStop) {
			scanner.stop();
			enableScan(true);
		} else if(source == btnConnect) {
			enableMessenger(true);
			tabbedPane.setSelectedIndex(1);
			String ip = list.isSelectionEmpty()?ipFrom.getText():list.getSelectedValue();
			if(m != null) {
				m.close();
				this.getChatArea().setText("");
			}
			
			addMessage("Connecting to " + ip + "...");
			
			if(listModel.isEmpty() || list.isSelectionEmpty()) {
				m = new Messenger(ipFrom.getText(), this);
			} else {
				m = new Messenger(list.getSelectedValue(), this);
			}
			
			m.execute();
		} else if(source == btnSend) {
			if(!getMessageArea().getText().isEmpty())
				m.sendMessage();
		}
	}
	
	public void enableScan(boolean b) {
		this.btnScan.setEnabled(b);
	}
	
	public void enableStop(boolean b) {
		this.btnStop.setEnabled(b);
	}
	
	public void enableConnect(boolean b) {
		this.btnConnect.setEnabled(b);
	}
	
	public void enableMessenger(boolean b) {
		this.tabbedPane.setEnabledAt(1, b);
	}
	
	public void setStatus(String s) {
		status.setText("Status: " + s);
	}
	
	public DefaultListModel<String> getListModel() {
		return this.listModel;
	}
	
	public JTabbedPane getTabbedPane() {
		return this.tabbedPane;
	}
	
	private JTextArea getChatArea() {
		return this.chatArea;
	}
	
	public JTextArea getMessageArea() {
		return this.messArea;
	}
	
	public void setMessenger(Messenger m) {
		this.m = m;
	}
	
	public synchronized void addMessage(String msg) {
		this.getChatArea().append(msg + "\n");
	}

}
