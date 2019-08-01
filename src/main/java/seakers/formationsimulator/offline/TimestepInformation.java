package seakers.formationsimulator.offline;

import org.orekit.bodies.GeodeticPoint;
import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;

public class TimestepInformation {
    AbsoluteDate date;
    ArrayList<Integer> visitedPoints;
    GeodeticPoint groundTrack;
}
