package seakers.formationsimulator.science;

public class ScienceState {

    private static ScienceState instance = null;

    public double reward;

    public int formationType;

    public static ScienceState getInstance() {
        if (instance == null)
            instance = new ScienceState();

        return instance;
    }

    private ScienceState() {
        reward = 0.;
        formationType = 1;
    }
}
