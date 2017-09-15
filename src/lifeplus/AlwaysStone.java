package lifeplus;

public class AlwaysStone extends Stone {

	public AlwaysStone() {
		super();
		super.born();
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public void kill() {
		/*
		 * kill has no impact; always alive never dies
		 */

	}
}
