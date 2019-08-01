package seakers.formationsimulator.offline;

import com.google.gson.Gson;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.gce.geotiff.GeoTiffReader;
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
import org.orekit.propagation.SpacecraftState;
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
import seakers.orekit.event.detector.FOVDetector;
import seakers.orekit.object.*;
import seakers.orekit.object.communications.ReceiverAntenna;
import seakers.orekit.object.communications.TransmitterAntenna;
import seakers.orekit.object.fieldofview.NadirRectangularFOV;
import seakers.orekit.propagation.PropagatorFactory;
import seakers.orekit.propagation.PropagatorType;
import seakers.orekit.util.Orbits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

public class OfflineSimulator {
    public static void main(String[] args) {
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

        TimeScale utc = TimeScalesFactory.getUTC();
        AbsoluteDate startDate = new AbsoluteDate(2021, 12, 1, 18, 00, 00.000, utc);
        AbsoluteDate endDate = new AbsoluteDate(2021, 12, 15, 18, 00, 00.000, utc);
        double mu = Constants.WGS84_EARTH_MU; // gravitation coefficient

        // must use IERS_2003 and EME2000 frames to be consistent with STK
        Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2003, true);
        Frame inertialFrame = FramesFactory.getEME2000();

        BodyShape earthShape = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
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

        Constellation formation = new Constellation("forest-sar", satellites);

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
                        DirectPosition lnglat = coverage.getGridGeometry().gridToWorld(pointGrid);
                        GeodeticPoint orekit_latlng = new GeodeticPoint(
                                FastMath.toRadians(lnglat.getOrdinate(1)),
                                FastMath.toRadians(lnglat.getOrdinate(0)),
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
        covDef1.assignConstellation(formation);
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

        PropagatorFactory pf = new PropagatorFactory(PropagatorType.J2, propertiesPropagator);
        Propagator propagator = pf.createPropagator(orb1, 100);

        // add all forest points to be analyzed
        double fovStepSize = sat1.getOrbit().getKeplerianPeriod() / 1000.;
        double threshold = 1e-3;
        for (CoveragePoint point: covDef1.getPoints()) {
            FOVDetector fovDetec = new FOVDetector(point, view1)
                    .withMaxCheck(fovStepSize)
                    .withThreshold(threshold)
                    .withHandler(new ForestHandler(true));

            propagator.addEventDetector(fovDetec);
        }

        boolean done = false;
        int steps = 0;
        SpacecraftState currentState = propagator.getInitialState();

        SimulationInformation simulationInformation = new SimulationInformation();
        simulationInformation.timesteps = new ArrayList<>();

        // Extrapolation loop
        double stepT = 30.;
        for (AbsoluteDate extrapDate = startDate;
             extrapDate.compareTo(endDate) <= 0;
             extrapDate = extrapDate.shiftedBy(stepT))  {

            ScienceState.getInstance().scienceAtStep = new ArrayList<>();

            if (extrapDate.compareTo(endDate) <= 0) {
                currentState = propagator.propagate(extrapDate);
                steps++;
                extrapDate = extrapDate.shiftedBy(stepT);

                if (steps % 500 == 0) {
                    System.out.println("step " + steps);
                    System.out.println(" time : " + currentState.getDate());
                    System.out.println(" " + currentState.getOrbit());
                }

                PVCoordinates currentCoords = currentState.getPVCoordinates();
                org.hipparchus.geometry.euclidean.threed.Vector3D currentPos = currentCoords.getPosition();
                GeodeticPoint earthPoint = earthShape.transform(currentPos, inertialFrame, currentState.getDate());

                TimestepInformation timestepInformation = new TimestepInformation();
                timestepInformation.visitedPoints = new ArrayList<>(ScienceState.getInstance().scienceAtStep);
                timestepInformation.date = currentState.getDate();
                timestepInformation.groundTrack = earthPoint;

                simulationInformation.timesteps.add(timestepInformation);
            }
            else {
                done = true;
            }

        }

        Gson gson = new Gson();
        String outputPath = "/Users/anmartin/Projects/FormationSimulation/fastsimulation.json";

        try {
            FileWriter writer = new FileWriter(outputPath);
            gson.toJson(simulationInformation, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
