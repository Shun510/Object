/*
 * BorderLayoutTest1.java
 *
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;

public class BorderLayoutTest1 extends JFrame{
	public static void main(String[] args){
		BorderLayoutTest1 test = new BorderLayoutTest1("BorderLayoutTest1");

		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}

	BorderLayoutTest1(String title){
		setTitle(title);
		setBounds( 10, 10, 400, 200);

		JButton btn1 = new JButton("PAGE_START");
		JButton btn2 = new JButton("PAGE_END");
		JButton btn3 = new JButton("LINE_START");
		JButton btn4 = new JButton("LINE_END");
		JButton btn5 = new JButton("CENTER");

		BorderLayout layout = new BorderLayout();
		layout.setHgap(2);
		layout.setVgap(5);

		JPanel p = new JPanel();
		p.setLayout(layout);
		p.add(btn1, BorderLayout.PAGE_START);
		p.add(btn2, BorderLayout.PAGE_END);
		p.add(btn3, BorderLayout.LINE_START);
		p.add(btn4, BorderLayout.LINE_END);
		p.add(btn5, BorderLayout.CENTER);
		p.setBackground(Color.RED);

		getContentPane().add(p);
	}
}
