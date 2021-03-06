import java.awt.*;
import java.awt.event.*;
import javax.swing.* ;
import javax.swing.event.ChangeListener ;
import javax.swing.event.ChangeEvent;
import java.awt.geom.Rectangle2D ;

class Pole {
	final static Color		COLOR = new Color(92,40,51);				// 棒の色
	final static double		THICK = 1.0;								// 棒の輪郭線の幅
	final static BasicStroke STROKE								// 棒の輪郭線の形状
								= new BasicStroke((float)THICK);
	int	location;													// 棒の位置（０〜２）
	int	num;													// 棒にさされた円盤の数
	Disk	cont[];													// 棒にさされた円盤

	Pole(int loc, int n){											// 棒の生成
		location = loc;											// 棒の位置を保存
		num = 0;													// 円盤数 ０
		cont = new Disk[n];										// 円盤を保持する配列の生成
	}

	void push(Disk ban){											// 棒に円盤をさしこむ
		cont[num++] = ban;
	}

	Disk pop(){
		if(num<1) return null;										// 盤がないとき null
		else		return cont[--num];								// はずした円盤を値とする
	}
}

class Disk {
	final static double		THICK = 1.0;								// 円盤の輪郭線の太さ
	final static BasicStroke STROKE = new BasicStroke((float)THICK);
	int			size;												// 円盤の大きさ
	Color		color;											// 円盤の色
 
	Disk(int s, Color c){											// 円盤の生成
		size	= s;
		color = c;													// 大きさと色を保存
	}

	Color diskcolor(){												// 円盤の色
		return color;
	}

	int disksize(){													// 円盤の大きさ
		return size;
	}
}

class ViewPanel extends JPanel {
	final static Rectangle2D.Double box								// 表示用の四角
						= new Rectangle2D.Double();
	final static double GAP = 5.0;									// 表示の間隔

	ViewPanel(int width, int height) {								// 表示用パネルの生成
		setBackground(new Color(255, 255, 168));
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
	}

	public void display(Graphics g, Pole p){
		Graphics2D gg = (Graphics2D)g;
		double w	= getWidth();										// パネルの横幅
		double h	= getHeight();										// パネルの縦幅
		double xd	= (w - 4.0*GAP) / 3.0;							// 棒１本分の表示幅
		double yd2 = (h-60.0)/(double)Hanoi.MAXDISK;				// 上下 30.0 づつの空間
		double yd	= yd2 - 2.0;									// 円盤の厚さ 
																// 表示の縦幅 - 円盤間の隙間
		double x0	= GAP + xd/2.0 + (GAP + xd)*p.location;			// 棒の横位置
		double y0	= h - 30.0;									// 棒の下端のｙ座標
		gg.setColor(Pole.COLOR);									// 棒の色
		box.setRect(x0-3.0, y0-(Hanoi.ndisk+1)*yd2,					// 棒の書き出し位置と
					6.0, (Hanoi.ndisk+1)*yd2);						// 棒の幅と長さ
		gg.fill(box);												// 棒の表示
		for(int i=0; i<p.num; i++){									// 棒にさされた全円盤を下から
			Disk d = p.cont[i];										// 円盤
			gg.setStroke(Disk.STROKE);							// 円盤の輪郭線
			box.setRect(x0-d.size/2.0, y0-yd,							// 円盤の位置と大きさ
						(double)d.disksize(), yd);
			gg.setColor(d.diskcolor());								// 円盤の色
			gg.fill(box);											// 円盤を描画
			gg.setColor(Color.black);								// 輪郭線の色
			gg.draw(box);										// 円盤の輪郭線を描画
			y0 -= yd2;											// 表示位置のｙ座標
		}
	}

	public void paintComponent(Graphics g) {						// ３本の軸を描画
		super.paintComponent(g);
		if(Hanoi.poles[0] != null) display(g, Hanoi.poles[0]);
		if(Hanoi.poles[1] != null) display(g, Hanoi.poles[1]);
		if(Hanoi.poles[2] != null) display(g, Hanoi.poles[2]);
	}
}

public class Hanoi extends JFrame implements Runnable{
	final static int	MAXDISK = 31;									// 円盤の数の最大
	static int			ndisk;										// 円盤の数
	static JTextField	ndiskText									// 円盤数の指定欄
						= new JTextField("10", 5);
	static Pole[]		poles = new Pole[3];							// 棒
	static ViewPanel	mainpanel;									// 棒と円盤のビュー
	static JLabel		countLabel									// 移動回数表示用
						= new JLabel("",	SwingConstants.RIGHT);
	static int			disp;										// 表示時間
	static JSlider		slider = new JSlider(0, 150, 100);					// 表示時間変更用
	static int			movecount ;									// 円盤の移動回数
	static Thread		thread;
	static volatile boolean trap;		// 一時停止フラグ（STARTボタンの押下を待つ状態）
	static volatile boolean quit;		// 中止フラグ（ネストした計算からできるだけ早く戻る）

