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
 * ������
 * 
 * @author StainGate
 * @version 1.3 2014-8-17
 */
public class FiveChesses extends JFrame implements MouseListener {
	// ��ȡ���ı���ͼƬ
	BufferedImage img = null;
	// ����x��y���������������
	int x;
	int y;
	// �жϽ����Ƿ��Ѿ���ʼ��
	boolean init = false;
	// �������ӵ���ɫ��trueΪ��ɫ��falseΪ��ɫ
	boolean chessColor = true;
	// ��¼��һ�����ӵ���ɫ���������л������
	boolean lastChessColor;
	// װ�����Ӷ���ļ���
	LinkedList<Chess> chesses = new LinkedList<Chess>();
	// ����һ����ά���鱣�����е����������
	int[][] allChess = new int[19][19];
	// ��ǰ�����������е��±�ֵ
	int indexX;
	int indexY;
	// ��¼��һ�����ӵ������±�ֵ���������л������
	int lastIndexx;
	int lastIndexy;
	// ��������
	Font font = new Font("����", Font.BOLD, 30);
	// ˫������˳����ʾ
	String tips = new String();

	// ����ڰ�˫�����Եķ��Ӻ����ӣ�����ʼ��Ϊ20��
	int minutesBlack = 0;
	int secondBlack = 20;
	int minutesWhite = 0;
	int secondWhite = 20;

	// ����ڰ�˫�����Եĵ���ʱ�߳�
	Thread blackThread = null;
	Thread whiteThread = null;

	// ��ʼ����20��
	String time = "20";
	// ˫����.....���������Ч����֤�������������ʱǶ����̫��ѭ�������Ҫ���Ǹ��󹤳̣��������˰�
	BufferedImage offScreenImage = null;
	// ˼��ʱ�䵽���������������
	Random random = new Random();
	int co = 0;

