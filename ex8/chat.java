/*
 *
 * */
import java.lang.Object;

import java.awt.Container;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Arrays;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import javax.swing.plaf.basic.BasicComboBoxUI;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

//import sun.net.util.IPAddressUtil;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.IOException;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Log
{
	public static void o(String s){
		System.out.println(s);
	}

	public static void o(Object o){
		System.out.println(o);
	}
}

public class chat
{
	public static final Color buttoncolorbg = new Color(191, 191, 191);
	public static final Color buttoncolorfg = new Color(15, 15, 15);

	public static final int fontsize = 25;
	public static final String buttonfont = "Microsoft YaHei";

	public static final String defaultname = "Anonymous";

	//private static enum IPTYPE { IPv4, IPv6 };

	public static void main(String [] args){
		int port = 13009;
		if(args.length > 0)
			port = Integer.parseInt(args[0]);
		chat thisisaclasschat = new chat(port);
	}	

	static JFrame mframe = new JFrame();
	static JTextField namefield = new JTextField(defaultname);
	static JTextField addrfield = new JTextField();
	static JLabel colonlabel = new JLabel(":");
	static JTextField portfield = new JTextField();
	static JButton startbutton = new JButton("\u804A");

	public chat(){
		System.out.println("Usage: java chat <yourPort>");
	}
	
	private static class portListener extends Thread {
		private DatagramSocket socket = null;
		public portListener(int port){
			try{
				InetSocketAddress in4 = new InetSocketAddress("0.0.0.0", port);
				InetSocketAddress in6 = new InetSocketAddress("::", port);
				socket = new DatagramSocket(port);
				try{		
					socket.bind(in4);
				} catch(SocketException e){
					Log.o(in4);
				}
				try{
					socket.bind(in6);
				} catch(SocketException e){
					Log.o(in6);
				}
				socket.setSoTimeout(1000);
			} catch(SocketException e){
				e.printStackTrace();
			}
			if(socket == null)
				System.exit(1);
		}
		
