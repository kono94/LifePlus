package lifeplus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class ViewLife extends Frame {
	private ModelLife m_model;
	private ControllerLife m_Controller;
	private StoneComponent[][] m_StoneComponents;
	private Image m_Img; // Double Buffering
	private Image m_ImgBuffer; // -II-
	private Image m_loadingTextImage;
	private int m_iWidth = 0;
	private int m_iMaxWidth = 0;
	private final int m_SCREEN_WIDTH;
	private final int m_SCREEN_HEIGHT;
	// ;? crazy? Buttons as fields to change Labels (pause->continue)
	private Button m_pausBtn;
	private Button m_startBtn;
	private Button m_restartBtn;
	// ;? extra field or just local in constructor to call dispose();
	private ButtonFrame buttonFrame;
	private Panel m_FieldPanel;

	public ViewLife(ModelLife model, ControllerLife controller) {
		m_SCREEN_WIDTH = (int) getToolkit().getScreenSize().getWidth();
		m_SCREEN_HEIGHT = (int) getToolkit().getScreenSize().getHeight() - 30;
		m_model = model;
		m_Controller = controller;
		m_StoneComponents = new StoneComponent[m_Controller.getRowCount()][m_Controller.getColumnCount()];

		buttonFrame = new ButtonFrame();
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
				buttonFrame.dispose();
				dispose();
				m_Controller.killThread();
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
		for (int i = 0; i < m_StoneComponents.length; ++i) {
			for (int j = 0; j < m_StoneComponents[i].length; j++) {
				m_StoneComponents[i][j].setColor(determineColor(m_model.getStone(i, j)));
				m_StoneComponents[i][j].update(m_StoneComponents[i][j].getGraphics());
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

			m_StoneComponents[i][j].setStatic();
		}

	}

	public void newFieldSize() {
		m_pausBtn.setLabel("continue");
		m_model.generateNewStoneArray(m_Controller.getRowCount(), m_Controller.getColumnCount(), m_Controller.getPercentStatic());
		m_StoneComponents = new StoneComponent[m_Controller.getRowCount()][m_Controller.getColumnCount()];
		Dimension oldD = m_FieldPanel.getSize();
		m_FieldPanel.removeAll();
		m_FieldPanel.revalidate();
		m_FieldPanel.repaint();
		m_FieldPanel.setLayout(new GridLayout(m_Controller.getRowCount(), m_Controller.getColumnCount(), 1, 1));
		for (int i = 0; i < m_StoneComponents.length; ++i) {
			for (int j = 0; j < m_StoneComponents[i].length; j++) {
				m_StoneComponents[i][j] = new StoneComponent();
				determineStaticFields(i, j);
				m_FieldPanel.add(m_StoneComponents[i][j]);
			}
		}
		m_FieldPanel.setPreferredSize(oldD);		
		pack();				
		repaint();
	}

	private void createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu loadMenu = new Menu("Presets");
		Menu windowMenu = new Menu("Window");
		MenuItem newItem = new MenuItem("New");
		MenuItem saveItem = new MenuItem("Save");
		MenuItem loadItem = new MenuItem("Load");
		MenuItem nextCycleItem = new MenuItem("Display next cycle");
		MenuItem quitItem = new MenuItem("Quit");
		newItem.addActionListener(e -> {
			m_Controller.restartGame();
		});
		saveItem.addActionListener(e -> {

		});
		loadItem.addActionListener(e -> {

		});
		nextCycleItem.addActionListener(e -> {
			oneMoreAction();
		});
		quitItem.addActionListener(e -> {
			buttonFrame.dispose();
			dispose();
			m_Controller.killThread();

		});
		fileMenu.add(newItem);
		fileMenu.add(saveItem);
		fileMenu.add(loadItem);
		fileMenu.add(nextCycleItem);
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

		MenuItem centerItem = new MenuItem("center Screen");
		MenuItem maximizeItem = new MenuItem("maximize");
		MenuItem iconifyItem = new MenuItem("iconify");
		Menu changeSizeMenu = new Menu("change screen size");
		MenuItem smallFieldItem = new MenuItem("small");
		MenuItem mediumFieldItem = new MenuItem("medium");
		MenuItem largeFieldItem = new MenuItem("large");
		changeSizeMenu.add(smallFieldItem);
		changeSizeMenu.add(mediumFieldItem);
		changeSizeMenu.add(largeFieldItem);
		centerItem.addActionListener(e -> {
			// ;in ;? exam?
			// setLocationRelativeTo(null);
			setLocation(m_SCREEN_WIDTH - (int) getSize().getWidth() / 2,
					m_SCREEN_HEIGHT - (int) getSize().getHeight() / 2);
		});
		maximizeItem.addActionListener(e -> {
			// setBounds(0,0, (int) getToolkit().getScreenSize().getWidth(),
			// (int) getToolkit().getScreenSize().getHeight());
			// ;in ;? shortcut to maximize the window; exam?!
			setExtendedState(MAXIMIZED_BOTH);
		});
		iconifyItem.addActionListener(e -> {
			setExtendedState(ICONIFIED);
		});
		smallFieldItem.addActionListener(e -> {
			setSize((int) (m_SCREEN_WIDTH / 2.25), (int) (m_SCREEN_HEIGHT / 1.7));
			setLocationRelativeTo(null);
		});
		mediumFieldItem.addActionListener(e -> {
			setSize((int) (m_SCREEN_WIDTH / 1.8), (int) (m_SCREEN_HEIGHT / 1.5));
			setLocationRelativeTo(null);
		});
		largeFieldItem.addActionListener(e -> {
			setSize((int) (m_SCREEN_WIDTH / 1.25), (int) (m_SCREEN_HEIGHT / 1.1));
			setLocationRelativeTo(null);
		});
		Menu stoneMenu = new Menu("Stone field");
		Menu numberOfStoneMenu = new Menu("number of stones");
		MenuItem fewItem = new MenuItem("10 x 20");
		MenuItem decentItem = new MenuItem(" 25 x 40");
		MenuItem muchItem = new MenuItem("80 x 100");
		fewItem.addActionListener(e -> {
			m_Controller.newFieldSize(10, 20);
		});
		decentItem.addActionListener(e -> {
			m_Controller.newFieldSize(25, 40);
		});
		muchItem.addActionListener(e -> {
			m_Controller.newFieldSize(80, 100);
		});
		windowMenu.add(centerItem);
		windowMenu.add(maximizeItem);
		windowMenu.add(iconifyItem);
		windowMenu.add(changeSizeMenu);
		numberOfStoneMenu.add(fewItem);
		numberOfStoneMenu.add(decentItem);
		numberOfStoneMenu.add(muchItem);
		stoneMenu.add(numberOfStoneMenu);
		menuBar.add(fileMenu);
		menuBar.add(loadMenu);
		menuBar.add(windowMenu);
		menuBar.add(stoneMenu);
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
		m_restartBtn = new Button("reset");
		btmPanel.add(m_restartBtn);

		JSlider slider = new JSlider(0, 500, 300);
		slider.setMajorTickSpacing(100);
		slider.createStandardLabels(100);
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

		m_FieldPanel = new Panel();
		m_FieldPanel.setLayout(new GridLayout(m_Controller.getRowCount(), m_Controller.getColumnCount(), 1, 1));
		for (int i = 0; i < m_StoneComponents.length; ++i) {
			for (int j = 0; j < m_StoneComponents[i].length; j++) {
				m_StoneComponents[i][j] = new StoneComponent();
				determineStaticFields(i, j);
				m_FieldPanel.add(m_StoneComponents[i][j]);
			}
		}
		add(BorderLayout.CENTER, m_FieldPanel);
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
			m_startBtn.setEnabled(false);
		});

		m_pausBtn.addActionListener(e -> {
			if (m_Controller.isRunning()) {
				m_Controller.stopGame();
				m_pausBtn.setLabel(("continue"));
				// ;in
				// this.revalidate();
			} else {
				m_Controller.continueGame();
				((Button) e.getSource()).setLabel("pause");
				// this.revalidate();
			}
		});

		m_restartBtn.addActionListener(e -> {
			// if (!m_Controller.getThread().isAlive())
			m_Controller.restartGame();
			m_pausBtn.setLabel("continue");
		});

	}

	private void oneMoreAction() {
		if (m_Controller.isRunning()) {
			m_Controller.stopGame();
			// ;in remember for exam LUL
			// ((Button) e.getSource()).setLabel("one");
			m_pausBtn.setLabel("continue");
		} else {
			m_Controller.singleStep();
		}
	}

	private class ButtonFrame extends Frame {
		public ButtonFrame() {
			setSize(300, 200);
			Button oneMore = new Button("One more cycle");
			oneMore.addActionListener(e -> {
				oneMoreAction();
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


