package lifeplus;

public class Stone {

	private Stone[] m_Neighbours;
	private int m_RoundsAlive;
	public static final int TOP = 0, TOP_RIGHT = 1, RIGHT = 2, BOTTOM = 4, BOTTOM_RIGHT = 3, BOTTOM_LEFT = 5, LEFT = 6,
			TOP_LEFT = 7;
	private I_Disease[] m_Diseases = { new Cancer(), new Plague() };
	private int m_DiseaseIndex;

	public Stone() {
		m_Neighbours = new Stone[8];
		m_RoundsAlive = -1;
		m_DiseaseIndex = -1;
	}

	public boolean aliveNextCycle() {
		double checkForSurvival = Math.random();

		if (isAlive() && (getNeighbourCount() == 2 || getNeighbourCount() == 3)) {
			if (hasDisease()) {
				if (checkForSurvival < m_Diseases[m_DiseaseIndex].chanceOfDecease()) {
					return false;
				}
			} else {
				return true;
			}
		} else if (!isAlive() && getNeighbourCount() == 3) {
			// newborn can be infected? if no add -> return true;
			return true;
		} else {
			return false;
		}
		return isAlive();
	}

	public int diseaseIndexNextCycle() {
		double checkForHealing = Math.random();
		double checkForInfection = Math.random();

		if (isAlive()) {
			for (int i = 0; i < m_Neighbours.length; ++i) {
				if (!hasDisease() && m_Neighbours[i].hasDisease()
						&& checkForInfection < m_Diseases[m_Neighbours[i].getDiseaseIndex()].chanceOfInfection()) {
					return m_Neighbours[i].getDiseaseIndex();
				}
			}
			if (hasDisease() && checkForHealing < m_Diseases[m_DiseaseIndex].chanceOfCure()) {
				return -1;
			}
		} else {
			return -1;
		}
		return m_DiseaseIndex;
	}

	public void computeNext(boolean alive, int diseaseIndex) {
		m_DiseaseIndex = diseaseIndex;
		if (isAlive()) {
			if (alive)
				survived();
			else
				kill();
		} else {
			if (alive)
				born();
		}
	}

	public boolean isAlive() {
		return m_RoundsAlive == -1 ? false : true;
	}

	public void setNeighbour(int indexInNeighbourArray, Stone neighbour) {
		m_Neighbours[indexInNeighbourArray] = neighbour;
	}

	public void survived() {
		++m_RoundsAlive;
	}

	public void kill() {
		m_RoundsAlive = -1;
		m_DiseaseIndex = -1; // dead means stone has no disease anymore
	}

	public void born() {
		m_RoundsAlive = 0;
	}

	// redudant ?!
	public void heal() {
		m_DiseaseIndex = -1;
	}

	public boolean hasDisease() {
		return m_DiseaseIndex == -1 ? false : true;
	}

	public int getNeighbourCount() {
		int tempCounter = 0;
		for (int i = 0; i < m_Neighbours.length; ++i) {
			if (m_Neighbours[i].isAlive()) {
				++tempCounter;
			}
		}
		return tempCounter;
	}

	public int getRoundsAlive() {
		return m_RoundsAlive;
	}

	public int getDiseaseIndex() {
		return m_DiseaseIndex;
	}
}
