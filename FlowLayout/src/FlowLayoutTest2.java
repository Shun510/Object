/*
 * FlowLayoutTest2.java
*
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class FlowLayoutTest2 extends JFrame{
	public static void main(String[] args){
		FlowLayoutTest2 test = new FlowLayoutTest2("FlowLayoutTest2");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}

	FlowLayoutTest2(String title){
		setTitle(title);
		setBounds( 10, 10, 700, 200);

		JButton btn1_1 = new JButton("Button1");
		JButton btn1_2 = new JButton("Button2");
		JButton btn1_3 = new JButton("Button3");
		JButton btn1_4 = new JButton("Button4");
		JButton btn1_5 = new JButton("Button5");
		JButton btn1_6 = new JButton("Button6");
		JButton btn1_7 = new JButton("Button7");

		JPanel p1 = new JPanel();

		FlowLayout layout = new FlowLayout();
		layout.setHgap(15);
		layout.setVgap(2);
		p1.setLayout(layout);

		p1.add(btn1_1);
		p1.add(btn1_2);
		p1.add(btn1_3);
		p1.add(btn1_4);
		p1.add(btn1_5);
		p1.add(btn1_6);
		p1.add(btn1_7);

		getContentPane().add(p1, BorderLayout.CENTER);
	}
}

 
