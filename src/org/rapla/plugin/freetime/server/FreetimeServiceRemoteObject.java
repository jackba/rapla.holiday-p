package org.rapla.plugin.freetime.server;

import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.domain.internal.ReservationImpl;
import org.rapla.entities.storage.internal.SimpleIdentifier;
import org.rapla.server.RemoteMethodFactory;
import org.rapla.server.RemoteSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class FreetimeServiceRemoteObject implements FreetimeServiceRemote, RemoteMethodFactory<FreetimeServiceRemote> {
    @Override
    public String getFreetimeName(Date date) {
        return FreetimeCache.getInstance().getFreetimeName(date);
    }

    @Override
    public String [] getFreetimeNames(Date from, Date till) {
        List<String> freetimeNames = FreetimeCache.getInstance().getFreetimeNames(from, till);
        return freetimeNames.toArray(new String[freetimeNames.size()]);
    }

    @Override
    public FreetimeServiceRemote createService(RemoteSession remoteSession) {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
