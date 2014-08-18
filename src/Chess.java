import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;


/**
 * 棋子类
 * @author StainGate
 *
 */
public class Chess extends JFrame{
	int x;
	int y;
	//决定棋子是黑是白,true为黑色，false为白色
	boolean chessColor = true;
	
	/**
	 * 构造方法
	 * @param x 棋子的横坐标
	 * @param y 棋子的纵坐标
	 * @param chessColor 棋子的颜色
	 */
	public Chess(int x, int y, boolean chessColor) {
		this.x = x;
		this.y = y;
		this.chessColor = chessColor;
	}

	/**
	 * 棋子的paint（）方法，用来画出棋子
	 */
	public void paint(Graphics g) {
		Color c = g.getColor();
		if(chessColor == true) {
			g.setColor(Color.BLACK);
		} else if(chessColor ==false) {
			g.setColor(Color.WHITE);
		}
		
		g.fillOval(x - 10, y - 10, 20, 20);
		g.setColor(c);
	}
}
