package seakers.formationsimulator.science;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.handlers.EventHandler;
import seakers.orekit.event.detector.FOVDetector;

public class ForestHandler implements EventHandler<FOVDetector> {
    private static final Logger logger = LogManager.getLogger(ForestHandler.class);

    private boolean saveVisit;

    public ForestHandler() {
        this(false);
    }

    public ForestHandler(boolean saveVisit) {
        super();
        this.saveVisit = saveVisit;
    }

    @Override
    public Action eventOccurred(SpacecraftState s, FOVDetector detector, boolean increasing) {
        if (increasing) {
            ForestArea area = (ForestArea)detector.getPVTarget();
            int formationType = ScienceState.getInstance().formationType;
            GeodeticPoint point = detector.getPVTarget().getPoint();
            if (saveVisit) {
                ScienceState.getInstance().scienceAtStep.add(area.getForestType());
            }
            if (formationType == area.getForestType()) {
                logger.debug("I have measured a forest of type " + area.getForestType() + " at (" + FastMath.toDegrees(point.getLatitude()) + ", " + FastMath.toDegrees(point.getLongitude()) + ")!");
                ScienceState.getInstance().reward += 1;
            }
        }
        return Action.CONTINUE;
    }
}
