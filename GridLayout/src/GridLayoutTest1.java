import javax.swing.*;
import java.awt.event.*;
import java.awt.GridLayout;

public class GridLayoutTest1 extends JFrame{
	public static void main(String[] args){
		GridLayoutTest1 test = new GridLayoutTest1("GridLayoutTest1");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}
	GridLayoutTest1(String title){
		setTitle(title);
		setBounds( 10, 10, 300, 200);

		JButton btn1 = new JButton("1:Tennis");
		JButton btn2 = new JButton("2:Golf");
		JButton btn3 = new JButton("3:FootBall");
		JButton btn4 = new JButton("4:Swimming");
		JButton btn5 = new JButton("5:BaseBall");
		JButton btn6 = new JButton("6:Basketball");
		JButton btn7 = new JButton("7:ball");

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 2));

		p.add(btn1);
		p.add(btn2);
		p.add(btn3);
		p.add(btn4);
		p.add(btn5);
		p.add(btn6);
		p.add(btn7);
		
		getContentPane().add(p);
	}
}

