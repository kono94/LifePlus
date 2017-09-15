package lifeplus;

public class ControllerLife {
	public static void main(String[] args) throws InterruptedException {

		ModelLife vModel = new ModelLife(100, 80, 00);
		ViewLife vLife = new ViewLife(vModel);
		while (true) {
			Thread.sleep(10);
			vModel.nextCycle();
			vLife.repaint();
		}
	}
}
