/*
 * FlowLayoutTest1.java
 *
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;

public class FlowLayoutTest1 extends JFrame{
	public static void main(String[] args){
		FlowLayoutTest1 test = new FlowLayoutTest1("FlowLayoutTest1");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}
	FlowLayoutTest1(String title){
		setTitle(title);
		setBounds( 10, 10, 400, 200);

		JButton btn1_1 = new JButton("Button1_1");
		JButton btn1_2 = new JButton("Button1_2");

		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1.add(btn1_1);
		p1.add(btn1_2);
		p1.setBackground(Color.RED);
		
		JButton btn2_1 = new JButton("Button2_1");
		JButton btn2_2 = new JButton("Button2_2");

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));
		p2.add(btn2_1);
		p2.add(btn2_2);
		p2.setBackground(Color.GREEN);

		JButton btn3_1 = new JButton("Button3_1");
		JButton btn3_2 = new JButton("Button3_2");
		JButton btn3_3 = new JButton("");

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p3.add(btn3_1);
		p3.add(btn3_2);
		p3.setBackground(Color.YELLOW);

		getContentPane().add(p1, BorderLayout.PAGE_START);
		getContentPane().add(p2, BorderLayout.CENTER);
		getContentPane().add(p3, BorderLayout.PAGE_END);
	}
}
