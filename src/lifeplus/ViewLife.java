package lifeplus;

import java.awt.*;
import java.awt.event.*;
import java.util.Set;

import javax.swing.JSlider;

@SuppressWarnings("serial")
public class ViewLife extends Frame {
	private ModelLife m_model;
	private ControllerLife m_Controller;
	private StoneComponent[][] stoneComponents;
	private Image m_Img; // Double Buffering
	private Image m_ImgBuffer; // -II-
	private Image m_loadingTextImage;
	private int m_iWidth = 0;
	private int m_iMaxWidth = 0;

	// ;? crazy? Buttons as fields to change Labels (pause->continue)
	Button m_pausBtn;
	Button m_startBtn;

	public void setController(ControllerLife c) {
		m_Controller = c;
	}

	public ViewLife(ModelLife model) {
		new ButtonFrame();
		m_model = model;
		stoneComponents = new StoneComponent[m_model.getRowCount()][m_model.getColumnCount()];

		loadingScreenRoutine();
		createMenuBar();
		layoutRoutine();
		applyListeners();
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
		// if (stone instanceof AlwaysStone)
		// return Color.GREEN;
		// if (stone instanceof NeverStone)
		// return Color.WHITE;
		// int roundsAlive = stone.getRoundsAlive();
		// if (roundsAlive == -1)
		// return Color.WHITE;
		// if (roundsAlive == 0)
		// return Color.MAGENTA;
		// if (roundsAlive == 1)
		// return Color.ORANGE;
		// if (roundsAlive == 2)
		// return Color.YELLOW;
		// if (roundsAlive == 3)
		// return Color.GREEN;
		// return Color.GREEN;
		int roundsAlive = stone.getRoundsAlive();
		if (roundsAlive == -1)
			return Color.WHITE;
		else
			return Color.BLACK;
	}

	private void determineStaticFields(int i, int j) {
		if (m_model.getStone(i, j) instanceof AlwaysStone || m_model.getStone(i, j) instanceof NeverStone) {

			stoneComponents[i][j].setStatic();
		}

	}

	private void createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu loadMenu = new Menu("Presets");
		MenuItem newItem = new MenuItem("New");
		MenuItem saveItem = new MenuItem("Save");
		MenuItem loadItem = new MenuItem("Load");
		MenuItem quitItem = new MenuItem("Quit");
		newItem.addActionListener(e -> {
			m_model.resetField();
		});
		saveItem.addActionListener(e -> {

		});
		loadItem.addActionListener(e -> {

		});
		quitItem.addActionListener(e -> {
			dispose();
			System.exit(0);
		});
		fileMenu.add(newItem);
		fileMenu.add(saveItem);
		fileMenu.add(loadItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		MenuItem gunItem = new MenuItem("Gun");
		MenuItem specialItem = new MenuItem("Speciale");
		gunItem.addActionListener(e -> {

		});
		specialItem.addActionListener(e -> {

		});
		loadMenu.add(gunItem);
		loadMenu.add(specialItem);
		menuBar.add(fileMenu);
		menuBar.add(loadMenu);
		setMenuBar(menuBar);
	}

	private void layoutRoutine() {
		setLayout(new BorderLayout()); // Top Layout Manager
		Panel btmPanel = new Panel();
		btmPanel.setLayout(new FlowLayout()); // Flow layout for the buttons
												// below the stonefield
		Panel rPanel;
		rPanel = new Panel(new GridLayout(3, 1));
		m_startBtn = new Button("start");
		btmPanel.add(m_startBtn);
		// ;in
		m_pausBtn = new Button("  pause  ");
		btmPanel.add(m_pausBtn);
		Button restartBtn = new Button("restart");
		btmPanel.add(restartBtn);

		JSlider slider = new JSlider(0, 2000, 1000);
		slider.setMajorTickSpacing(500);
		slider.createStandardLabels(500);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);

		// ;? View changes static variable in controller
		slider.addChangeListener(e -> {
			m_Controller.setSleepTime(((JSlider) e.getSource()).getValue());
		});
		slider.setPreferredSize(new Dimension(500, 80));
		btmPanel.add(slider);
		rPanel.add(new Button("ok"));
		add(BorderLayout.SOUTH, btmPanel);
		add(BorderLayout.EAST, rPanel);

		Panel fieldPanel = new Panel();
		fieldPanel.setLayout(new GridLayout(m_model.getRowCount(), m_model.getColumnCount(), 1, 1));
		for (int i = 0; i < stoneComponents.length; ++i) {
			for (int j = 0; j < stoneComponents[i].length; j++) {
				stoneComponents[i][j] = new StoneComponent();
				determineStaticFields(i, j);
				fieldPanel.add(stoneComponents[i][j]);
			}
		}
		add(BorderLayout.CENTER, fieldPanel);
	}

	private void loadingScreenRoutine() {
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
	}

	private void applyListeners() {
		m_startBtn.addActionListener(e -> {
			if (!m_Controller.getThread().isAlive())
				m_Controller.getThread().start();
		});

		m_pausBtn.addActionListener(e -> {
			if (m_model.isRunning()) {
				m_model.stop();
				m_pausBtn.setLabel(("continue"));
				// ;in
				// this.revalidate();
			} else {
				m_model.continueGame();
				((Button) e.getSource()).setLabel("pause");
				// this.revalidate();
			}
		});

	}

	private class ButtonFrame extends Frame {
		public ButtonFrame() {
			setSize(300, 200);
			Button oneMore = new Button("click");
			oneMore.addActionListener(e -> {
				if (m_model.isRunning()) {
					m_model.stop();
					// ;in remember for exam LUL
					((Button) e.getSource()).setLabel("continue");
					// m_pausBtn.setLabel("continue");
				}
			});
			add(oneMore);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			setVisible(true);

		}
	}

}
