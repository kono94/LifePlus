package lifeplus;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class StoneComponent extends Component {
	private Color m_myColor;
	private boolean m_StaticStone;
	private boolean m_drawOvals;

	public StoneComponent() {
		m_myColor = Color.WHITE;
		m_StaticStone = false;
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {

		if (!m_StaticStone) {
			g.setColor(m_myColor);
			if (!m_drawOvals)
				g.fillRect(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
			else
				g.fillOval(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
		} else {
			if (!m_drawOvals) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
				g.setColor(m_myColor);
				g.fillRect(2, 2, (int) this.getSize().getWidth() - 4, (int) this.getSize().getHeight() - 4);
			} else {
				g.setColor(Color.BLACK);
				g.fillOval(0, 0, (int) this.getSize().getWidth(), (int) this.getSize().getHeight());
				g.setColor(m_myColor);
				g.fillOval(2, 2, (int) this.getSize().getWidth() - 4, (int) this.getSize().getHeight() - 4);
			}
		}

	}

	public void setOvals(boolean b) {
		m_drawOvals = b;
	}

	public void setColor(Color c) {
		m_myColor = c;
	}

	public void setStatic() {
		m_StaticStone = true;
	}
}
