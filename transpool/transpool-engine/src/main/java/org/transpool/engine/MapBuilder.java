package org.transpool.engine;

import org.transpool.engine.ds.MapDb;
import org.transpool.engine.ds.Time;
import org.transpool.engine.ds.TranspoolTrip;
import org.transpool.engine.ds.schema.Path;
import org.transpool.engine.ds.schema.Stop;
import org.transpool.engine.ds.schema.TransPool;
import org.transpool.engine.ds.schema.TransPoolTrip;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBuilder {
    private final static String PATH_GENER_CLASS = "org.transpool.engine.ds.schema";
    private MapDb map;
    private String description;

    public MapDb getMapDb() {
        return map;
    }

    public String getDescription() {
        return description;
    }


    private TransPool xmlToObj(InputStream inputStream) throws JAXBException, IOException {
            JAXBContext jc = JAXBContext.newInstance(PATH_GENER_CLASS);
            Unmarshaller u = jc.createUnmarshaller();
            return (TransPool) u.unmarshal(inputStream);

    }

    private boolean setMap(int width, int length) {
        if (!MapDb.checkBoundaries(width, length)) {
            description = "map boundaries are not between 6 to 100";
            return false;
        }
        map = new MapDb(width, length);
        return true;
    }

    private boolean setStops(List<Stop> stops) {

        for (Stop stop : stops) {

            description = map.addStop(stop.getName().trim(), stop.getX(), stop.getY());
            if (description != null)
                return false;
        }
        return true;
    }

    private boolean setPaths(List<Path> paths) {

        for (Path path : paths) {

            if (!map.addPath(path.getTo().trim(), path.getFrom().trim(), path.isOneWay(), path.getLength(), path.getFuelConsumption(), path.getSpeedLimit())) {
                description = "invalid path";
                return false;
            }
        }
        return true;
    }


    public boolean build(InputStream inputStream) throws JAXBException, IOException {
        TransPool transPool = xmlToObj(inputStream);

        if (!setMap(transPool.getMapDescriptor().getMapBoundries().getWidth(), transPool.getMapDescriptor().getMapBoundries().getLength()))
            return false;

        if (!setStops(transPool.getMapDescriptor().getStops().getStop()))
            return false;

        if (!setPaths(transPool.getMapDescriptor().getPaths().getPath()))
            return false;

        return true;
    }

}