	Hanoi(String s){
		super(s);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});

		ndiskText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit = true;								// 円盤数が入力されたら
				trap = false;								//	計算・表示を終了する。
			}
		});


		slider.setBackground(new Color(0, 200, 200));
		slider.addChangeListener(new ChangeListener() {				// 表示時間を指定するスライダ
			public void stateChanged(ChangeEvent e) {
				disp = slider.getValue();							// 動かされたら表示時間を変更
			}
		});

		final JButton gbutton = new JButton("開始");					// 円盤の移動開始を指示するボタン
		gbutton.setBackground(new Color(0, 240, 255));
		gbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trap = false;										// 押されたら trap フラグをクリア
			}
		});

		final JButton sbutton = new JButton("停止");				// 円盤の移動中断を指示するボタン
		sbutton.setBackground(new Color(0, 240, 255));
		sbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trap = true;									// 押されたら trap フラグを設定
			}
		});

		GridBagLayout gridbag = new GridBagLayout();			// SOUTH のパネルの配置
		GridBagConstraints constraints							// 配置規則
								= new GridBagConstraints();
		JPanel menupanel = new JPanel(gridbag);				// SOUTH 用のパネル
		menupanel.setBackground(new Color(0, 200, 200));	

		constraints.gridx = 0;									// パネルの左上から
		constraints.gridy = 0;
		constraints.gridwidth= 1;								// １マスずつ
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 10, 0, 0);					// 左側にスキマ１０ドット

		JLabel ndiskLabel = new JLabel("円盤数") ; 				// "Number of Disks"ラベル
		gridbag.setConstraints(ndiskLabel, constraints);
		menupanel.add(ndiskLabel);

		ndiskText.setBackground(new Color(255, 220, 220));
		ndiskText.setHorizontalAlignment(SwingConstants.CENTER); // フィールドの中心に表示
		constraints.gridy = GridBagConstraints.RELATIVE;	// "Number of Disks"ラベルの下へ
		gridbag.setConstraints(ndiskText, constraints);
		menupanel.add(ndiskText);

		JLabel glue1 = new JLabel("	");						// 空ラベル
		constraints.gridx = 1;									// １列目の上から
		constraints.gridy = 0;
		constraints.gridheight = 2;								// 縦に２マス占有
		constraints.weightx = 100.0;								// 横にひろがる
		constraints.insets = new Insets(0, 0, 0, 0);					// パネルの両端以外はゼロ
		gridbag.setConstraints(glue1, constraints);
		menupanel.add(glue1);

		constraints.gridx = 2;									// パネルの２列目の上の段
		constraints.gridy = 0;
		constraints.gridwidth = 2;								// 横に２マス占有
		constraints.gridheight = 1;								// 縦は１マス
		constraints.weightx = 0.0;								// 横にはひろがらない
		gridbag.setConstraints(slider, constraints);				// 以上はスライダの件
		menupanel.add(slider);

		JLabel sliderQuick = new JLabel("高速");				// "quick"ボタン
		constraints.anchor = GridBagConstraints.NORTHWEST;	// 左上隅に寄って
		constraints.gridx = 2;									// ２列目の
		constraints.gridy = 1;									// １行目
		constraints.gridwidth = 1;								// １マス占有
		gridbag.setConstraints(sliderQuick, constraints);
		menupanel.add(sliderQuick);

		JLabel sliderSlow	= new JLabel("低速");					// "slow"ボタン
		constraints.anchor = GridBagConstraints.NORTHEAST;	// 右上隅に寄って
		constraints.gridx = 3;									// ３列目に
		gridbag.setConstraints(sliderSlow, constraints);
		menupanel.add(sliderSlow);

		JLabel glue2 = new JLabel("	");						// 空ラベル
		constraints.anchor = GridBagConstraints.CENTER;		// 右上隅に寄るのは終り
		constraints.gridx = 4;									// ４列目の上から
		constraints.gridy = 0;
		constraints.gridheight = 2;								// 縦に２マス占有
		constraints.weightx = 50.0;								// 横に広がる（少なめ）
		gridbag.setConstraints(glue2, constraints);
		menupanel.add(glue2);

		constraints.gridx = 5;									// ５列目の上から
		constraints.gridy = 0;
		constraints.gridheight = 1;								// １マス占有
		constraints.weightx = 0.0;								// 横には広がらない
		constraints.fill = GridBagConstraints.BOTH;				// マスいっぱいに広げて配置
		gridbag.setConstraints(gbutton, constraints);				// 以上は "start"ボタンの件
		menupanel.add( gbutton );

		constraints.gridy = 1;									// １行目
		gridbag.setConstraints(sbutton, constraints);				// 以上は"stop"ボタンの件
		menupanel.add( sbutton );								//

		JLabel glue3 = new JLabel("	");						// 空ラベル
		constraints.gridx = 6;									// ６行目の上から
		constraints.gridy = 0;
		constraints.gridheight = 2;								// ２マス占有
		constraints.weightx = 100.0;								// 横に広がる欄
		constraints.fill = GridBagConstraints.NONE;				// マスに広げて配置しない
		gridbag.setConstraints(glue3, constraints);
		menupanel.add(glue3);

		JLabel countHLabel = new JLabel("移動数");				// "move count"ラベル
		constraints.gridx = 7;									// ７列目に配置
		constraints.gridy = 0;									// 一番上
		constraints.gridheight = 1;								// １マス占有
		constraints.weightx = 0.0;								// 横には広がらない
		constraints.insets = new Insets(0, 0, 0, 10);					// 右側にスキマ１０ドット
		gridbag.setConstraints(countHLabel, constraints);
		menupanel.add( countHLabel );

		constraints.gridy = GridBagConstraints.RELATIVE;		// "move count"ラベルの下へ
		gridbag.setConstraints(countLabel, constraints);			// countLabelを配置
		menupanel.add( countLabel );

		mainpanel = new ViewPanel(450, 300);					// 表示パネル

		Container cont = getContentPane();
		cont.setLayout(new BorderLayout());
		cont.add( menupanel, BorderLayout.SOUTH );
		cont.add( mainpanel, BorderLayout.CENTER );
	}

	static void shift(Pole a, Pole b, Pole c, int n){
		Disk d;
		if(quit) return;										// 中止
		if(n==0) {
			sleep(disp);										// なにもせず表示のみ
			return;
		}
		else {
			shift(a, c, b, n-1);									// a から c へ n-1 枚 shift
			if(quit) return;									// 中止
			d = a.pop();										// a から １枚取り出し
			mainpanel.repaint();								// 画面を更新
			countLabel.setText(String.valueOf(++movecount));		// 移動回数を増やして表示
			b.push(d);										// その１枚を b に挿す
			mainpanel.repaint();								// 画面を更新
			shift(c, b, a, n-1);									// c から b へ n-1 枚 shift
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
		while(true) {											// 永久ループ
			trap = true;										// STARTボタン押下まち
			quit = false;										// 計算中止しない
			movecount = 0;									// 移動回数 クリア
			countLabel.setText(String.valueOf(movecount));		// 移動回数 表示
			ndisk = Integer.parseInt(ndiskText.getText().trim());	// ディスク数取得
			if(ndisk>MAXDISK-1) {								// ディスク数の指定
				System.out.println("Max Disks: " + (MAXDISK-1));	// 大きすぎる旨
				ndisk = MAXDISK-1;								// 最大許容値に変更
				ndiskText.setText(String.valueOf(ndisk));			//	表示も更新
			}
			disp = slider.getValue();							// 表示時間を取得
			for(int i=0; i<3; i++)								// ３本の
				{ poles[i] = new Pole(i, MAXDISK); }				// ポールを作成
			for(int i=ndisk-1; i>=0; i--) {						// 全ディスクを
				poles[0].push(new Disk(10*(i+1), Color.red));		// 小さい順に作成
			}
			repaint();										// 画面更新
			sleep(0);											// 一定時間表示
			shift(poles[0], poles[1], poles[2], ndisk);				// 開始！！
			trap = true;										// これ以上計算しない
			sleep(0);											// 最終結果の表示継続
		}
	}

	static void sleep(int time) {
		while(--time>0) {										// 0.01 秒の sleep を
			try{ Thread.sleep(10); }							// time 回 繰り返す
				catch(InterruptedException e) { }				// time には disp が渡される
		}
		while(trap) {											// START ボタンが押されるまで
			try{ Thread.sleep(10); }							//	先に進まない
			catch(InterruptedException e) { }
		}
	}
}
