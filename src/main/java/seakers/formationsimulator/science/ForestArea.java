package seakers.formationsimulator.science;

import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import seakers.orekit.object.CoveragePoint;

public class ForestArea extends CoveragePoint {

    private int forestType;

    public ForestArea(BodyShape parentShape, GeodeticPoint point, String name, int type) {
        super(parentShape, point, name);
        this.forestType = type;
    }

    public int getForestType() {
        return forestType;
    }

    public void setForestType(int forestType) {
        this.forestType = forestType;
    }

}
