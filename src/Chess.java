import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;


/**
 * ������
 * @author StainGate
 *
 */
public class Chess extends JFrame{
	int x;
	int y;
	//���������Ǻ��ǰ�,trueΪ��ɫ��falseΪ��ɫ
	boolean chessColor = true;
	
	/**
	 * ���췽��
	 * @param x ���ӵĺ�����
	 * @param y ���ӵ�������
	 * @param chessColor ���ӵ���ɫ
	 */
	public Chess(int x, int y, boolean chessColor) {
		this.x = x;
		this.y = y;
		this.chessColor = chessColor;
	}

	/**
	 * ���ӵ�paint����������������������
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
