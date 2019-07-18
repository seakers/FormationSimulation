package seakers.formationsimulator.server;

import org.apache.thrift.TException;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import seakers.formationsimulator.science.ForestArea;
import seakers.formationsimulator.science.ForestHandler;
import seakers.formationsimulator.science.ScienceState;
import seakers.formationsimulator.thrift.GroundPosition;
import seakers.formationsimulator.thrift.Orekit;
import seakers.formationsimulator.thrift.SpacecraftState;
import seakers.formationsimulator.thrift.Vector3D;
import seakers.orekit.event.detector.FOVDetector;
import seakers.orekit.object.*;
import seakers.orekit.object.communications.ReceiverAntenna;
import seakers.orekit.object.communications.TransmitterAntenna;
import seakers.orekit.object.fieldofview.NadirRectangularFOV;
import seakers.orekit.propagation.PropagatorFactory;
import seakers.orekit.propagation.PropagatorType;
import seakers.orekit.util.Orbits;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrekitHandler implements Orekit.Iface {

    private AbsoluteDate startDate;
    private AbsoluteDate endDate;
    private AbsoluteDate extrapDate;

    private Propagator propagator;
    private org.orekit.propagation.SpacecraftState currentState;

    private Frame earthFrame;
    private Frame inertialFrame;
    private BodyShape earthShape;

    private int stepsBeforeChange;
    private int newFormationType;

    private boolean done;

    private int steps;

    public OrekitHandler() {
        // configure Orekit
        File home       = new File(System.getProperty("user.home"));
        File orekitData = new File(home, "orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n",
                    orekitData.getAbsolutePath());
            System.err.format(Locale.US, "You need to download %s from %s, unzip it in %s and rename it 'orekit-data' for this tutorial to work%n",
                    "orekit-data-master.zip", "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
                    home.getAbsolutePath());
            System.exit(1);
        }
        DataProvidersManager manager = DataProvidersManager.getInstance();
        manager.addProvider(new DirectoryCrawler(orekitData));

        // if running on a non-US machine, need the line below
        Locale.setDefault(new Locale("en", "US"));

        // setup logger
        Level level = Level.ALL;
        Logger.getGlobal().setLevel(level);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        Logger.getGlobal().addHandler(handler);
    }

    @Override
    public void reset() throws TException {
        TimeScale utc = TimeScalesFactory.getUTC();
        startDate = new AbsoluteDate(2021, 12, 1, 18, 00, 00.000, utc);
        endDate = new AbsoluteDate(2022, 1, 1, 18, 00, 00.000, utc);
        extrapDate = startDate;
        double mu = Constants.WGS84_EARTH_MU; // gravitation coefficient

        // must use IERS_2003 and EME2000 frames to be consistent with STK
        earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2003, true);
        inertialFrame = FramesFactory.getEME2000();

        earthShape = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING, earthFrame);

        // Enter satellite orbital parameters
        double h = 747000;
        double a747 = Constants.WGS84_EARTH_EQUATORIAL_RADIUS + h;
        double iSSO = Orbits.incSSO(h);
        double RAAN = Orbits.LTAN2RAAN(h, 18.0, 1, 12, 2021);

        // define instruments
        NadirRectangularFOV fov = new NadirRectangularFOV(FastMath.toRadians(10), FastMath.toRadians(20), 0, earthShape);
        ArrayList<Instrument> payload = new ArrayList<>();
        Instrument view1 = new Instrument("SAR", fov, 100, 100);
        payload.add(view1);

        ArrayList<Satellite> satellites = new ArrayList<>();

        HashSet<CommunicationBand> satBands = new HashSet<>();
        satBands.add(CommunicationBand.UHF);

        Orbit orb1 = new KeplerianOrbit(a747, 0.0001, iSSO, 0.0, RAAN, FastMath.toRadians(0), PositionAngle.MEAN, inertialFrame, startDate, mu);
        Satellite sat1 = new Satellite("mainsat", orb1, null, payload,
                new ReceiverAntenna(6., satBands), new TransmitterAntenna(6., satBands), Propagator.DEFAULT_MASS, Propagator.DEFAULT_MASS);

        satellites.add(sat1);
        // TODO: Add more satellites to the formation

        Constellation constellation = new Constellation("forest-sar", satellites);

        // load forest points
        ArrayList<CoveragePoint> forests = new ArrayList<>();
        try {
            File file = new File("/Users/anmartin/Projects/summer_project/gym-orekit/forest_data.tiff");

            GridCoverage2DReader reader = new GeoTiffReader(file);
            GridCoverage2D coverage = reader.read(null);

            CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
            GridEnvelope2D gridRange = coverage.getGridGeometry().getGridRange2D();

            for (int i = gridRange.getLow(0); i < gridRange.getHigh(0); ++i) {
                for (int j = gridRange.getLow(1); j < gridRange.getHigh(1); ++j) {
                    GridCoordinates2D pointGrid = new GridCoordinates2D(i, j);
                    int[] valueArray = new int[1];
                    valueArray = coverage.evaluate(pointGrid, valueArray);
                    if (valueArray[0] != 255) {
                        DirectPosition latlng = coverage.getGridGeometry().gridToWorld(pointGrid);
                        GeodeticPoint orekit_latlng = new GeodeticPoint(
                                FastMath.toRadians(latlng.getOrdinate(0)),
                                FastMath.toRadians(latlng.getOrdinate(1)),
                                0.);
                        forests.add(new ForestArea(earthShape, orekit_latlng, "forest", valueArray[0]));
                    }
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (TransformException te) {
            te.printStackTrace();
        }

        CoverageDefinition covDef1 = new CoverageDefinition("earth-forests", forests);
        covDef1.assignConstellation(constellation);
        HashSet<CoverageDefinition> covDefs = new HashSet<>();
        covDefs.add(covDef1);

        Properties propertiesPropagator = new Properties();
        propertiesPropagator.setProperty("orekit.propagator.mass", "100");
        propertiesPropagator.setProperty("orekit.propagator.atmdrag", "false");
        propertiesPropagator.setProperty("orekit.propagator.dragarea", "0.075");
        propertiesPropagator.setProperty("orekit.propagator.dragcoeff", "2.2");
        propertiesPropagator.setProperty("orekit.propagator.thirdbody.sun", "true");
        propertiesPropagator.setProperty("orekit.propagator.thirdbody.moon", "true");
        propertiesPropagator.setProperty("orekit.propagator.solarpressure", "true");
        propertiesPropagator.setProperty("orekit.propagator.solararea", "0.058");

        PropagatorFactory pf = new PropagatorFactory(PropagatorType.NUMERICAL, propertiesPropagator);
        propagator = pf.createPropagator(orb1, 100);

        // add all forest points to be analyzed
        double fovStepSize = sat1.getOrbit().getKeplerianPeriod() / 1000.;
        double threshold = 1e-3;
        for (CoveragePoint point: covDef1.getPoints()) {
            FOVDetector fovDetec = new FOVDetector(point, view1)
                    .withMaxCheck(fovStepSize)
                    .withThreshold(threshold)
                    .withHandler(new ForestHandler());

            propagator.addEventDetector(fovDetec);
        }

        done = false;
        steps = 0;
        currentState = propagator.getInitialState();
        stepsBeforeChange = 0;
        newFormationType = 0;
    }

    @Override
    public void step() throws TException {
        // Step duration in seconds
        double stepT = 30.;
        ScienceState.getInstance().reward = 0.;

        if (stepsBeforeChange > 0) {
            --stepsBeforeChange;
            if (stepsBeforeChange == 0) {
                ScienceState.getInstance().formationType = newFormationType;
            }
        }

        if (extrapDate.compareTo(endDate) <= 0) {
            currentState = propagator.propagate(extrapDate);

            System.out.println("step " + steps++);
            System.out.println(" time : " + currentState.getDate());
            System.out.println(" " + currentState.getOrbit());

            extrapDate = extrapDate.shiftedBy(stepT);
        }
        else {
            done = true;
        }
    }

    @Override
    public boolean done() throws TException {
        return done;
    }

    @Override
    public List<SpacecraftState> currentStates() throws TException {
        ArrayList<SpacecraftState> states = new ArrayList<>();
        // TODO: Multiple satellites
        org.hipparchus.geometry.euclidean.threed.Vector3D positionOrekit = currentState.getPVCoordinates().getPosition();
        org.hipparchus.geometry.euclidean.threed.Vector3D velocityOrekit = currentState.getPVCoordinates().getVelocity();
        Vector3D position = new Vector3D(positionOrekit.getX(), positionOrekit.getY(), positionOrekit.getZ());
        Vector3D velocity = new Vector3D(velocityOrekit.getX(), velocityOrekit.getY(), velocityOrekit.getZ());
        SpacecraftState state = new SpacecraftState(position, velocity);
        states.add(state);
        return states;
    }

    @Override
    public double getReward() throws TException {
        return ScienceState.getInstance().reward;
    }

    @Override
    public GroundPosition groundPosition() throws TException {
        PVCoordinates currentCoords = this.currentState.getPVCoordinates();
        org.hipparchus.geometry.euclidean.threed.Vector3D currentPos = currentCoords.getPosition();
        GeodeticPoint earthPoint = earthShape.transform(currentPos, inertialFrame, currentState.getDate());
        return new GroundPosition(
                FastMath.toDegrees(earthPoint.getLatitude()),
                FastMath.toDegrees(earthPoint.getLongitude()));
    }

    @Override
    public void sendLowLevelCommands(List<Vector3D> commandList) throws TException {
        // TODO: Implement commands from Alex model
    }

    @Override
    public void sendHighLevelCommand(int command) throws TException {
        System.out.println("Changing formation to " + command + " after 90 steps.");
        ScienceState.getInstance().formationType = 0;
        newFormationType = command;
        stepsBeforeChange = 90;
    }
}
