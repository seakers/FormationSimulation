package seakers.formationsimulator.science;

import org.hipparchus.ode.events.Action;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.handlers.EventHandler;
import seakers.orekit.event.detector.FOVDetector;

public class ForestHandler implements EventHandler<FOVDetector> {
    @Override
    public Action eventOccurred(SpacecraftState s, FOVDetector detector, boolean increasing) {
        if (increasing) {
            ForestArea area = (ForestArea)detector.getPVTarget();
            int formationType = ScienceState.getInstance().formationType;
            if (formationType == area.getForestType()) {
                ScienceState.getInstance().reward += 1;
            }
        }
        return Action.CONTINUE;
    }
}
