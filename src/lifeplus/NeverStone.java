package lifeplus;

public class NeverStone extends Stone {
	
	public NeverStone() {
		super();
		super.kill();
	}
	
	@Override
	public boolean isAlive() {
		return false;
	}
	

	@Override
	public int getRoundsAlive() {
		return -1;
	}

	@Override
	public void survived() {
	 // safety redudant???
	}

	@Override
	public void born() {
		// What is dead may never reborn
	}

	@Override
	public void kill() {
		// kill has no impact
		// what is dead may never die
	}
}
