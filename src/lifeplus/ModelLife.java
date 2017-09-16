package lifeplus;

public class ModelLife {
	private int m_RowCount;
	private int m_ColumnCount; 
	private Stone[][] m_Stones;
	private double m_ChanceOfStaticFields;
	private boolean resetOnNextCycle;
	
	public ModelLife(int rows, int columns, double ChanceOfStaticFields){
		m_Stones = new Stone[rows][columns];
		m_RowCount = rows;
		m_ColumnCount = columns;
		m_ChanceOfStaticFields = ChanceOfStaticFields;
		initilize();
	}
	
	private void initilize(){
		createField();
		refrenceAllNeighbours();
	}
	private void createField() {
		int alive = 0;
		int dead = 0;
		int infectedCells = 0;
		for (int i = 0; i < m_RowCount; ++i) {
			for (int j = 0; j < m_ColumnCount; ++j) {
				if (Math.random() > (1 - m_ChanceOfStaticFields)) {
					if (Math.random() > 0.35) {
						m_Stones[i][j] = new AlwaysStone();
						++alive;
					} else {
						m_Stones[i][j] = new NeverStone();
						++dead;
					}
				} else {
					if (Math.random() > 0.5) {
						m_Stones[i][j] = new Stone();
						m_Stones[i][j].born();
					} else {
						m_Stones[i][j] = new Stone();
						++dead;
					}
				}
				if(m_Stones[i][j].hasDisease())
					++infectedCells;
			}
		}			
	}
	
	
	
	public void refrenceAllNeighbours() {
		for (int y = 0; y < m_Stones.length; ++y)
		{
			for (int x = 0; x < m_Stones[y].length; ++x)
			{
				m_Stones[y][x].setNeighbour(Stone.TOP, 			getStone( (y == 0 ? m_RowCount : y) - 1, x));
				m_Stones[y][x].setNeighbour(Stone.TOP_RIGHT,		getStone( (y == 0 ? m_RowCount : y) - 1, (x == m_ColumnCount-1 ? -1 : x) + 1));
				m_Stones[y][x].setNeighbour(Stone.TOP_LEFT,		getStone( (y == 0 ? m_RowCount : y) - 1, (x == 0 ? m_ColumnCount : x) - 1));
				m_Stones[y][x].setNeighbour(Stone.BOTTOM, 		getStone( (y == m_RowCount-1 ? -1 : y) + 1, x));
				m_Stones[y][x].setNeighbour(Stone.BOTTOM_RIGHT,  getStone( (y == m_RowCount-1 ? -1 : y) + 1, (x == m_ColumnCount-1 ? -1 : x) + 1));
				m_Stones[y][x].setNeighbour(Stone.BOTTOM_LEFT,	getStone( (y == m_RowCount-1 ? -1 : y) + 1, (x == 0 ? m_ColumnCount : x) - 1));
				m_Stones[y][x].setNeighbour(Stone.LEFT, 			getStone( y, (x == 0 ? m_ColumnCount : x) - 1));
				m_Stones[y][x].setNeighbour(Stone.RIGHT,			getStone( y, (x == m_ColumnCount-1 ? -1 : x) + 1));
				
			}
		}
	}

	public void nextCycle(){
		boolean[][] tempNext = new boolean[m_RowCount][m_ColumnCount];
		int[][] tempDiseaseNext = new int[m_RowCount][m_ColumnCount];
		for (int y = 0; y < m_Stones.length; ++y)
		{
			for (int x = 0; x < m_Stones[y].length; ++x)
			{
				tempNext[y][x] = m_Stones[y][x].aliveNextCycle();		
				tempDiseaseNext[y][x] = m_Stones[y][x].diseaseIndexNextCycle();
			}
		}
		
		for (int y = 0; y < m_Stones.length; ++y)
		{
			for (int x = 0; x < m_Stones[y].length; ++x)
			{
				m_Stones[y][x].computeNext(tempNext[y][x], tempDiseaseNext[y][x]);
				
			}
		}
		if(resetOnNextCycle){
			initilize();
			resetOnNextCycle = false;
		}
	}
	
	public void resetField(){
		resetOnNextCycle = true;
	}	
	public void hardReset(){
		initilize();
	}

	public Stone getStone(int i, int j) {	
		return m_Stones[i][j];
	}
	
	public int getRowCount(){
		return m_RowCount;
	}
	public int getColumnCount(){
		return m_ColumnCount;
	}
}
