package lifeplus;

public class DiseaseAdapter implements I_Disease {
	private double _infectionRate;
	private double _deceaseRate;
	private double _healingRate;
	
	public DiseaseAdapter(double infRate, double decRate, double healRate){
		_infectionRate = infRate;
		_deceaseRate = decRate;
		_healingRate = healRate;
	}
	
	@Override
	public double chanceOfInfection() {
		return _infectionRate;
	}

	@Override
	public double chanceOfDecease() {
		return _deceaseRate;
	}

	@Override
	public double chanceOfCure() {
		return _healingRate;
	}
}
