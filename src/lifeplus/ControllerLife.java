package lifeplus;

/*
 * ;? = design questions
 * ;in = funny constructs etc
 */
public class ControllerLife {
	private int m_SleepTime = 500;
	private ModelLife m_ModelLife;
	private ViewLife m_ViewLife;
	private Thread m_GameThread;
	// ;in  volatile
	volatile private boolean m_StopThread;
	volatile private boolean m_Reset;

	public ControllerLife() {
		m_ModelLife = new ModelLife(100, 80, 00);
		m_ViewLife = new ViewLife(m_ModelLife);
		m_ViewLife.setController(this);
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
			// System.out.println(m_StopThread);
			if(m_Reset){
				stopGame();	
				m_ModelLife.hardReset();
				m_ViewLife.repaint();
				m_Reset = false;
			}
			// ;? is there a different option to multiple flags?
			if (!m_StopThread) {			
				Thread.sleep(m_SleepTime);			
				m_ModelLife.nextCycle();				
				m_ViewLife.repaint();
			}
		}
	} 	
	public void singleStep(){
		m_ModelLife.nextCycle();				
		m_ViewLife.repaint();
	}

	public void setSleepTime(int time) {
		m_SleepTime = time;
	}
	public boolean isRunning(){
		return !m_StopThread;
	}
	public void stopGame(){
		m_StopThread = true;
	}
	public void continueGame(){
		m_StopThread = false;
	}
	public void restartGame(){
		m_Reset = true;
	}
	public Thread getThread() {
		return m_GameThread;
	}
}
