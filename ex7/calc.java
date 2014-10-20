/*
 * Incomplete...
 * 2014-10-19
 *
 * */
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.lang.Math;
import java.util.Stack;
import java.util.Arrays;

public class calc extends JFrame implements ActionListener
{
	// constants
	public static final int FLAG_TYPE = 0x7;
	 public static final int TYPE_SIMPLE = 0x0;
	 public static final int TYPE_SCIENTIFIC = 0x1;
	public static final int FLAG_DISABLED_POINT = 0x8;
	//public static final int FLAG_DISABLED_ = 0x10;
	//public static final int FLAG_

	// ----
	public static void main(String [] args){
		calc thisisacalculator = new calc();
	}
	
	// ----
	private void dbg(String msg){
		//JOptionPane.showMessageDialog(null, msg);
	}

	private void err(String msg){
		clearResult();
		outputtop.setText(msg);
		JOptionPane.showMessageDialog(null, msg);
	}

	// stacks and temporary variables
	private Stack<Object> mainstack = new Stack<Object>();
	private Stack<String> opstack = new Stack<String>();
	private int tint = 0;
	private double tdouble = 0.0;
	private String tnum = "";
	private String top = "";
	private double tres = 0.0;
	private double lastres = 0.0;
	private String lastinput = "";
	
	// ----
	private static final String[] bts = 
		{"0", "1", "2", "3",
		 "4", "5", "6", "7",
		 "8", "9", ".", "+/-", 	// 8 - 11
		 "C", "CE", "del", "=",	// 12 - 15
		 "+", "-", "*", "/", 	// 16 - 19
		 "sqrt", "%", "1/x", "Pi",	// 20 - 23
		 "e", "res", "(", ")",	// 24 - 27
		 "sin", "cos", "tan", "sqr", 	// 28 - 31
		 "yroot", "^", "cbrt", "cube", 	// 32 - 35
		 "log", "powten", "ln", "exp()",	// 36 - 39
		 "sinh", "cosh", "tanh", " ", 	// 40 - 43
		 " ", " ", " ", " " // 44 - 47
		};
	//private enum Actions { NUMBER, FUNCTION, MEMORY, OPERATOR};
	private JButton[] buttons = new JButton[bts.length];
	private String buttonfont = "Droid Sans";
	private int[] buttonfontsize = {40, 40, 40, 28, 21, 17, 14};
	private Color buttoncolorbg = new Color(191, 191, 191);
	private Color buttoncolorfg = new Color(15, 15, 15);

	// ----
	private JTextField outputtop = new JTextField("expression", 1);
	private JTextField outputbot = new JTextField("0", 1);
	private String[] outputfont = {"Serif", "Ubuntu"};
	private int[] outputfontsize = {30, 40};
	private Color[] outputbg = 
		{new Color(240, 240, 240),
		 new Color(192, 192, 192)
		};
	private Color[] outputfg = 
		{new Color(16, 16, 16),
		 new Color(64, 64, 64)
		};
	
	// ---- 
	private int flag = 0x0001;

	// ----
	private JPanel pmain = new JPanel(); // main panel

	private JPanel ptext = new JPanel(); // output and text
	private JPanel psimp = new JPanel(); // simple
	private JPanel pscie = new JPanel(); // scientific

	private JPanel pbase = new JPanel(); // base zone : backspace C CE
	private JPanel pnumb = new JPanel(); // number zone : . 0-9 
	private JPanel pfunc = new JPanel(); // function zone : MC MR MS M+ M-
	private JPanel pops1 = new JPanel(); // operators zone 1 : + - * / +/- = 1/x % sqrt
	private JPanel pops2 = new JPanel(); // operators zone 2 : 

