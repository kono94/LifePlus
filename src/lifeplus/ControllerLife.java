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
	public ControllerLife(){
		m_ModelLife = new ModelLife(100, 80, 00);
		m_ViewLife = new ViewLife(m_ModelLife);
		m_ViewLife.setController(this);
		m_GameThread = new Thread(){
			@Override
			public void run(){
				try {
					runGame();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}	

	public void runGame() throws InterruptedException{
		while (true) {
			Thread.sleep(m_SleepTime);
			m_ModelLife.nextCycle();
			m_ViewLife.repaint();
		}
	}
	public void setSleepTime(int time){
		m_SleepTime = time;
	}
	public Thread getThread(){
		return m_GameThread;
	}
}
