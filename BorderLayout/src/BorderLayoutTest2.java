import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;

public class BorderLayoutTest2 extends JFrame{
	public static void main(String[] args){
		BorderLayoutTest2 test = new BorderLayoutTest2("BorderLayoutTest2");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}
	BorderLayoutTest2(String title){
		setTitle(title);
		setBounds( 10, 10, 400, 200);

		JButton btn1 = new JButton("PAGE_START");
		JButton btn2 = new JButton("PAGE_END");
		JButton btn3 = new JButton("LINE_START");
		JButton btn4 = new JButton("LINE_END");

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(btn1, BorderLayout.PAGE_START);
		p.add(btn2, BorderLayout.PAGE_END);
		p.add(btn3, BorderLayout.LINE_START);
		p.add(btn4, BorderLayout.LINE_END);
		p.setBackground(Color.RED);
		JButton btn5_1 = new JButton("IN_PAGE_START");
		JButton btn5_2 = new JButton("IN_PAGE_END");
		JButton btn5_3 = new JButton("IN_CENTER");

		JPanel inpanel = new JPanel();
		inpanel.setLayout(new BorderLayout());
		inpanel.add(btn5_1, BorderLayout.PAGE_START);
		inpanel.add(btn5_2, BorderLayout.PAGE_END);
		inpanel.add(btn5_3, BorderLayout.CENTER);

		p.add(inpanel, BorderLayout.CENTER);
		p.setBackground(Color.RED);
		getContentPane().add(p);
	}
}