	// ----
	public calc(){
		this.addWindowListener(new WindowAdapter(){
			// close window and exit
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});

		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.isControlDown() == true && e.getKeyCode() == KeyEvent.VK_Q){
					// close window and exit
					System.exit(0);
				} 
				// Alt + 1 / 2 / 3
			}

			public void keyReleadsed(KeyEvent e){
				;
			}

			public void keyTyped(KeyEvent e){ 
				;
			}
		});

		// ----
		int i, fontsize;
		for(i = 0; i < bts.length; ++i){
			buttons[i] = new JButton(bts[i]);
			buttons[i].setPreferredSize(new Dimension(50, 50));
			buttons[i].setMargin(new Insets(0,-20,0,-20));
			fontsize = buttonfontsize[bts[i].length()];
			buttons[i].setFont(new Font(buttonfont, Font.BOLD, fontsize));
			buttons[i].setBackground(buttoncolorbg);
			buttons[i].setForeground(buttoncolorfg);
			buttons[i].addActionListener(this);
			buttons[i].setActionCommand(bts[i]);
		}

		// ----
		outputtop.setFont(new Font(outputfont[0], Font.PLAIN, outputfontsize[0]));
		outputtop.setHorizontalAlignment(JTextField.RIGHT);
		outputtop.setEnabled(false);
		outputtop.setBackground(outputbg[0]);
		outputtop.setDisabledTextColor(outputfg[0]);
		outputtop.setForeground(outputfg[0]);
		outputbot.setFont(new Font(outputfont[1], Font.BOLD , outputfontsize[1]));
		outputbot.setHorizontalAlignment(JTextField.RIGHT);
		outputbot.setEnabled(false);
		outputbot.setBackground(outputbg[1]);
		outputbot.setDisabledTextColor(outputfg[1]);
		outputbot.setForeground(outputfg[1]);

		// ----
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		//GroupLayout gp = new GroupLayout();
		
		// output zone
		ptext.setLayout(new GridLayout(2, 1, 0, 5));
		ptext.add(outputtop);
		ptext.add(outputbot);
		
		// number zone
		pnumb.setLayout(gridbag);
		//pnumb.setSize(60, 80);
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1);
		//c.ipadx = 20; c.ipady = 20;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(buttons[7], c);
		pnumb.add(buttons[7]);
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(buttons[8], c);
		pnumb.add(buttons[8]);
		c.gridx = 2; c.gridy = 0; gridbag.setConstraints(buttons[9], c);
		pnumb.add(buttons[9]);
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(buttons[4], c);
		pnumb.add(buttons[4]);
		c.gridx = 1; c.gridy = 1; gridbag.setConstraints(buttons[5], c);
		pnumb.add(buttons[5]);
		c.gridx = 2; c.gridy = 1; gridbag.setConstraints(buttons[6], c);
		pnumb.add(buttons[6]);
		c.gridx = 0; c.gridy = 2; gridbag.setConstraints(buttons[1], c);
		pnumb.add(buttons[1]);
		c.gridx = 1; c.gridy = 2; gridbag.setConstraints(buttons[2], c);
		pnumb.add(buttons[2]);
		c.gridx = 2; c.gridy = 2; gridbag.setConstraints(buttons[3], c);
		pnumb.add(buttons[3]);
		c.gridwidth = 2;
		c.gridx = 0; c.gridy = 3; gridbag.setConstraints(buttons[0], c);
		pnumb.add(buttons[0]);
		c.gridwidth = 1;
		c.gridx = 2; c.gridy = 3; gridbag.setConstraints(buttons[10], c);
		pnumb.add(buttons[10]);

		// base zone 
		pbase.setLayout(gridbag);
		//pbase.setSize(60, 20);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);
		//c.ipadx = 20; c.ipady = 20;
		c.gridx = 2; c.gridy = 0; gridbag.setConstraints(buttons[12], c);
		pbase.add(buttons[12]);
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(buttons[13], c);
		pbase.add(buttons[13]);
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(buttons[14], c);
		pbase.add(buttons[14]);

		// operators zone 1
		pops1.setLayout(gridbag);
		//pops1.setSize(40, 100);
		//c.fill = GridBagConstraints.VERTICAL;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1);
		//c.ipadx = 20; c.ipady = 20;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(buttons[11], c);
		pops1.add(buttons[11]);
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(buttons[19], c);
		pops1.add(buttons[19]);
		c.gridx = 0; c.gridy = 2; gridbag.setConstraints(buttons[18], c);
		pops1.add(buttons[18]);
		c.gridx = 0; c.gridy = 3; gridbag.setConstraints(buttons[17], c);
		pops1.add(buttons[17]);
		c.gridx = 0; c.gridy = 4; gridbag.setConstraints(buttons[16], c);
		pops1.add(buttons[16]);
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(buttons[20], c);
		pops1.add(buttons[20]);
		c.gridx = 1; c.gridy = 1; gridbag.setConstraints(buttons[21], c);
		pops1.add(buttons[21]);
		c.gridx = 1; c.gridy = 2; gridbag.setConstraints(buttons[22], c);
		pops1.add(buttons[22]);
		c.gridheight = 2;
		c.gridx = 1; c.gridy = 3; gridbag.setConstraints(buttons[15], c);
		pops1.add(buttons[15]);
		c.gridheight = 1;
	
		// operators zone 2
		pops2.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 1, 1);
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(buttons[36], c);
		pops2.add(buttons[36]);
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(buttons[37], c);
		pops2.add(buttons[37]);
		c.gridx = 2; c.gridy = 0; gridbag.setConstraints(buttons[38], c);
		pops2.add(buttons[38]);
		c.gridx = 3; c.gridy = 0; gridbag.setConstraints(buttons[39], c);
		pops2.add(buttons[39]);
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(buttons[32], c);
		pops2.add(buttons[32]);
		c.gridx = 1; c.gridy = 1; gridbag.setConstraints(buttons[33], c);
		pops2.add(buttons[33]);
		c.gridx = 2; c.gridy = 1; gridbag.setConstraints(buttons[34], c);
		pops2.add(buttons[34]);
		c.gridx = 3; c.gridy = 1; gridbag.setConstraints(buttons[35], c);
		pops2.add(buttons[35]);
		c.gridx = 0; c.gridy = 2; gridbag.setConstraints(buttons[40], c);
		pops2.add(buttons[40]);
		c.gridx = 1; c.gridy = 2; gridbag.setConstraints(buttons[41], c);
		pops2.add(buttons[41]);
		c.gridx = 2; c.gridy = 2; gridbag.setConstraints(buttons[42], c);
		pops2.add(buttons[42]);
		c.gridx = 3; c.gridy = 2; gridbag.setConstraints(buttons[26], c);
		pops2.add(buttons[26]);
		c.gridx = 0; c.gridy = 3; gridbag.setConstraints(buttons[28], c);
		pops2.add(buttons[28]);
		c.gridx = 1; c.gridy = 3; gridbag.setConstraints(buttons[29], c);
		pops2.add(buttons[29]);
		c.gridx = 2; c.gridy = 3; gridbag.setConstraints(buttons[30], c);
		pops2.add(buttons[30]);
		c.gridx = 3; c.gridy = 3; gridbag.setConstraints(buttons[27], c);
		pops2.add(buttons[27]);
		c.gridx = 0; c.gridy = 4; gridbag.setConstraints(buttons[25], c);
		pops2.add(buttons[25]);
		c.gridx = 1; c.gridy = 4; gridbag.setConstraints(buttons[45], c);
		pops2.add(buttons[45]);
		c.gridx = 2; c.gridy = 4; gridbag.setConstraints(buttons[23], c);
		pops2.add(buttons[23]);
		c.gridx = 3; c.gridy = 4; gridbag.setConstraints(buttons[24], c);
		pops2.add(buttons[24]);

		// simple 
		psimp.setLayout(gridbag);
		c.insets = new Insets(0, 0, 0, 0);
		// psimp.setSize();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(pbase, c);
		psimp.add(pbase);
		c.gridheight = 2;
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(pops1, c);
		psimp.add(pops1);
		c.gridheight = 1;
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(pnumb, c);
		psimp.add(pnumb);

		// scientific  
		pscie.setLayout(gridbag);
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(pops2, c);
		pscie.add(pops2);
		c.gridx = 1; c.gridy = 0; gridbag.setConstraints(psimp, c);
		pscie.add(psimp);

		// ----
		resizeMainPanel();
		
		// ----
		clearResult();

		this.add(pmain);
		this.setSize(pmain.getPreferredSize());
		this.setVisible(true);
	}

	private JPanel selectInputPanel(){
		JPanel p;
		switch(flag & FLAG_TYPE){
		case TYPE_SCIENTIFIC :
			p = pscie;
			break;
		case TYPE_SIMPLE :
		default :
			p = psimp;
			break;
		}
		return p;
	}		

	private void resizeMainPanel(){
		// package 
		JPanel pinput = selectInputPanel();
		//JOptionPane.showMessageDialog(null, pinput.getPreferredSize(), "", JOptionPane.INFORMATION_MESSAGE);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		int w, h1, h2;
		w = pinput.getPreferredSize().width;
		h1 = pinput.getPreferredSize().height;
		h2 = outputfontsize[0] + outputfontsize[1] + 5;
	//	ptext.setPreferredSize(new Dimension(w-(int)(Math.floor(w/50)-1)*2, h2));
		ptext.setPreferredSize(new Dimension(w-2, h2));
		
		pmain.setLayout(gridbag);
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0; c.gridy = 0; gridbag.setConstraints(ptext, c);
		pmain.add(ptext);
		c.insets = new Insets(0,5,5,5);
		c.gridx = 0; c.gridy = 1; gridbag.setConstraints(pinput, c);
		c.insets = new Insets(0,0,0,0);
		pmain.add(pinput);
	}

	private void clearResult(){
		outputtop.setText("");
		outputbot.setText("0");
		tint = 0;
		tdouble = 0.0;
		tnum = "";
		top = "";
		lastres = 0.0;
		lastinput = "";
		flag = 0x0001;
	}

	private void clearStack(){
		while(mainstack.empty() == false)
			mainstack.pop();
		while(opstack.empty() == false)
			opstack.pop();
	}


	// operators
	private static String[] constants = 
		{"Pi", "e", "res"
		};
	private static String[] unary = 
		{"!", "1/x", "fact", "+/-",
		 "sqrt", "sqr", "cube", "cbrt",
		 "log", "powten", "ln", "exp()",
		 "sin", "cos", "tan",
		 "sinh", "cosh", "tanh"
		};
	private static String[] binocular = 
		{"|", "&", "Xor", "Mod",
		 "yroot", "^", "/", "*",
		 "-", "+"
		};

	private void toPostfix(String op){
		String top = "";
		if(op == ""){
			while(opstack.empty() == false){
				top = opstack.pop();
				dbg("= push " + top);
				mainstack.push(top);
			}
		} else if(opstack.empty() == false){
		while(opstack.empty() == false){
			top = opstack.peek();
			if(op == ")" && top != "("){
				opstack.pop();
				dbg(") trans " + top);
				mainstack.push(top);
			} else if(op == ")" && top == "("){
				opstack.pop();
				dbg(") find (");
				if(opstack.empty() == false)
				dbg("now top is " + (opstack.peek()));
				return;
			} else if(op == "("){
				dbg("now op is " + op);
				opstack.push(op);
				return;
			} else if(top == "("){
				dbg("now top is " + top + ", op is " + op);
				opstack.push(op);
				return;
			} else if(Arrays.asList(unary).indexOf(op) >= 0){
				dbg("now op is " + op);
				opstack.push(op);
				return;
			} else if(Arrays.asList(binocular).indexOf(op) >= 0){
				if(compareOps(op, top) > 0){
					dbg("now op is " + op);
					opstack.push(op);
					return;
				} else {
					opstack.pop();
					dbg("push " + top);
					mainstack.push(top);
				}
			} else if(Arrays.asList(constants).indexOf(op) >= 0){
				dbg("push " + op);
				mainstack.push(getConstantsValue(op));
				return;
			}
		}
			dbg("now op is " + op);
			opstack.push(op);	
		} else {
			if(op == ")"){
				;
			} else {
				dbg("now op is " + op);
				opstack.push(op);
			}
		}
		
	}
	
	private void produceNum(){
		if(tnum == "")
			tnum = "0";
		tdouble = Double.parseDouble(tnum);
		tint = (int)Math.floor(tdouble);
		tnum = "";
	}

	private void inputNum(String i){
		int type = Arrays.asList(bts).indexOf(lastinput);
		if(type == 23 || type == 24 || type == 25){
			produceNum();
		}

		switch(i){
			case "." :
				if(tnum.length() == 0)
					tnum += "0";
				else if(tnum.indexOf('.') >= 0)
					break;
				tnum += ".";
				break;
			case "0" :
				if(tnum.length() == 0)
					break;
				else if(tnum.length() == 1 && tnum.charAt(0) == '0')
					break;
			case "1" :
			case "2" :
			case "3" :
			case "4" :
			case "5" :
			case "6" :
			case "7" :
			case "8" :
			case "9" :
				tnum += i;
				break;
			default :
				break;
		}
		lastinput = i; 
	}

	private void inputOp(String i){
		int type = Arrays.asList(bts).indexOf(lastinput);
		int cur = Arrays.asList(bts).indexOf(i);

		if(type == 23 || type == 24 || type == 25){
			tnum = String.valueOf(getConstantsValue(lastinput));
			lastinput = "0";
			inputOp(i);
		} else if((type <= 10 && type >= 0) || type == 27){
			// last input is a number
			produceNum();
			if(cur == 15){
				// enter "="
				dbg(lastinput + " before =");
				if(type != 27){
					dbg("= push " + String.valueOf(tdouble));
					mainstack.push(tdouble);
				}
				toPostfix("");
				lastres = calculate();
			} else if(cur == 26){
				// "(" at the start
				if(mainstack.size() == 0 && opstack.size() == 0){
					toPostfix(i);
					lastinput = i;
				}
			} else if(cur == 27){
				// ")"
				if(mainstack.size() == 0 && opstack.size() == 0){
					; // *invilid*
				} else {
					dbg(") push num : " + String.valueOf(tdouble));
					mainstack.push(tdouble);
					toPostfix(i);
					lastinput = i;
				}
			} else if(Arrays.asList(unary).indexOf(i) >= 0){
				if(mainstack.size() == 0 && opstack.size() == 0){
					toPostfix(i);
					lastinput = i;
				}
			} else if(Arrays.asList(binocular).indexOf(i) >= 0){
				dbg(i + " push num : " + String.valueOf(tdouble));
				mainstack.push(tdouble);
				toPostfix(i);
				lastinput = i;
			} else if(Arrays.asList(constants).indexOf(i) >= 0){
				lastinput = i;
			} else {
				err("Invilid Operator");// *error*
			}
		} else if(type > 10){
			// last input is a operator
			if(cur == 15){
				lastres = calculate();
			} else if(cur == 26){
				// "("
				toPostfix(i);
				lastinput = i;
			} else if(cur == 27){
				// ")"
				if(type == 26){
					toPostfix(i);
				}
			} else if(Arrays.asList(unary).indexOf(i) >= 0){
				toPostfix(i);
				lastinput = i;
			} else if(Arrays.asList(binocular).indexOf(i) >= 0){
				;// ?
			} else if(Arrays.asList(constants).indexOf(i) >= 0){
				lastinput = i;
			} else {
				err("Invilid Operator : " + i);// *error*
			}
		} else {
			if(lastres == 0){
				inputNum("0");
			} else {
				tnum = String.valueOf(lastres);
			}
			lastinput = "0";
			inputOp(i);
		}
	}
					
	private double getConstantsValue(String op){
		if(op == "Pi"){
			return Math.PI;
		} else if(op == "e"){
			return Math.E;
		} else if(op == "res"){
			return lastres;
		} else {
			return 0.0;
		}
	}

	private int compareOps(String opnew, String opold){
		int posn, poso;
		if(opold == "("){
			return 1;
		} else if(Arrays.asList(unary).indexOf(opnew) >= 0){
			return 1;
		} else if((posn = Arrays.asList(binocular).indexOf(opnew)) >= 0){
			poso = Arrays.asList(binocular).indexOf(opold);
			return (int)((poso - posn) / 2);
		} else {
			return 1;
		}
	}
			
	private double calculate(){
		if(mainstack.empty())
			return 0.0; 

		Object o = mainstack.pop();
		double t1, t2;
		String ts;
		boolean f = (flag & FLAG_DISABLED_POINT) == FLAG_DISABLED_POINT;

		if(o.getClass().equals(Double.class)){
			dbg(String.valueOf(mainstack.size()) + " : " + String.valueOf((double)o));	
			return(f ? (int)o : (double)o);
		} else if(o.getClass().equals(String.class)){
			dbg(String.valueOf(mainstack.size()) + " : " + (String)o);	
			ts = (String)o;
			if(Arrays.asList(unary).indexOf(ts) >= 0){
				t1 = calculate();
				switch(ts){
					case "sqr" :
						t1 = Math.pow(t1, 2);
						break;
					case "sqrt" :
						if(t1 <= 0){
							t1 = 0; // *error*
							err("Squart Can't Calculate the Number < 0");
						} else
							t1 = Math.sqrt(t1);
						break;
					case "cube" :
						t1 = Math.pow(t1, 3);
						break;
					case "cbrt" :
						t1 = Math.cbrt(t1);
						break;
					case "1/x" :
						t1 = 1 / t1;
						break;
					case "+/-" :
						t1 = -t1;
						break;
					case "powten" :
						t1 = Math.pow(10, t1);
						break;
					case "exp()" : 
						t1 = Math.exp(t1);
						break;
					case "log" :
						t1 = Math.log(t1);
						break;
					case "ln" : 
						t1 = Math.log(t1)/Math.log(Math.E);
						break;
					case "sin" :
						t1 = Math.sin(t1);
						break;
					case "cos" :
						t1 = Math.cos(t1);
						break;
					case "tan" :
						t1 = Math.tan(t1);
						break;
					case "sinh" :
						t1 = Math.sinh(t1);
						break;
					case "cosh" :
						t1 = Math.cosh(t1);
						break;
					case "tanh" :
						t1 = Math.tanh(t1);
						break;
					default :
						t1 = 0.0 + t1; // *error*
						err("Something Invilid Input");
				}
				dbg(String.valueOf(t1));
				return(f ? (int)t1 : (double)t1);
			} else if(Arrays.asList(binocular).indexOf(ts) >= 0){
				t2 = calculate();
				t1 = calculate();
				switch(ts){
					case "+" :
						t1 += t2;
						break;
					case "-" :
						t1 -= t2;
						break;
					case "*" :
						t1 *= t2;
						break;
					case "/" :
						if(t2 == 0){
							t1 = 0.0; // *error*
							err("Divide 0");
						} else
							t1 /= t2;
						break;
					case "^" :
						if(t2 == 0)
							t1 = 1.0;
						else
							t1 = Math.pow(t1, t2);
						break;
					case "yroot" :
						if(t2 == 0){
							t1 = 0.0; // *error*
							err("Root 0");
						} else
							t1 = Math.pow(t1, 1/t2);
						break;
					default :
						break;
				}
				dbg(String.valueOf(t1));
				return(f ? (int)t1 : (double)t1);
			} else {
				dbg("error in 717 : " + ts);
				err("Invilid Input");
				return 0.0; // *error*
			}
		} else {
			dbg(o.getClass().toString());
			return 0.0;
		}
	}

	public void actionPerformed(ActionEvent e){
		String bt = ((JButton)e.getSource()).getActionCommand();
		switch(bt){
			case "0" :
			case "1" :
			case "2" :
			case "3" :
			case "4" :
			case "5" :
			case "6" :
			case "7" :
			case "8" :
			case "9" :
			case "." :
				inputNum(bt);
				outputbot.setText(tnum);
				break;
			case "C" :
				clearResult();	
				clearStack();
				break;
			case "CE" :
				tnum = "";
				outputbot.setText("");
				break;
			case "del" :
				break;
			case "(" :
			case ")" :
			case "+" :
			case "-" :
			case "*" :
			case "/" :
			case "+/-" :
			case "1/x" :
			case "sqrt" :
			case "sqr" :
			case "cube" :
			case "cbrt" :
			case "^" :
			case "yroot" :
			case "Pi" :
			case "e" :
			case "res" :
			case "sin" :
			case "cos" :
			case "tan" :
			case "sinh" :
			case "cosh" :
			case "tanh" :
			case "log" :
			case "powten" :
			case "ln" :
			case "exp()" :
				inputOp(bt);
				break;
			case "=" :
				inputOp(bt);
				tres = lastres;
				clearResult();
				clearStack();
				outputResult(String.valueOf(tres));
				lastres = tres;
				break;
			default:
				break;
		}
	}

	private void outputResult(String res){
		int f = flag & FLAG_TYPE;
		switch(f){
			case TYPE_SIMPLE :
				if(res.length() > 10)
					outputbot.setText(res.substring(0, 10));
				else 
					outputbot.setText(res);
				break;
			case TYPE_SCIENTIFIC :
				if(res.length() > 17)
					outputbot.setText(res.substring(0, 17));
				else 
					outputbot.setText(res);
				break;
			default:
				if(res.length() > 10)
					outputbot.setText(res.substring(0, 10));
				else 
					outputbot.setText(res);
		}
	}

}
	

