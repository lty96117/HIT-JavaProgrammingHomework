/*
 *
 * */
import java.lang.Object;

import java.awt.Container;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.GroupLayout;
import javax.swing.BorderFactory;

import javax.swing.plaf.basic.BasicComboBoxUI;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;

public class chat
{
	public static void main(String [] args){
		chat thisisaclasschat = new chat();
	}	
	
	public chat(){
		JFrame mainframe = new JFrame();
		JTextArea test = new JTextArea();

		mainframe.add(test);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(100,100);
		mainframe.setVisible(true);

		chatwindow chatxxx = new chatwindow("test");
	}

}

class chatwindow
{
	private static final Color buttoncolorbg = new Color(191, 191, 191);
	private static final Color buttoncolorfg = new Color(15, 15, 15);
	
	private static final int fontsize = 25;
	private static final String buttonfont = "Microsoft YaHei";

	private static final String[] fontlist = {"Droid Sans", "Ubuntu"};
	private static final String[] colorlist = {"black", "red", "blue"};
	
	private JFrame chatwin;
	private myComboBox<String> fontselect = new myComboBox<>(fontlist);
	private myComboBox<String> colorselect = new myComboBox<>(colorlist);
	private JTextArea sendtext = new JTextArea();
	private JButton sendbutton = new JButton("\u53D1\u9001");
	private JTextPane chatzone = new JTextPane();

	public chatwindow(String peername){
		String title = "\u4E0E " + peername + " \u804A\u5929";
		chatwin = new JFrame(title);
		chatwin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		
		fontselect.setPreferredSize(new Dimension(80, 20));
		colorselect.setPreferredSize(new Dimension(80, 20));
	
		sendtext.setPreferredSize(new Dimension(300, 50));

		sendbutton.setPreferredSize(new Dimension(50, 50));
		sendbutton.setMargin(new Insets(0,-20,0,-20));
		sendbutton.setFont(new Font(buttonfont, Font.BOLD, fontsize));
		sendbutton.setForeground(buttoncolorfg);
		sendbutton.setBackground(buttoncolorbg);
		sendbutton.setActionCommand("send");
		sendbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				;//
			}
		});

		chatzone.setContentType("text/html");
		chatzone.setEditable(false);
		chatzone.setPreferredSize(new Dimension(350, 300));

		GroupLayout gp = new GroupLayout(panel);

		gp.setHorizontalGroup(gp
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(chatzone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addGap(0)
			.addGroup(gp
				.createSequentialGroup()
				.addComponent(fontselect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(0)
				.addComponent(colorselect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(0)
			.addGroup(gp
				.createSequentialGroup()
				.addComponent(sendtext, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(sendbutton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);

		gp.setVerticalGroup(gp
			.createSequentialGroup()
			.addComponent(chatzone)
			.addGap(0)
			.addGroup(gp
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(fontselect)
				.addGap(0)
				.addComponent(colorselect))
			.addGap(0)
			.addGroup(gp
				.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(sendtext)
				.addGap(0)
				.addComponent(sendbutton))
		);

		chatwin.add(panel);
		chatwin.setSize(panel.getPreferredSize());
		chatwin.setVisible(true);
	}

	private void addChatRow(String text, String fontfamily, int fontsize){
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setFontFamily(set, fontfamily);
		StyleConstants.setFontSize(set, fontsize);

		StyledDocument doc = chatzone.getStyledDocument();
		try{
			doc.insertString(doc.getLength(), text, set);
		} catch(BadLocationException e){
			;
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

