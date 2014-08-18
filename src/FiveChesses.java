import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * 五子棋
 * 
 * @author StainGate
 * @version 1.3 2014-8-17
 */
public class FiveChesses extends JFrame implements MouseListener {
	// 读取到的背景图片
	BufferedImage img = null;
	// 定义x，y接受鼠标点击的坐标
	int x;
	int y;
	// 判断界面是否已经初始化
	boolean init = false;
	// 控制棋子的颜色，true为黑色，false为白色
	boolean chessColor = true;
	// 记录上一个棋子的颜色，用来进行悔棋操作
	boolean lastChessColor;
	// 装载棋子对象的集合
	LinkedList<Chess> chesses = new LinkedList<Chess>();
	// 定义一个二维数组保存所有的棋盘坐标点
	int[][] allChess = new int[19][19];
	// 当前坐标在数组中的下标值
	int indexX;
	int indexY;
	// 记录上一个棋子的数组下标值，用来进行悔棋操作
	int lastIndexx;
	int lastIndexy;
	// 设置字体
	Font font = new Font("粗体", Font.BOLD, 30);
	// 双方下棋顺序提示
	String tips = new String();

	// 定义黑白双方各自的分钟和秒钟，并初始化为20秒
	int minutesBlack = 0;
	int secondBlack = 20;
	int minutesWhite = 0;
	int secondWhite = 20;

	// 定义黑白双方各自的倒计时线程
	Thread blackThread = null;
	Thread whiteThread = null;

	// 初始化的20秒
	String time = "20";
	// 双缓冲.....结果用了无效，验证后发现是输出棋子时嵌套了太多循环，如果要改是个大工程，还是算了吧
	BufferedImage offScreenImage = null;
	// 思考时间到随机下棋的随机对象
	Random random = new Random();
	int co = 0;

