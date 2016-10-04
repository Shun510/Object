import java.awt.*;
import java.awt.event.*;
import javax.swing.* ;
import javax.swing.event.ChangeListener ;
import javax.swing.event.ChangeEvent;
import java.awt.geom.Rectangle2D ;

class Pole {
	final static Color		COLOR = new Color(92,40,51);				// �_�̐F
	final static double		THICK = 1.0;								// �_�̗֊s���̕�
	final static BasicStroke STROKE								// �_�̗֊s���̌`��
								= new BasicStroke((float)THICK);
	int	location;													// �_�̈ʒu�i�O�`�Q�j
	int	num;													// �_�ɂ����ꂽ�~�Ղ̐�
	Disk	cont[];													// �_�ɂ����ꂽ�~��

	Pole(int loc, int n){											// �_�̐���
		location = loc;											// �_�̈ʒu��ۑ�
		num = 0;													// �~�Ր� �O
		cont = new Disk[n];										// �~�Ղ�ێ�����z��̐���
	}

	void push(Disk ban){											// �_�ɉ~�Ղ���������
		cont[num++] = ban;
	}

	Disk pop(){
		if(num<1) return null;										// �Ղ��Ȃ��Ƃ� null
		else		return cont[--num];								// �͂������~�Ղ�l�Ƃ���
	}
}

class Disk {
	final static double		THICK = 1.0;								// �~�Ղ̗֊s���̑���
	final static BasicStroke STROKE = new BasicStroke((float)THICK);
	int			size;												// �~�Ղ̑傫��
	Color		color;											// �~�Ղ̐F
 
	Disk(int s, Color c){											// �~�Ղ̐���
		size	= s;
		color = c;													// �傫���ƐF��ۑ�
	}

	Color diskcolor(){												// �~�Ղ̐F
		return color;
	}

	int disksize(){													// �~�Ղ̑傫��
		return size;
	}
}

class ViewPanel extends JPanel {
	final static Rectangle2D.Double box								// �\���p�̎l�p
						= new Rectangle2D.Double();
	final static double GAP = 5.0;									// �\���̊Ԋu

	ViewPanel(int width, int height) {								// �\���p�p�l���̐���
		setBackground(new Color(255, 255, 168));
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
	}

	public void display(Graphics g, Pole p){
		Graphics2D gg = (Graphics2D)g;
		double w	= getWidth();										// �p�l���̉���
		double h	= getHeight();										// �p�l���̏c��
		double xd	= (w - 4.0*GAP) / 3.0;							// �_�P�{���̕\����
		double yd2 = (h-60.0)/(double)Hanoi.MAXDISK;				// �㉺ 30.0 �Â̋��
		double yd	= yd2 - 2.0;									// �~�Ղ̌��� 
																// �\���̏c�� - �~�ՊԂ̌���
		double x0	= GAP + xd/2.0 + (GAP + xd)*p.location;			// �_�̉��ʒu
		double y0	= h - 30.0;									// �_�̉��[�̂����W
		gg.setColor(Pole.COLOR);									// �_�̐F
		box.setRect(x0-3.0, y0-(Hanoi.ndisk+1)*yd2,					// �_�̏����o���ʒu��
					6.0, (Hanoi.ndisk+1)*yd2);						// �_�̕��ƒ���
		gg.fill(box);												// �_�̕\��
		for(int i=0; i<p.num; i++){									// �_�ɂ����ꂽ�S�~�Ղ�������
			Disk d = p.cont[i];										// �~��
			gg.setStroke(Disk.STROKE);							// �~�Ղ̗֊s��
			box.setRect(x0-d.size/2.0, y0-yd,							// �~�Ղ̈ʒu�Ƒ傫��
						(double)d.disksize(), yd);
			gg.setColor(d.diskcolor());								// �~�Ղ̐F
			gg.fill(box);											// �~�Ղ�`��
			gg.setColor(Color.black);								// �֊s���̐F
			gg.draw(box);										// �~�Ղ̗֊s����`��
			y0 -= yd2;											// �\���ʒu�̂����W
		}
	}

	public void paintComponent(Graphics g) {						// �R�{�̎���`��
		super.paintComponent(g);
		if(Hanoi.poles[0] != null) display(g, Hanoi.poles[0]);
		if(Hanoi.poles[1] != null) display(g, Hanoi.poles[1]);
		if(Hanoi.poles[2] != null) display(g, Hanoi.poles[2]);
	}
}

public class Hanoi extends JFrame implements Runnable{
	final static int	MAXDISK = 31;									// �~�Ղ̐��̍ő�
	static int			ndisk;										// �~�Ղ̐�
	static JTextField	ndiskText									// �~�Ր��̎w�藓
						= new JTextField("10", 5);
	static Pole[]		poles = new Pole[3];							// �_
	static ViewPanel	mainpanel;									// �_�Ɖ~�Ղ̃r���[
	static JLabel		countLabel									// �ړ��񐔕\���p
						= new JLabel("",	SwingConstants.RIGHT);
	static int			disp;										// �\������
	static JSlider		slider = new JSlider(0, 150, 100);					// �\�����ԕύX�p
	static int			movecount ;									// �~�Ղ̈ړ���
	static Thread		thread;
	static volatile boolean trap;		// �ꎞ��~�t���O�iSTART�{�^���̉�����҂�ԁj
	static volatile boolean quit;		// ���~�t���O�i�l�X�g�����v�Z����ł��邾�������߂�j

