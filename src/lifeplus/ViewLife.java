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
	private PopupMenu pop;
	volatile private Color m_StoneColor = Color.BLACK;

	public ViewLife(ModelLife model, ControllerLife controller) {
		m_SCREEN_WIDTH = (int) getToolkit().getScreenSize().getWidth();
		m_SCREEN_HEIGHT = (int) getToolkit().getScreenSize().getHeight() - 30;
		m_model = model;
		m_Controller = controller;
		m_StoneComponents = new StoneComponent[m_Controller.getRowCount()][m_Controller.getColumnCount()];

		buttonFrame = new ButtonFrame();
		loadingScreenRoutine();

		layoutRoutine();
		createMenuBar();
		applyListeners();
		pack();
		setSize(1200, 800);
		setLocationRelativeTo(null); // eike
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closingRoutine();
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
		m_Controller.setPaintBlock(false);
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
//
//		if (roundsAlive == -1)
//			return Color.WHITE;
//		else
//			return m_StoneColor;
	}

	private void determineStaticFields(int i, int j) {
		if (m_model.getStone(i, j) instanceof AlwaysStone || m_model.getStone(i, j) instanceof NeverStone) {

			m_StoneComponents[i][j].setStatic();
		}

	}

	public void newFieldSize() {
		m_pausBtn.setLabel("continue");
		m_model.generateNewStoneArray(m_Controller.getRowCount(), m_Controller.getColumnCount(),
				m_Controller.getPercentStatic());
		m_StoneComponents = new StoneComponent[m_Controller.getRowCount()][m_Controller.getColumnCount()];
		Dimension oldD = m_FieldPanel.getSize();
		m_FieldPanel.removeAll();
		m_FieldPanel.revalidate();
		m_FieldPanel.repaint();
		m_FieldPanel.setLayout(new GridLayout(m_Controller.getRowCount(), m_Controller.getColumnCount(), 1, 1));
		for (int i = 0; i < m_StoneComponents.length; ++i) {
			for (int j = 0; j < m_StoneComponents[i].length; j++) {
				m_StoneComponents[i][j] = new StoneComponent();
				m_StoneComponents[i][j].setMinimumSize(new Dimension(40, 40));
				determineStaticFields(i, j);
				m_FieldPanel.add(m_StoneComponents[i][j]);
			}
		}
		m_FieldPanel.setPreferredSize(oldD);
		pack();
		repaint();
		m_Controller.setResizing(false);
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
			new SettingsDialog(this);
		});
		loadItem.addActionListener(e -> {

		});
		nextCycleItem.addActionListener(e -> {
			oneMoreAction();
		});
		quitItem.addActionListener(e -> {
			closingRoutine();

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
			setLocationRelativeTo(null);
			// getSize() only works after pack, I guess?
			// setLocation(m_SCREEN_WIDTH - (int) getSize().getWidth() / 2,
			// m_SCREEN_HEIGHT - (int) getSize().getHeight() / 2);
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
		MenuItem customSizeMenuItem = new MenuItem("Custom size");
		Menu numberOfStoneMenu = new Menu("number of stones");
		MenuItem fewItem = new MenuItem("10 x 20");
		MenuItem decentItem = new MenuItem(" 25 x 40");
		MenuItem muchItem = new MenuItem("80 x 100");
		fewItem.addActionListener(e -> {
			try {
				m_Controller.newFieldSize(10, 20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});
		decentItem.addActionListener(e -> {
			try {
				m_Controller.newFieldSize(25, 40);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		muchItem.addActionListener(e -> {
			try {
				m_Controller.newFieldSize(80, 100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		customSizeMenuItem.addActionListener(e -> {
			StoneDialog stoneDialog = new StoneDialog(this);

		});
		windowMenu.add(centerItem);
		windowMenu.add(maximizeItem);
		windowMenu.add(iconifyItem);
		windowMenu.add(changeSizeMenu);
		numberOfStoneMenu.add(fewItem);
		numberOfStoneMenu.add(decentItem);
		numberOfStoneMenu.add(muchItem);
		stoneMenu.add(customSizeMenuItem);
		stoneMenu.add(numberOfStoneMenu);
		menuBar.add(fileMenu);
		menuBar.add(loadMenu);
		menuBar.add(windowMenu);
		menuBar.add(stoneMenu);
		setMenuBar(menuBar);

		pop = new PopupMenu();
		pop.add("kee");
		add(pop);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	protected void processMouseEvent(MouseEvent e) {
		System.out.println("dd");
		if (e.isPopupTrigger()) {
			pop.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
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

		rPanel.add(new Button("ok"));
		add(BorderLayout.SOUTH, btmPanel);
		add(BorderLayout.EAST, rPanel);
		ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
		m_FieldPanel = new Panel();
		scrollPane.add(m_FieldPanel);
		m_FieldPanel.setLayout(new GridLayout(m_Controller.getRowCount(), m_Controller.getColumnCount(), 1, 1));

		for (int i = 0; i < m_StoneComponents.length; ++i) {
			for (int j = 0; j < m_StoneComponents[i].length; j++) {
				m_StoneComponents[i][j] = new StoneComponent();
				determineStaticFields(i, j);
				m_FieldPanel.add(m_StoneComponents[i][j]);
			}
		}
		add(scrollPane, BorderLayout.CENTER);
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

	public void closingRoutine() {
		m_Controller.stopGame();
		ClosingDialog closingDialog = new ClosingDialog(ViewLife.this, "Do you really want to quit?");
		if (closingDialog.m_DialogResult) {
			buttonFrame.dispose();
			dispose();
			m_Controller.killThread();
		} else {
			m_Controller.continueGame();
		}
	}

	private class ClosingDialog extends Dialog {
		public boolean m_DialogResult;

		public ClosingDialog(Frame owner, String msg) {
			super(owner, "", true);
			setLayout(new BorderLayout());
			setResizable(false);
			add(BorderLayout.CENTER, new Label(msg));
			Panel buttonPanel = new Panel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			add(BorderLayout.SOUTH, buttonPanel);
			Button b = new Button("Yes");
			b.addActionListener(e -> {
				m_DialogResult = true;
				dispose();
			});
			buttonPanel.add(b);
			Button a = new Button("No");
			a.addActionListener(e -> {
				m_DialogResult = false;
				dispose();
			});
			buttonPanel.add(a);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			pack();
			Point p = owner.getLocation();
			setLocation(p.x + (int) owner.getSize().getWidth() / 2 - (int) getSize().getWidth() / 2,
					p.y + (int) owner.getSize().getHeight() / 2 - (int) getSize().getHeight() / 2);
			setVisible(true);

		}
	}

	private class StoneDialog extends Dialog {
		public StoneDialog(Frame owner) {
			super(owner, "", false);
			setLayout(new BorderLayout());
			Panel input = new Panel();
			input.setLayout(new GridLayout(2, 2));
			setResizable(false);
			input.add(new Label("Rows:"));
			TextField rowText = new TextField();
			input.add(rowText);
			input.add(new Label("Columns:"));
			TextField columnText = new TextField();
			input.add(columnText);
			add(BorderLayout.CENTER, input);
			Panel buttonPanel = new Panel();
			buttonPanel.setLayout(new FlowLayout());
			Button confirmButton = new Button("Confirm");
			Label errorLabel = new Label();
			errorLabel.setPreferredSize(new Dimension(200, 50));
			;
			errorLabel.setForeground(Color.RED);
			confirmButton.addActionListener(e -> {
				try {
					m_Controller.newFieldSize(Integer.parseInt(rowText.getText()),
							Integer.parseInt(columnText.getText()));
					dispose();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					errorLabel.setText("Error, not a numeric input");
				}
			});
			Button cancelButton = new Button("Cancel");
			cancelButton.addActionListener(e -> {
				dispose();
			});
			buttonPanel.add(confirmButton);
			buttonPanel.add(cancelButton);
			buttonPanel.add(errorLabel);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			add(BorderLayout.SOUTH, buttonPanel);
			pack();
			Point p = owner.getLocation();
			setLocation(p.x + (int) owner.getSize().getWidth() / 2 - (int) getSize().getWidth() / 2,
					p.y + (int) owner.getSize().getHeight() / 2 - (int) getSize().getHeight() / 2);
			setVisible(true);
		}
	}

	private class SettingsDialog extends Dialog {
		public SettingsDialog(Frame owner) {
			super(owner, "", false);
			setLayout(new GridLayout(4, 1));
			Panel colorPanel = new Panel();
			colorPanel.setLayout(new FlowLayout());
			colorPanel.add(new Label("Stone color:"));
			CheckboxGroup colorCheckBoxes = new CheckboxGroup();
			Checkbox blackBox = new Checkbox("Black", colorCheckBoxes, true);
			blackBox.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED)
					m_StoneColor = Color.BLACK;
				owner.repaint();
			});
			colorPanel.add(blackBox);
			Checkbox redBox = new Checkbox("Red", colorCheckBoxes, false);
			redBox.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED)
					m_StoneColor = Color.RED;
				owner.repaint();

			});
			colorPanel.add(redBox);
			Checkbox blueBox = new Checkbox("Blue", colorCheckBoxes, false);
			blueBox.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED)
					m_StoneColor = Color.BLUE;
				owner.repaint();

			});
			colorPanel.add(blueBox);
			add(colorPanel);
			Panel formPanel = new Panel();
			formPanel.setLayout(new FlowLayout());
			formPanel.add(new Label("Form of playstone:"));
			CheckboxGroup formGroup = new CheckboxGroup();
			Checkbox rectangle = new Checkbox("Rectangle", formGroup, true);
			rectangle.addItemListener(e -> {
				for (int i = 0; i < m_StoneComponents.length; i++) {
					for (int j = 0; j < m_StoneComponents[i].length; j++) {
						m_StoneComponents[i][j].setOvals(false);
					}
				}
			});
			formPanel.add(rectangle);
			Checkbox oval = new Checkbox("Oval", formGroup, false);
			oval.addItemListener(e->{
				for (int i = 0; i < m_StoneComponents.length; i++) {
					for (int j = 0; j < m_StoneComponents[i].length; j++) {
						m_StoneComponents[i][j].setOvals(true);
					}
				}
			});
			formPanel.add(oval);
			add(formPanel);
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
			add(slider);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			pack();
			setVisible(true);
		}
	}

}