	/**
	 * 组织界面的方法，主要用来描绘各种组件
	 */
	public void face() {
		// 设置窗体的各种属性
		setTitle("1115-罗学林-五子棋");
		setSize(500, 500);
		setResizable(false);

		try {
			// 通过imgIO将背景图片加载进来
			InputStream InputStream = FiveChesses.class.getClassLoader().getResourceAsStream("fivechess.jpg");
			img = ImageIO.read(InputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 添加窗体的鼠标监听
		this.addMouseListener(this);
		// 设置窗口默认关闭主窗体
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * 继承的update方法
	 */
	public void update(Graphics g) {
		if (offScreenImage == null) {
			try {
				offScreenImage = ImageIO.read(new File("img//fivechess.jpg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		g.drawImage(offScreenImage, 0, 20, null);
	}

	/**
	 * 重写JFrame的paint方法，用来画出各种元素
	 * 
	 * @param g
	 *            得到一个画笔对象
	 */
	@Override
	public void paint(Graphics g) {
		g.setFont(font);
		// 将背景图片画入界面
		g.drawImage(img, 0, 20, null);
		Color c = g.getColor();
		// 描绘棋盘
		for (int x = 10; x <= 370; x += 20) {
			for (int y = 70; y <= 430; y += 20) {
				g.drawLine(x, y, 360, y);
				g.drawLine(x, y, x, 420);
			}
		}

		// 迭代从集合呢取出每个棋子对象，然后画在画面上
		for (Chess chess : chesses) {
			for (int i = 0; i < 19; i++) {
				for (int j = 0; j < 19; j++) {
					if (allChess[i][j] == 1) {
						chess.paint(g);
						// g.setColor(c);
						g.drawString(tips, 200, 55);
					} else if (allChess[i][j] == 2) {
						chess.paint(g);
						g.drawString(tips, 200, 55);
					}
				}
			}
		}

		// 判断分钟和秒钟是否小于10，小于则在前面加0，并画出
		if (minutesBlack < 10) {
			g.drawString("0" + String.valueOf(minutesBlack), 60, 472);
		} else {
			g.drawString(String.valueOf(minutesBlack), 60, 472);
		}
		if (secondBlack < 10) {
			g.drawString(" ：0" + String.valueOf(secondBlack), 100, 472);
		} else {
			g.drawString(" ：" + String.valueOf(secondBlack), 100, 472);
		}
		if (minutesWhite < 10) {
			g.drawString("0" + String.valueOf(minutesWhite), 280, 472);
		} else {
			g.drawString(String.valueOf(minutesWhite), 280, 472);
		}
		if (secondWhite < 10) {
			g.drawString(" ：0" + String.valueOf(secondWhite), 320, 472);
		} else {
			g.drawString(" ：" + String.valueOf(secondWhite), 320, 472);
		}

	}

	/**
	 * 下棋的方法，用来创建棋子的对象
	 * 
	 * @param x
	 *            得到格式转换后鼠标点击的x坐标
	 * @param y
	 *            得到格式转换后鼠标点击的y坐标
	 */
	public void placeChess(int x, int y) {
		x = x * 20 + 10;
		y = y * 20 + 70;

		// 判断各种点击事件的发生坐标，并作出相应的响应
		if (x > 400 && x < 470 && y < 100 && y > 60) {
			if (JOptionPane.showConfirmDialog(this, "确认重新开始游戏？") == JOptionPane.OK_OPTION) {
				// 将allChess对象指向一个新的对象
				for (int i = 0; i < 19; i++) {
					for (int j = 0; j < 19; j++) {
						allChess[i][j] = 0;
					}
				}
				chesses.clear();// 将装棋子的集合清空
				// 将双方线程停止并赋空值
				if (blackThread != null) {
					blackThread.stop();
					blackThread = null;
				} else if (whiteThread != null) {
					whiteThread.stop();
					whiteThread = null;
				}
				// 重新初始化分钟和秒钟
				customerInit();

			}
		} else if (x > 400 && x < 470 && y > 112 && y < 150) {
			time = JOptionPane.showInputDialog(this, "请设置游戏时间（秒）");
			// 依据给出的时间初始化时间
			customerInit();
		} else if (x > 400 && x < 470 && y > 360 && y < 400) {
			// 点击退出则退出系统
			System.exit(0);

		} else if (x > 400 && x < 470 && y > 320 && y < 350) {
			// 点击则进行悔棋操作
			backward();
		} else if (x > 400 && x < 470 && y > 270 && y < 300) {
			// 点击则认输
			if (JOptionPane.showConfirmDialog(this, "确认认输？？") == JOptionPane.OK_OPTION) {
				if (chessColor == true) {
					JOptionPane.showMessageDialog(null, "黑方认输！！");
				} else if (chessColor == false) {
					JOptionPane.showMessageDialog(null, "白方认输！！");
				}
				for (int i = 0; i < 19; i++) {
					for (int j = 0; j < 19; j++) {
						allChess[i][j] = 0;
					}
				}
				chesses.clear();
				blackThread.stop();
				blackThread = null;
				whiteThread.stop();
				whiteThread = null;
				customerInit();
			}

		} else if (x < 10 || x > 370 || y < 70 || y > 430) {
			// 当在棋盘外下棋时弹出提示框
			JOptionPane.showMessageDialog(this, "请在棋盘内放置棋子！");

		} else {
			// System.out.println(indexX + "  " + indexY);
			if (allChess[indexX][indexY] == 1 || allChess[indexX][indexY] == 2) {
				// 当在同一位置下棋时弹出此提示框
				JOptionPane.showMessageDialog(this, "请不要在同一位置重复下棋！！");
			} else if (chessColor == true) {// 判断此轮由哪方下棋以确定棋子颜色
				lastChessColor = chessColor;
				playBlack(indexX, indexY, x, y);

			} else if (chessColor == false) {
				lastChessColor = chessColor;
				playWhite(indexX, indexY, x, y);

			}

		}
		// 重画整个界面

		repaint();
	}

	
	/**
	 * 下黑棋的方法
	 * @param indexX 二维数组的x下标值
	 * @param indexY 二维数组的y下标值
	 * @param x 绘画棋子的x坐标
	 * @param y 绘画棋子的y坐标
	 */
	public void playBlack(int indexX, int indexY, int x, int y) {
		allChess[indexX][indexY] = 1;
		// 记录上一个棋子的下标值
		lastIndexx = indexX;
		lastIndexy = indexY;
		chesses.add(new Chess(x, y, true));
		// judegmentOfWinBlack();

		chessColor = false;// 将棋子颜色设置为false，以实现交替下棋
		openThread();
		co = 0;
		boolean win1 = win4x(indexX, indexY);
		// System.out.println(win1);
		boolean win2 = win4y(indexX, indexY);
		boolean win3 = win4xy(indexX, indexY);
		boolean win4 = win4yx(indexX, indexY);
		if (win1 || win2 || win3 || win4) {
			tips = "黑方胜！！";
			// repaint();
			JOptionPane.showMessageDialog(null, "黑方胜！！");

			for (int i = 0; i < 19; i++) {
				for (int j = 0; j < 19; j++) {
					allChess[i][j] = 0;
				}
			}
			chesses.clear();
			blackThread.stop();
			blackThread = null;
			whiteThread.stop();
			whiteThread = null;
			customerInit();
		}
	}

	
	/**
	 * 下白棋的方法
	 * @param indexX 二维数组的x下标值
	 * @param indexY 二维数组的y下标值
	 * @param x 绘画棋子的x坐标
	 * @param y 绘画棋子的y坐标
	 */
	public void playWhite(int indexX, int indexY, int x, int y) {
		allChess[indexX][indexY] = 2;
		// 记录上一个棋子的下标值
		lastIndexx = indexX;
		lastIndexy = indexY;
		chesses.add(new Chess(x, y, false));
		// judgementOfWin();

		chessColor = true;
		openThread();
		co = 0;
		boolean win1 = win4x(indexX, indexY);
		// System.out.println(win1);
		boolean win2 = win4y(indexX, indexY);
		boolean win3 = win4xy(indexX, indexY);
		boolean win4 = win4yx(indexX, indexY);
		if (win1 || win2 || win3 || win4) {
			tips = "白方胜！！";
			// repaint();
			JOptionPane.showMessageDialog(null, "白方胜！！");

			for (int i = 0; i < 19; i++) {
				for (int j = 0; j < 19; j++) {
					allChess[i][j] = 0;
				}
			}
			chesses.clear();
			blackThread.stop();
			blackThread = null;
			whiteThread.stop();
			whiteThread = null;
			customerInit();
		}
	}

	/**
	 * 开启倒计时线程方法
	 */
	public void openThread() {
		if (chessColor == true) {
			tips = "黑方下子";
			blackThread = new Thread(new countDown());// 初始化黑方线程
			blackThread.start();// 黑方线程启动
			customerInit();
			if (whiteThread != null) {
				// 将白方线程停止
				whiteThread.stop();
				// 并初始化白方的倒计时时间
				customerInit();
			}
		} else if (chessColor == false) {
			tips = "白方下子";
			whiteThread = new Thread(new countDown());
			whiteThread.start();
			customerInit();
			if (blackThread != null) {
				blackThread.stop();
				customerInit();
			}
		}
	}

	/**
	 * 悔棋方法
	 */
	public void backward() {
		if(co == 0) {
			// 将上一个棋子在数组中的值赋为0，即撤销上一步操作
			allChess[lastIndexx][lastIndexy] = 0;
			// 将上一个棋子从队列的末尾移除
			chesses.removeLast();
			// 将棋子颜色重新设置为上一个棋子的颜色
			chessColor = lastChessColor;
			openThread();
			co ++;
		} 
		
		

	}

	/**
	 * 初始化时间的方法，特别抽出以增加代码复用性
	 */
	public void customerInit() {
		minutesBlack = Integer.valueOf(time) / 60;
		secondBlack = Integer.valueOf(time) % 60;
		minutesWhite = Integer.valueOf(time) / 60;
		secondWhite = Integer.valueOf(time) % 60;
	}

	/**
	 * 检查x轴方向上输赢的方法
	 * @param indexx 二维数组的x下标值
	 * @param indexy 二维数组的y下标值
	 * @return 赢了返回true，否则返回false
	 */
	public boolean win4x(int indexx, int indexy) {
		// 相同棋子计数器
		int countChess = 1;
		int right = 1;// 控制右边的计数器
		int left = 1;
		if (indexx != 0) {
			while (indexx - left > -1
					&& allChess[indexx][indexy] == allChess[indexx - left][indexy]) {
				left++;
				countChess++;
			}
		}
		if (indexx != 18) {
			while (indexx + right <= 18
					&& allChess[indexx][indexy] == allChess[indexx + right][indexy]) {
				right++;
				countChess++;
			}
		}

		if (countChess == 5) {
			return true;
		}

		return false;
	}

	
	/**
	 * 检查y轴方向上输赢的方法
	 * @param indexx 二维数组的x下标值
	 * @param indexy 二维数组的y下标值
	 * @return 赢了返回true，否则返回false
	 */
	public boolean win4y(int indexx, int indexy) {
		// 相同棋子计数器
		int countChess = 1;
		int up = 1;// 控制右边的计数器
		int down = 1;
		if (indexy != 18) {
			while (indexy + down <= 18
					&& allChess[indexx][indexy] == allChess[indexx][indexy
							+ down]) {
				down++;
				countChess++;

			}
		}
		if (indexy != 0) {
			while (indexy - up > -1
					&& allChess[indexx][indexy] == allChess[indexx][indexy - up]) {
				up++;
				countChess++;

			}
		}

		if (countChess == 5) {
			return true;
		}

		return false;
	}

	
	/***
	 * 检查左下至右上方向上输赢的方法
	 * @param indexx 二维数组的x下标值
	 * @param indexy 二维数组的y下标值
	 * @return 赢了返回true，否则返回false
	 */
	public boolean win4xy(int indexx, int indexy) {
		// System.out.println("I hava be called!");
		// 相同棋子计数器
		int countChess = 1;
		int up = 1;// 控制右边的计数器
		int down = 1;
		if (indexx != 18 && indexy != 0) {
			while (indexx + down <= 18
					&& indexy - down > -1
					&& allChess[indexx][indexy] == allChess[indexx + down][indexy
							- down]) {
				down++;
				countChess++;

			}
		}
		if (indexx != 0 && indexy != 18) {
			while (indexx - up > -1
					&& indexy + up <= 18
					&& allChess[indexx][indexy] == allChess[indexx - up][indexy
							+ up]) {
				up++;
				countChess++;

			}
		}

		if (countChess == 5) {
			return true;
		}

		return false;
	}

	
	/**
	 * 检查左上至右下方向上输赢的方法
	 * @param indexx 二维数组的x下标值
	 * @param indexy 二维数组的y下标值
	 * @return 赢了返回true，否则返回false
	 */
	public boolean win4yx(int indexx, int indexy) {
		// 相同棋子计数器
		int countChess = 1;
		int up = 1;// 控制右边的计数器
		int down = 1;
		if (indexx != 18 && indexy != 18) {
			while (indexx + down <= 18
					&& indexy + down <= 18
					&& allChess[indexx][indexy] == allChess[indexx + down][indexy
							+ down]) {
				down++;
				countChess++;

			}
		}
		if (indexx != 0 && indexy != 0) {
			while (indexx - up > -1
					&& indexy - up > -1
					&& allChess[indexx][indexy] == allChess[indexx - up][indexy
							- up]) {
				up++;
				countChess++;

			}
		}

		if (countChess == 5) {
			return true;
		}

		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		x = e.getX();
		y = e.getY();
		// System.out.println(x + ">>>" + y);
		indexX = Math.round((x - 10) / 20.0f);
		indexY = Math.round((y - 70) / 20.0f);

		this.placeChess(indexX, indexY);
		// System.out.println(indexX);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * 内部类，用来倒计时，实现Runable接口
	 * 
	 * @author StainGate
	 * @version 1.2 2017-8-16
	 * 
	 */
	public class countDown implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 当黑方下棋时，线程每睡眠一秒就倒计时一秒
			if (chessColor == true) {
				while (true) {
					try {
						blackThread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 线程醒来后调用黑方的倒计时方法
					countBlack();
				}
			} else if (chessColor == false) {// 当白方下棋时，线程每睡眠一秒就倒计时一秒
				while (true) {
					try {
						whiteThread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 调用白方倒计时方法
					countWhite();
				}
			}

		}

		
		/**
		 * 黑方线程内的倒计时方法
		 */
		public synchronized void countBlack() {
			// System.out.println(secondBlack);
			if (secondBlack <= 0) {
				minutesBlack--;
				secondBlack = 60;
			}
			secondBlack--;

			// 当时间到0时，则黑方违反规则，白方胜，退出系统
			if (secondBlack == 0 && minutesBlack == 0) {
				int xx = random.nextInt(18);
				int yy = random.nextInt(18);
				while (allChess[xx][yy] == 0) {
					playBlack(xx, yy, xx * 20 + 10, yy * 20 + 70);
				}
			}
			repaint();
		}

		
		/**
		 * 白方线程内的倒计时方法
		 */
		public synchronized void countWhite() {
			if (secondWhite <= 0) {
				minutesWhite--;
				secondWhite = 60;
			}
			secondWhite--;

			// 当时间到0时，则白方违反规则，黑方胜，退出系统
			if (secondWhite == 0 && minutesWhite == 0) {
				int xx = random.nextInt(18);
				int yy = random.nextInt(18);
				while (allChess[xx][yy] == 0) {
					playWhite(xx, yy, xx * 20 + 10, yy * 20 + 70);
				}
			}
			repaint();

		}

	}

}