		public void run(){
			ShakeMessage rdata;
			ShakeMessage wdata = new ShakeMessage();
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			try{ while(true){
				try{
					socket.receive(packet);
				} catch(SocketTimeoutException e){
					continue;
				}
				try{
					ByteArrayInputStream baos = new ByteArrayInputStream(buf);
					ObjectInputStream oos = new ObjectInputStream(baos);
					rdata = (ShakeMessage)oos.readObject();
				} catch(IOException e){
					e.printStackTrace();
					continue;
				} catch(ClassNotFoundException e){
					e.printStackTrace();
					continue;
				}	
				
				InetAddress client = packet.getAddress();
				// accept?
				int n = JOptionPane.showConfirmDialog(null,
						"Chat from " + rdata.name + "(" + client.toString() + ")",
						"Accept?",
						JOptionPane.YES_NO_OPTION);
				if(n == JOptionPane.NO_OPTION){
					continue;
				}
				
				// start chat
				if(rdata.port == 0)
					rdata.port = ((InetSocketAddress)(packet.getSocketAddress())).getPort();
				if(rdata.name == null || rdata.name.equals(""))
					rdata.name = defaultname;
				
				DatagramSocket s = new DatagramSocket();
				wdata.port = ((InetSocketAddress)(s.getLocalSocketAddress())).getPort();
				if(namefield.getText().equals(""))
					wdata.name = defaultname;
				else
					wdata.name = namefield.getText();
				if(s.getLocalSocketAddress() == packet.getSocketAddress() 
					&& wdata.port == rdata.port){
					Log.o("Loop!");
				}
				Log.o("Local port : " + wdata.port);
				Log.o("Remote port : " + rdata.port);
				
				try{
					s.connect(client, rdata.port);
					ObjectOverUdp.sendObject(s, wdata, client, rdata.port);
					chatwindow cw = new chatwindow(s, client, rdata.port, rdata.name);
				} catch(SocketException e){
					e.printStackTrace();
					break;
				} 
			} }catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public chat(int port){
		startbutton.setPreferredSize(new Dimension(50, 30));
		startbutton.setMargin(new Insets(0,-20,0,-20));
		startbutton.setFont(new Font(buttonfont, Font.BOLD, fontsize));
		startbutton.setForeground(buttoncolorfg);
		startbutton.setBackground(buttoncolorbg);
		startbutton.setActionCommand("start");
		startbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				startChat();//
			}
		});
		addrfield.setPreferredSize(new Dimension(200, 20));
		portfield.setPreferredSize(new Dimension(60, 20));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel mpanel = new JPanel();
		mpanel.setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 5);
		c.gridwidth = 4;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(namefield, c);
		mpanel.add(namefield);
		c.insets = new Insets(5, 5, 5, 2);
		c.gridwidth = 1;
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(addrfield, c);
		mpanel.add(addrfield);
		c.insets = new Insets(5, 0, 5, 2);
		c.gridx = 1; c.gridy = 1; gridbag.setConstraints(colonlabel, c);
		mpanel.add(colonlabel);
		c.insets = new Insets(5, 0, 5, 2);
		c.gridx = 2; c.gridy = 1; gridbag.setConstraints(portfield, c);
		mpanel.add(portfield);
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 3; c.gridy = 1; gridbag.setConstraints(startbutton, c);
		mpanel.add(startbutton);
		
		mframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mframe.add(mpanel);
		mframe.setSize(mpanel.getPreferredSize());
		mframe.setTitle(String.valueOf(port));
		mframe.setVisible(true);

		new portListener(port).start();
	}

	private void startChat(){
		if(portfield.getText().equals(""))
			return ;
		if(addrfield.getText().equals(""))
			return ;
		int port = Integer.parseInt(portfield.getText());
		String ip = addrfield.getText();
		// prepare
		InetSocketAddress serv;
		DatagramSocket socket;
		ShakeMessage data = new ShakeMessage();
		serv = new InetSocketAddress(ip, port);
		try{
			socket = new DatagramSocket();
			socket.setSoTimeout(10000);
		} catch(SocketException e){
			e.printStackTrace();
			return ;
		}

		data.port = ((InetSocketAddress)(socket.getLocalSocketAddress())).getPort();
		Log.o("Local port : " + data.port);
		data.name = namefield.getText().equals("") ? defaultname : namefield.getText();
		try{
			// socket.connect(serv);
			ObjectOverUdp.sendObject(socket, data, serv);
			// socket.disconnect();
			data = (ShakeMessage)ObjectOverUdp.recvObject(socket);
			//Log.o(data);
		} catch(IOException e){
			e.printStackTrace();
		}
		if(data == null){
			socket.close();
			return ;
		} else if(data.flag != 1){
			socket.close();
			return ;
		}

		socket.connect(serv.getAddress(), data.port);
		Log.o("remote peer name : " + data.name);

		if(data.name == null || data.name.equals(""))
			data.name = defaultname;
	
		chatwindow cw = new chatwindow(socket, serv.getAddress(), data.port, data.name);
		// socket.close();
	}
}

class ObjectOverUdp
{
	public static void sendObject(DatagramSocket sock, Object obj, InetSocketAddress serv) throws SocketException, IOException {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			byte[] buf = baos.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serv);
			//Log.o(packet.getSocketAddress());
			sock.send(packet);
		} catch(SocketException e){
			// e.printStackTrace();
			throw e;
		} catch(IOException e){
			throw e;
		}
	}

	public static void sendObject(DatagramSocket sock, Object obj, InetAddress serv, int port) throws IOException, SocketException {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			byte[] buf = baos.toByteArray();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serv, port);
			sock.send(packet);
		} catch(SocketException e){
			// e.printStackTrace();
			throw e;
		} catch(IOException e){
			throw e;
		}
	}

	public static Object recvObject(DatagramSocket sock) throws IOException, SocketTimeoutException, SocketException {
		Object obj = null;
		try{
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			sock.receive(packet);
			ByteArrayInputStream baos = new ByteArrayInputStream(buf);
			ObjectInputStream oos = new ObjectInputStream(baos);
			obj = oos.readObject();
		} catch(SocketTimeoutException e){
			throw e;
		} catch(SocketException e){
			//e.printStackTrace();
			sock.close();
			throw e;
		} catch(IOException e){
			throw e;
		} finally {
			return obj;
		}
	}
}

