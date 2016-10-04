import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class GridBagTest2 extends JFrame{
	public static void main(String[] args){
		GridBagTest2 test = new GridBagTest2("GridBagTest2");
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setVisible(true);
	}
	GridBagTest2(String title){
		setTitle(title);
		setBounds( 100, 100, 320, 568);

		GridBagLayout layout = new GridBagLayout();
		JPanel p = new JPanel();
		p.setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		/* ボタン1は0行0列-1行1列の位置へ  */
		JButton btn1 = new JButton("Tennis");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.weightx = 1.0d;
		gbc.weighty = 1.0d;
		layout.setConstraints(btn1, gbc);
		p.add(btn1);

		/* ボタン2は0行2列の位置へ  */
		JButton btn2 = new JButton("Golf");
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0d;
		gbc.weighty = 1.0d;
		layout.setConstraints(btn2, gbc);
		p.add(btn2);

		/* ボタン3は1行2列の位置へ  */
		JButton btn3 = new JButton("BaseBall");
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0d;
		gbc.weighty = 1.0d;
		layout.setConstraints(btn3, gbc);
		p.add(btn3);

		getContentPane().add(p);
	}
}