	/**
	 * ��֯����ķ�������Ҫ�������������
	 */
	public void face() {
		// ���ô���ĸ�������
		setTitle("1115-��ѧ��-������");
		setSize(500, 500);
		setResizable(false);

		try {
			// ͨ��imgIO������ͼƬ���ؽ���
			InputStream InputStream = FiveChesses.class.getClassLoader().getResourceAsStream("fivechess.jpg");
			img = ImageIO.read(InputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// ��Ӵ����������
		this.addMouseListener(this);
		// ���ô���Ĭ�Ϲر�������
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * �̳е�update����
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
	 * ��дJFrame��paint������������������Ԫ��
	 * 
	 * @param g
	 *            �õ�һ�����ʶ���
	 */
	@Override
	public void paint(Graphics g) {
		g.setFont(font);
		// ������ͼƬ�������
		g.drawImage(img, 0, 20, null);
		Color c = g.getColor();
		// �������
		for (int x = 10; x <= 370; x += 20) {
			for (int y = 70; y <= 430; y += 20) {
				g.drawLine(x, y, 360, y);
				g.drawLine(x, y, x, 420);
			}
		}

		// �����Ӽ�����ȡ��ÿ�����Ӷ���Ȼ���ڻ�����
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

		// �жϷ��Ӻ������Ƿ�С��10��С������ǰ���0��������
		if (minutesBlack < 10) {
			g.drawString("0" + String.valueOf(minutesBlack), 60, 472);
		} else {
			g.drawString(String.valueOf(minutesBlack), 60, 472);
		}
		if (secondBlack < 10) {
			g.drawString(" ��0" + String.valueOf(secondBlack), 100, 472);
		} else {
			g.drawString(" ��" + String.valueOf(secondBlack), 100, 472);
		}
		if (minutesWhite < 10) {
			g.drawString("0" + String.valueOf(minutesWhite), 280, 472);
		} else {
			g.drawString(String.valueOf(minutesWhite), 280, 472);
		}
		if (secondWhite < 10) {
			g.drawString(" ��0" + String.valueOf(secondWhite), 320, 472);
		} else {
			g.drawString(" ��" + String.valueOf(secondWhite), 320, 472);
		}

	}

	/**
	 * ����ķ����������������ӵĶ���
	 * 
	 * @param x
	 *            �õ���ʽת�����������x����
	 * @param y
	 *            �õ���ʽת�����������y����
	 */
	public void placeChess(int x, int y) {
		x = x * 20 + 10;
		y = y * 20 + 70;

		// �жϸ��ֵ���¼��ķ������꣬��������Ӧ����Ӧ
		if (x > 400 && x < 470 && y < 100 && y > 60) {
			if (JOptionPane.showConfirmDialog(this, "ȷ�����¿�ʼ��Ϸ��") == JOptionPane.OK_OPTION) {
				// ��allChess����ָ��һ���µĶ���
				for (int i = 0; i < 19; i++) {
					for (int j = 0; j < 19; j++) {
						allChess[i][j] = 0;
					}
				}
				chesses.clear();// ��װ���ӵļ������
				// ��˫���߳�ֹͣ������ֵ
				if (blackThread != null) {
					blackThread.stop();
					blackThread = null;
				} else if (whiteThread != null) {
					whiteThread.stop();
					whiteThread = null;
				}
				// ���³�ʼ�����Ӻ�����
				customerInit();

			}
		} else if (x > 400 && x < 470 && y > 112 && y < 150) {
			time = JOptionPane.showInputDialog(this, "��������Ϸʱ�䣨�룩");
			// ���ݸ�����ʱ���ʼ��ʱ��
			customerInit();
		} else if (x > 400 && x < 470 && y > 360 && y < 400) {
			// ����˳����˳�ϵͳ
			System.exit(0);

		} else if (x > 400 && x < 470 && y > 320 && y < 350) {
			// �������л������
			backward();
		} else if (x > 400 && x < 470 && y > 270 && y < 300) {
			// ���������
			if (JOptionPane.showConfirmDialog(this, "ȷ�����䣿��") == JOptionPane.OK_OPTION) {
				if (chessColor == true) {
					JOptionPane.showMessageDialog(null, "�ڷ����䣡��");
				} else if (chessColor == false) {
					JOptionPane.showMessageDialog(null, "�׷����䣡��");
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
			// ��������������ʱ������ʾ��
			JOptionPane.showMessageDialog(this, "���������ڷ������ӣ�");

		} else {
			// System.out.println(indexX + "  " + indexY);
			if (allChess[indexX][indexY] == 1 || allChess[indexX][indexY] == 2) {
				// ����ͬһλ������ʱ��������ʾ��
				JOptionPane.showMessageDialog(this, "�벻Ҫ��ͬһλ���ظ����壡��");
			} else if (chessColor == true) {// �жϴ������ķ�������ȷ��������ɫ
				lastChessColor = chessColor;
				playBlack(indexX, indexY, x, y);

			} else if (chessColor == false) {
				lastChessColor = chessColor;
				playWhite(indexX, indexY, x, y);

			}

		}
		// �ػ���������

		repaint();
	}

	
	/**
	 * �º���ķ���
	 * @param indexX ��ά�����x�±�ֵ
	 * @param indexY ��ά�����y�±�ֵ
	 * @param x �滭���ӵ�x����
	 * @param y �滭���ӵ�y����
	 */
	public void playBlack(int indexX, int indexY, int x, int y) {
		allChess[indexX][indexY] = 1;
		// ��¼��һ�����ӵ��±�ֵ
		lastIndexx = indexX;
		lastIndexy = indexY;
		chesses.add(new Chess(x, y, true));
		// judegmentOfWinBlack();

		chessColor = false;// ��������ɫ����Ϊfalse����ʵ�ֽ�������
		openThread();
		co = 0;
		boolean win1 = win4x(indexX, indexY);
		// System.out.println(win1);
		boolean win2 = win4y(indexX, indexY);
		boolean win3 = win4xy(indexX, indexY);
		boolean win4 = win4yx(indexX, indexY);
		if (win1 || win2 || win3 || win4) {
			tips = "�ڷ�ʤ����";
			// repaint();
			JOptionPane.showMessageDialog(null, "�ڷ�ʤ����");

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
	 * �°���ķ���
	 * @param indexX ��ά�����x�±�ֵ
	 * @param indexY ��ά�����y�±�ֵ
	 * @param x �滭���ӵ�x����
	 * @param y �滭���ӵ�y����
	 */
	public void playWhite(int indexX, int indexY, int x, int y) {
		allChess[indexX][indexY] = 2;
		// ��¼��һ�����ӵ��±�ֵ
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
			tips = "�׷�ʤ����";
			// repaint();
			JOptionPane.showMessageDialog(null, "�׷�ʤ����");

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
	 * ��������ʱ�̷߳���
	 */
	public void openThread() {
		if (chessColor == true) {
			tips = "�ڷ�����";
			blackThread = new Thread(new countDown());// ��ʼ���ڷ��߳�
			blackThread.start();// �ڷ��߳�����
			customerInit();
			if (whiteThread != null) {
				// ���׷��߳�ֹͣ
				whiteThread.stop();
				// ����ʼ���׷��ĵ���ʱʱ��
				customerInit();
			}
		} else if (chessColor == false) {
			tips = "�׷�����";
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
	 * ���巽��
	 */
	public void backward() {
		if(co == 0) {
			// ����һ�������������е�ֵ��Ϊ0����������һ������
			allChess[lastIndexx][lastIndexy] = 0;
			// ����һ�����ӴӶ��е�ĩβ�Ƴ�
			chesses.removeLast();
			// ��������ɫ��������Ϊ��һ�����ӵ���ɫ
			chessColor = lastChessColor;
			openThread();
			co ++;
		} 
		
		

	}

	/**
	 * ��ʼ��ʱ��ķ������ر��������Ӵ��븴����
	 */
	public void customerInit() {
		minutesBlack = Integer.valueOf(time) / 60;
		secondBlack = Integer.valueOf(time) % 60;
		minutesWhite = Integer.valueOf(time) / 60;
		secondWhite = Integer.valueOf(time) % 60;
	}

	/**
	 * ���x�᷽������Ӯ�ķ���
	 * @param indexx ��ά�����x�±�ֵ
	 * @param indexy ��ά�����y�±�ֵ
	 * @return Ӯ�˷���true�����򷵻�false
	 */
	public boolean win4x(int indexx, int indexy) {
		// ��ͬ���Ӽ�����
		int countChess = 1;
		int right = 1;// �����ұߵļ�����
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
	 * ���y�᷽������Ӯ�ķ���
	 * @param indexx ��ά�����x�±�ֵ
	 * @param indexy ��ά�����y�±�ֵ
	 * @return Ӯ�˷���true�����򷵻�false
	 */
	public boolean win4y(int indexx, int indexy) {
		// ��ͬ���Ӽ�����
		int countChess = 1;
		int up = 1;// �����ұߵļ�����
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
	 * ������������Ϸ�������Ӯ�ķ���
	 * @param indexx ��ά�����x�±�ֵ
	 * @param indexy ��ά�����y�±�ֵ
	 * @return Ӯ�˷���true�����򷵻�false
	 */
	public boolean win4xy(int indexx, int indexy) {
		// System.out.println("I hava be called!");
		// ��ͬ���Ӽ�����
		int countChess = 1;
		int up = 1;// �����ұߵļ�����
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
	 * ������������·�������Ӯ�ķ���
	 * @param indexx ��ά�����x�±�ֵ
	 * @param indexy ��ά�����y�±�ֵ
	 * @return Ӯ�˷���true�����򷵻�false
	 */
	public boolean win4yx(int indexx, int indexy) {
		// ��ͬ���Ӽ�����
		int countChess = 1;
		int up = 1;// �����ұߵļ�����
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
	 * �ڲ��࣬��������ʱ��ʵ��Runable�ӿ�
	 * 
	 * @author StainGate
	 * @version 1.2 2017-8-16
	 * 
	 */
	public class countDown implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// ���ڷ�����ʱ���߳�ÿ˯��һ��͵���ʱһ��
			if (chessColor == true) {
				while (true) {
					try {
						blackThread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// �߳���������úڷ��ĵ���ʱ����
					countBlack();
				}
			} else if (chessColor == false) {// ���׷�����ʱ���߳�ÿ˯��һ��͵���ʱһ��
				while (true) {
					try {
						whiteThread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// ���ð׷�����ʱ����
					countWhite();
				}
			}

		}

		
		/**
		 * �ڷ��߳��ڵĵ���ʱ����
		 */
		public synchronized void countBlack() {
			// System.out.println(secondBlack);
			if (secondBlack <= 0) {
				minutesBlack--;
				secondBlack = 60;
			}
			secondBlack--;

			// ��ʱ�䵽0ʱ����ڷ�Υ�����򣬰׷�ʤ���˳�ϵͳ
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
		 * �׷��߳��ڵĵ���ʱ����
		 */
		public synchronized void countWhite() {
			if (secondWhite <= 0) {
				minutesWhite--;
				secondWhite = 60;
			}
			secondWhite--;

			// ��ʱ�䵽0ʱ����׷�Υ�����򣬺ڷ�ʤ���˳�ϵͳ
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