class ShakeMessage implements Serializable
{
	public int flag = 1;
	public int port = 0;
	public String name = "";
}

class fontStructure implements Serializable
{
	public String ff;
	public String fc;
	public String fs;
}

class messageStructure extends fontStructure
{
	public String message;
}

class chatwindow implements Runnable
{
	private static final String[] fontlist = {"Microsoft YaHei", "Ubuntu"};
	private static final String[] colorlist = {"black", "red", "blue", "green", "gray"};
	private static final Color[] colorlist_map = {Color.black, Color.red, Color.blue, Color.green, Color.gray};
	private static final String[] sizelist = {"16", "20", "24", "28", "32"};
	
	private static fontStructure defaultfont = new fontStructure();
	private static fontStructure defaultfontforme = new fontStructure();
	private static fontStructure defaultfontfailed = new fontStructure();

	private static String he;
	private static String me;

	private static final String sendfailure = "\u53D1\u9001\u5931\u8D25";

	private JFrame chatwin;
	private myComboBox<String> fontselect = new myComboBox<>(fontlist);
	private myComboBox<String> colorselect = new myComboBox<>(colorlist);
	private myComboBox<String> sizeselect = new myComboBox<>(sizelist);
	private JTextArea sendtext = new JTextArea();
	private JButton sendbutton = new JButton("\u53D1\u9001");
	private JTextPane chatzone = new JTextPane();

	private InetSocketAddress address;
	private DatagramSocket socket;

	fontStructure font = new fontStructure();

	private void setTitle(String name, String host){
		chatwin.setTitle("\u4E0E " + name + "(" + host + ")" + " \u804A\u5929");
	}