	Hanoi(String s){
		super(s);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});

		ndiskText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit = true;								// �~�Ր������͂��ꂽ��
				trap = false;								//	�v�Z�E�\�����I������B
			}
		});


		slider.setBackground(new Color(0, 200, 200));
		slider.addChangeListener(new ChangeListener() {				// �\�����Ԃ��w�肷��X���C�_
			public void stateChanged(ChangeEvent e) {
				disp = slider.getValue();							// �������ꂽ��\�����Ԃ�ύX
			}
		});

		final JButton gbutton = new JButton("�J�n");					// �~�Ղ̈ړ��J�n���w������{�^��
		gbutton.setBackground(new Color(0, 240, 255));
		gbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trap = false;										// �����ꂽ�� trap �t���O���N���A
			}
		});

		final JButton sbutton = new JButton("��~");				// �~�Ղ̈ړ����f���w������{�^��
		sbutton.setBackground(new Color(0, 240, 255));
		sbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trap = true;									// �����ꂽ�� trap �t���O��ݒ�
			}
		});

		GridBagLayout gridbag = new GridBagLayout();			// SOUTH �̃p�l���̔z�u
		GridBagConstraints constraints							// �z�u�K��
								= new GridBagConstraints();
		JPanel menupanel = new JPanel(gridbag);				// SOUTH �p�̃p�l��
		menupanel.setBackground(new Color(0, 200, 200));	

		constraints.gridx = 0;									// �p�l���̍��ォ��
		constraints.gridy = 0;
		constraints.gridwidth= 1;								// �P�}�X����
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 10, 0, 0);					// �����ɃX�L�}�P�O�h�b�g

		JLabel ndiskLabel = new JLabel("�~�Ր�") ; 				// "Number of Disks"���x��
		gridbag.setConstraints(ndiskLabel, constraints);
		menupanel.add(ndiskLabel);

		ndiskText.setBackground(new Color(255, 220, 220));
		ndiskText.setHorizontalAlignment(SwingConstants.CENTER); // �t�B�[���h�̒��S�ɕ\��
		constraints.gridy = GridBagConstraints.RELATIVE;	// "Number of Disks"���x���̉���
		gridbag.setConstraints(ndiskText, constraints);
		menupanel.add(ndiskText);

		JLabel glue1 = new JLabel("	");						// �󃉃x��
		constraints.gridx = 1;									// �P��ڂ̏ォ��
		constraints.gridy = 0;
		constraints.gridheight = 2;								// �c�ɂQ�}�X��L
		constraints.weightx = 100.0;								// ���ɂЂ낪��
		constraints.insets = new Insets(0, 0, 0, 0);					// �p�l���̗��[�ȊO�̓[��
		gridbag.setConstraints(glue1, constraints);
		menupanel.add(glue1);

		constraints.gridx = 2;									// �p�l���̂Q��ڂ̏�̒i
		constraints.gridy = 0;
		constraints.gridwidth = 2;								// ���ɂQ�}�X��L
		constraints.gridheight = 1;								// �c�͂P�}�X
		constraints.weightx = 0.0;								// ���ɂ͂Ђ낪��Ȃ�
		gridbag.setConstraints(slider, constraints);				// �ȏ�̓X���C�_�̌�
		menupanel.add(slider);

		JLabel sliderQuick = new JLabel("����");				// "quick"�{�^��
		constraints.anchor = GridBagConstraints.NORTHWEST;	// ������Ɋ����
		constraints.gridx = 2;									// �Q��ڂ�
		constraints.gridy = 1;									// �P�s��
		constraints.gridwidth = 1;								// �P�}�X��L
		gridbag.setConstraints(sliderQuick, constraints);
		menupanel.add(sliderQuick);

		JLabel sliderSlow	= new JLabel("�ᑬ");					// "slow"�{�^��
		constraints.anchor = GridBagConstraints.NORTHEAST;	// �E����Ɋ����
		constraints.gridx = 3;									// �R��ڂ�
		gridbag.setConstraints(sliderSlow, constraints);
		menupanel.add(sliderSlow);

		JLabel glue2 = new JLabel("	");						// �󃉃x��
		constraints.anchor = GridBagConstraints.CENTER;		// �E����Ɋ��̂͏I��
		constraints.gridx = 4;									// �S��ڂ̏ォ��
		constraints.gridy = 0;
		constraints.gridheight = 2;								// �c�ɂQ�}�X��L
		constraints.weightx = 50.0;								// ���ɍL����i���Ȃ߁j
		gridbag.setConstraints(glue2, constraints);
		menupanel.add(glue2);

		constraints.gridx = 5;									// �T��ڂ̏ォ��
		constraints.gridy = 0;
		constraints.gridheight = 1;								// �P�}�X��L
		constraints.weightx = 0.0;								// ���ɂ͍L����Ȃ�
		constraints.fill = GridBagConstraints.BOTH;				// �}�X�����ς��ɍL���Ĕz�u
		gridbag.setConstraints(gbutton, constraints);				// �ȏ�� "start"�{�^���̌�
		menupanel.add( gbutton );

		constraints.gridy = 1;									// �P�s��
		gridbag.setConstraints(sbutton, constraints);				// �ȏ��"stop"�{�^���̌�
		menupanel.add( sbutton );								//

		JLabel glue3 = new JLabel("	");						// �󃉃x��
		constraints.gridx = 6;									// �U�s�ڂ̏ォ��
		constraints.gridy = 0;
		constraints.gridheight = 2;								// �Q�}�X��L
		constraints.weightx = 100.0;								// ���ɍL���闓
		constraints.fill = GridBagConstraints.NONE;				// �}�X�ɍL���Ĕz�u���Ȃ�
		gridbag.setConstraints(glue3, constraints);
		menupanel.add(glue3);

		JLabel countHLabel = new JLabel("�ړ���");				// "move count"���x��
		constraints.gridx = 7;									// �V��ڂɔz�u
		constraints.gridy = 0;									// ��ԏ�
		constraints.gridheight = 1;								// �P�}�X��L
		constraints.weightx = 0.0;								// ���ɂ͍L����Ȃ�
		constraints.insets = new Insets(0, 0, 0, 10);					// �E���ɃX�L�}�P�O�h�b�g
		gridbag.setConstraints(countHLabel, constraints);
		menupanel.add( countHLabel );

		constraints.gridy = GridBagConstraints.RELATIVE;		// "move count"���x���̉���
		gridbag.setConstraints(countLabel, constraints);			// countLabel��z�u
		menupanel.add( countLabel );

		mainpanel = new ViewPanel(450, 300);					// �\���p�l��

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		cont.add( menupanel, BorderLayout.SOUTH );
		cont.add( mainpanel, BorderLayout.CENTER );
	}

	static void shift(Pole a, Pole b, Pole c, int n){
		Disk d;
		if(quit) return;										// ���~
		if(n==0) {
			sleep(disp);										// �Ȃɂ������\���̂�
			return;
		}
		else {
			shift(a, c, b, n-1);									// a ���� c �� n-1 �� shift
			if(quit) return;									// ���~
			d = a.pop();										// a ���� �P�����o��
			mainpanel.repaint();								// ��ʂ��X�V
			countLabel.setText(String.valueOf(++movecount));		// �ړ��񐔂𑝂₵�ĕ\��
			b.push(d);										// ���̂P���� b �ɑ}��
			mainpanel.repaint();								// ��ʂ��X�V
			shift(c, b, a, n-1);									// c ���� b �� n-1 �� shift
		}
	}

	public static void main(String arg[]){
		Hanoi frame = new Hanoi("Tower of Hanoi");
		frame.pack();
		frame.setVisible(true);
		thread = new Thread(frame);
		thread.start();
	}

	public void run() {
		while(true) {											// �i�v���[�v
			trap = true;										// START�{�^�������܂�
			quit = false;										// �v�Z���~���Ȃ�
			movecount = 0;									// �ړ��� �N���A
			countLabel.setText(String.valueOf(movecount));		// �ړ��� �\��
			ndisk = Integer.parseInt(ndiskText.getText().trim());	// �f�B�X�N���擾
			if(ndisk>MAXDISK-1) {								// �f�B�X�N���̎w��
				System.out.println("Max Disks: " + (MAXDISK-1));	// �傫������|
				ndisk = MAXDISK-1;								// �ő勖�e�l�ɕύX
				ndiskText.setText(String.valueOf(ndisk));			//	�\�����X�V
			}
			disp = slider.getValue();							// �\�����Ԃ��擾
			for(int i=0; i<3; i++)								// �R�{��
				{ poles[i] = new Pole(i, MAXDISK); }				// �|�[�����쐬
			for(int i=ndisk-1; i>=0; i--) {						// �S�f�B�X�N��
				poles[0].push(new Disk(10*(i+1), Color.red));		// ���������ɍ쐬
			}
			repaint();										// ��ʍX�V
			sleep(0);											// ��莞�ԕ\��
			shift(poles[0], poles[1], poles[2], ndisk);				// �J�n�I�I
			trap = true;										// ����ȏ�v�Z���Ȃ�
			sleep(0);											// �ŏI���ʂ̕\���p��
		}
	}

	static void sleep(int time) {
		while(--time>0) {										// 0.01 �b�� sleep ��
			try{ Thread.sleep(10); }							// time �� �J��Ԃ�
				catch(InterruptedException e) { }				// time �ɂ� disp ���n�����
		}
		while(trap) {											// START �{�^�����������܂�
			try{ Thread.sleep(10); }							//	��ɐi�܂Ȃ�
			catch(InterruptedException e) { }
		}
	}
}
