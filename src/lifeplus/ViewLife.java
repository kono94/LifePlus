package lifeplus;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

@SuppressWarnings("serial")
public class ViewLife extends Frame {
	ModelLife m_model;
	StoneComponent[][] stoneComponents;
	Image m_Img; // Double Buffering
	Image m_ImgBuffer; // -II-
	Image m_loadingTextImage;
	int m_iWidth = 0;
	int m_iMaxWidth = 0;

	public ViewLife(ModelLife model) {
		m_model = model;
		stoneComponents = new StoneComponent[m_model.getRowCount()][m_model.getColumnCount()];

		new Window(this) {
			{
				setSize(700, 500);

				m_loadingTextImage = getToolkit().createImage(getClass().getResource("/loadingText.png"));
				MediaTracker mt = new MediaTracker(this);
				mt.addImage(m_loadingTextImage, 1);
				try {
					mt.waitForAll();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setVisible(true);
				final Insets INS = getInsets();
				if (m_iMaxWidth == 0)
					m_iMaxWidth = getWidth() - INS.left - INS.right - m_loadingTextImage.getWidth(this);
				setLocationRelativeTo(null); // eike
				// animateLoadingText();
				dispose();
			}

			public void update(Graphics g) {
				if (m_ImgBuffer == null) {
					m_ImgBuffer = createImage(getWidth(), getHeight());
				}
				Graphics bufferGraphics = m_ImgBuffer.getGraphics();
				bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
				paint(bufferGraphics);
				g.drawImage(m_ImgBuffer, 0, 0, this);
			}

			@Override
			public void paint(Graphics g) {
				final Insets INS = getInsets();
				g.drawImage(m_loadingTextImage, INS.left + m_iWidth, (getHeight() - INS.top - INS.bottom) / 2 - 50,
						this);

			}

			public void animateLoadingText() {
				int msCounter = 0;
				while (msCounter < 1000) {
					for (m_iWidth = 0; m_iWidth < m_iMaxWidth; ++m_iWidth) {
						try {
							Thread.sleep(4);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msCounter += 2;
						repaint();
					}
					for (m_iWidth = m_iMaxWidth; m_iWidth > 0; --m_iWidth) {
						try {
							Thread.sleep(4);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msCounter += 2;
						repaint();
					}

				}
				dispose();
			}

		};

		setLayout(new BorderLayout()); // Top Layout Manager
		Panel btmPanel = new Panel();
		btmPanel.setLayout(new FlowLayout()); // Flow layout for the buttons
												// below the stonefield
		Panel rPanel;
		rPanel = new Panel(new GridLayout(3, 1));
		btmPanel.add(new Button("Start"));
		btmPanel.add(new Button("Pause"));
		btmPanel.add(new Button("Restart"));
		rPanel.add(new Button("ok"));
		add(BorderLayout.SOUTH, btmPanel);
		add(BorderLayout.EAST, rPanel);

		Panel fieldPanel = new Panel();
		fieldPanel.setLayout(new GridLayout(m_model.getRowCount(), m_model.getColumnCount(), 3, 3));
		for (int i = 0; i < stoneComponents.length; ++i) {
			for (int j = 0; j < stoneComponents[i].length; j++) {
				stoneComponents[i][j] = new StoneComponent();
				determineStaticFields(i, j);
				fieldPanel.add(stoneComponents[i][j]);
			}
		}
		add(BorderLayout.CENTER, fieldPanel);
		pack();
		setSize(1200, 800);
		setLocationRelativeTo(null); // eike
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		setVisible(true);
	}

	@Override
	public void update(Graphics g) {
		if (m_ImgBuffer == null) {
			m_ImgBuffer = createImage(getWidth(), getHeight());
		}
		Graphics bufferGraphics = m_ImgBuffer.getGraphics();
		bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
		paint(bufferGraphics);
		g.drawImage(m_ImgBuffer, 0, 0, this);
	}

	@Override
	public void paint(Graphics g) {
		for (int i = 0; i < stoneComponents.length; ++i) {
			for (int j = 0; j < stoneComponents[i].length; j++) {
				stoneComponents[i][j].setColor(determineColor(m_model.getStone(i, j)));
				stoneComponents[i][j].update(stoneComponents[i][j].getGraphics());
			}
		}
	}

	private Color determineColor(Stone stone) {
		if (stone instanceof AlwaysStone)
			return Color.GREEN;
		if (stone instanceof NeverStone)
			return Color.WHITE;
		int roundsAlive = stone.getRoundsAlive();
		if (roundsAlive == -1)
			return Color.WHITE;
		if (roundsAlive == 0)
			return Color.MAGENTA;
		if (roundsAlive == 1)
			return Color.ORANGE;
		if (roundsAlive == 2)
			return Color.YELLOW;
		if (roundsAlive == 3)
			return Color.GREEN;
		return Color.GREEN;
//		int roundsAlive = stone.getRoundsAlive();
//		if (roundsAlive == -1)
//			return Color.WHITE;
//		else
//			return Color.BLACK;
	}

	private void determineStaticFields(int i, int j) {
		if (m_model.getStone(i, j) instanceof AlwaysStone || m_model.getStone(i, j) instanceof NeverStone) {
			
				stoneComponents[i][j].setStatic();
		}

	}

}