	public void run(){
		messageStructure m = null;
		try{
			socket.setSoTimeout(1000);
		} catch(SocketException e){
			e.printStackTrace();
		}
		try{ while(true){
			try{
				m = (messageStructure)ObjectOverUdp.recvObject(socket);
			} catch(SocketTimeoutException e){
				continue;
			}
			
			if(m == null)
				continue;
				
			DateFormat dateFormat = new SimpleDateFormat("  yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			addChatRow(he + dateFormat.format(date), defaultfont);
			addChatRow(m);
		} } catch(SocketException e){
			e.printStackTrace();
			socket.close();
		} catch(IOException e){
			// something wrong?
			e.printStackTrace();
		} 
	}
	
	public chatwindow(DatagramSocket from, InetAddress addr, int port, String he){
		// ...
		socket = from;
		address = new InetSocketAddress(addr, port);
		//Log.o(from);
		//Log.o(addr);
		//Log.o(String.valueOf(port));
		//Log.o(he);

		// default
		defaultfont.ff = fontlist[0];
		defaultfont.fc = colorlist[0];
		defaultfont.fs = sizelist[0];
		defaultfontforme.ff = fontlist[0];
		defaultfontforme.fc = colorlist[3];
		defaultfontforme.fs = sizelist[0];
		defaultfontfailed.ff = fontlist[0];
		defaultfontfailed.fc = colorlist[4];
		defaultfontfailed.fs = sizelist[0];
		this.he = he;
		this.me = "\u6211";

		// Initialization
		chatwin = new JFrame();
		//chatwin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chatwin.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				socket.close();
			}
		});
		setTitle(he, addr.toString());

		fontselect.setPreferredSize(new Dimension(200, 20));
		colorselect.setPreferredSize(new Dimension(90, 20));
		sizeselect.setPreferredSize(new Dimension(60, 20));
		JPanel optpanel = new JPanel();
		optpanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		optpanel.add(fontselect);
		optpanel.add(colorselect);
		optpanel.add(sizeselect);
		optpanel.setPreferredSize(new Dimension(optpanel.getPreferredSize().width, 20));

		fontselect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setTextArea();
			}
		});
		colorselect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setTextArea();
			}
		});
		sizeselect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setTextArea();
			}
		});
	
		setTextArea();
		sendtext.setPreferredSize(new Dimension(300, 50));

		sendbutton.setPreferredSize(new Dimension(50, 50));
		sendbutton.setMargin(new Insets(0,-20,0,-20));
		sendbutton.setFont(new Font(chat.buttonfont, Font.BOLD, chat.fontsize));
		sendbutton.setForeground(chat.buttoncolorfg);
		sendbutton.setBackground(chat.buttoncolorbg);
		sendbutton.setActionCommand("send");
		sendbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				// callback function is better
				// the following is awful...
				sendMessage(sendtext.getText());
				sendtext.setText("");
			}
		});

		chatzone.setContentType("text/html");
		chatzone.setEditable(false);
		chatzone.setPreferredSize(new Dimension(350, 300));

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel mpanel = new JPanel();
		mpanel.setLayout(gridbag);

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 0, 5);
		c.gridwidth = 2;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(chatzone, c);
		mpanel.add(chatzone);
		c.gridwidth = 2;
		c.insets = new Insets(3, 5, 3, 5);
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(optpanel, c);
		mpanel.add(optpanel);
		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 5, 0);
		c.gridx = 0; c.gridy = 2; gridbag.setConstraints(sendtext, c);
		mpanel.add(sendtext);
		c.insets = new Insets(0, 0, 5, 5);
		c.gridx = 1; c.gridy = 2; gridbag.setConstraints(sendbutton, c);
		mpanel.add(sendbutton);

		chatwin.add(mpanel);
		chatwin.setSize(mpanel.getPreferredSize());
		chatwin.setVisible(true);

		// one more thread ?
		new Thread(this).start();
	}
	
	private void addChatRow(String text, fontStructure f){
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setFontFamily(set, f.ff);
		StyleConstants.setFontSize(set, Integer.parseInt(f.fs));
		StyleConstants.setForeground(set, colorlist_map[Arrays.asList(colorlist).indexOf(f.fc)]);

		StyledDocument doc = chatzone.getStyledDocument();
		try{
			doc.insertString(doc.getLength(), text, set);
			doc.insertString(doc.getLength(), "\n", set);
			System.out.println(text);
		} catch(BadLocationException e){
			e.printStackTrace();
		}
	}

	private void addChatRow(messageStructure m){
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setFontFamily(set, m.ff);
		StyleConstants.setFontSize(set, Integer.parseInt(m.fs));
		StyleConstants.setForeground(set, colorlist_map[Arrays.asList(colorlist).indexOf(m.fc)]);

		StyledDocument doc = chatzone.getStyledDocument();
		try{
			doc.insertString(doc.getLength(), m.message, set);
			doc.insertString(doc.getLength(), "\n", set);
			System.out.println(m.message);
		} catch(BadLocationException e){
			e.printStackTrace();
		}
	}

	private void setTextArea(){
		font.ff = String.valueOf(fontselect.getSelectedItem());
		font.fc = String.valueOf(colorselect.getSelectedItem());
		font.fs = String.valueOf(sizeselect.getSelectedItem());

		int idx = Arrays.asList(colorlist).indexOf(font.fc);
		idx = idx >= 0 ? idx : 0;

		sendtext.setFont(new Font(font.ff, Font.PLAIN, Integer.parseInt(font.fs)));
		sendtext.setForeground(colorlist_map[idx]);
	}

	private void sendMessage(String msg){
		DateFormat dateFormat = new SimpleDateFormat("  yyyy-MM-dd HH:mm:ss");		
		Date date = new Date();
		addChatRow(me + dateFormat.format(date), defaultfontforme);

		messageStructure m = new messageStructure();
		m.ff = font.ff;
		m.fc = font.fc;
		m.fs = font.fs;
		m.message = msg;
		addChatRow(m);

		try {
			ObjectOverUdp.sendObject(socket, m, address);
		} catch(IOException e){
			e.printStackTrace();
			addChatRow(sendfailure, defaultfontfailed);
		} 
	}
}

class myComboBox<E> extends JComboBox<E>
{
	public myComboBox(E[] list){
		super(list);
	}

	@Override public void updateUI(){
		super.updateUI();
		setUI(new BasicComboBoxUI() {
			@Override protected JButton createArrowButton(){
				JButton b = new JButton();
				b.setBorder(BorderFactory.createEmptyBorder());
				b.setVisible(false);
				return b;
			}
		});
		setBorder(BorderFactory.createLineBorder(Color.gray));
	}
}

