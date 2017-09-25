package lifeplus;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * ;? = design questions
 * ;in = funny constructs etc
 * maximize window:	setExtendedState(MAXIMIZED_BOTH);
 * 
 * cast lambda
 * m_Controller.setSleepTime(((JSlider) e.getSource()).getValue());
 */
public class ControllerLife {
	private int m_SleepTime = 500;
	private ModelLife m_ModelLife;
	private ViewLife m_ViewLife;
	private Thread m_GameThread;
	private int m_RowCount = 100;
	private int m_ColumnCount = 80;
	private double m_PercentStatic = 0.05;
	// ;in volatile
	volatile private AtomicBoolean m_StopThread = new AtomicBoolean(false);
	volatile private AtomicBoolean m_Reset = new AtomicBoolean(false);
	volatile private AtomicBoolean m_DestroyThread = new AtomicBoolean(false);	
	volatile private AtomicBoolean m_PaintBlocks = new AtomicBoolean(false);
	volatile private AtomicBoolean m_CurrentlyResizing = new AtomicBoolean(false);

	public ControllerLife() {
		m_ModelLife = new ModelLife(m_RowCount, m_ColumnCount, m_PercentStatic);
		m_ViewLife = new ViewLife(m_ModelLife, this);
		m_GameThread = new Thread() {
			@Override
			public void run() {
				try {
					runGame();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	public void runGame() throws InterruptedException {
		while (true) {
			if (m_DestroyThread.get())
				return;
			// System.out.println(m_StopThread);
			if (m_Reset.get()) {
				stopGame();
				m_ModelLife.hardReset();
				m_PaintBlocks.set(true);
				m_ViewLife.repaint();
				m_Reset.set(false);
			}
			// ;? is there a different option to multiple flags?
			if (!m_StopThread.get() && !m_PaintBlocks.get() && !m_CurrentlyResizing.get()) {			
					Thread.sleep(m_SleepTime);
					m_ModelLife.nextCycle();
					m_PaintBlocks.set(true);
					m_ViewLife.repaint();				
			}		
		}
	}

	public void newFieldSize(int rows, int columns) throws InterruptedException {
		stopGame();
		setRowCount(rows);
		setColumnCount(columns);
		m_CurrentlyResizing.set(true);
		m_ViewLife.newFieldSize();	
	}
	public void setResizing(boolean b){
		m_CurrentlyResizing.set(b);
	}
	public void singleStep() {
		m_ModelLife.nextCycle();
		m_ViewLife.repaint();
	}

	public void killThread() {
		m_DestroyThread.set(true);
	}

	public void setSleepTime(int time) {
		m_SleepTime = time;
	}

	public boolean isRunning() {
		return !m_StopThread.get();
	}

	public void stopGame() {
		m_StopThread.set(true);
	}

	public void continueGame() {
		m_StopThread.set(false);
	}

	public void restartGame() {
		m_Reset.set(true);
	}
	public void setPaintBlock(boolean b){
		m_PaintBlocks.set(b);
	}
	public Thread getThread() {
		return m_GameThread;
	}

	public int getRowCount() {
		return m_RowCount;
	}

	public void setRowCount(int m_RowCount) {
		this.m_RowCount = m_RowCount;
	}

	public int getColumnCount() {
		return m_ColumnCount;
	}

	public void setColumnCount(int m_ColumnCount) {
		this.m_ColumnCount = m_ColumnCount;
	}

	public double getPercentStatic() {
		return m_PercentStatic;
	}

	public void setPercentStatic(double m_PercentStatic) {
		this.m_PercentStatic = m_PercentStatic;
	}

}
