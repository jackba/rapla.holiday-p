package org.rapla.plugin.freetime.server;

import org.rapla.server.RemoteMethodFactory;
import org.rapla.server.RemoteSession;
import org.rapla.server.ServerExtension;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rku
 * Date: 25.05.13
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class FreetimeServiceRemoteObject implements FreetimeServiceRemote, RemoteMethodFactory<FreetimeServiceRemote> {
    @Override
    public String getHoliday(Date date) {
        //todo: this has to be very performant

        if (hasHoliday(date, date))
            return HolidayCache.getInstance().getHolidayNames(date);
        else
            return null;
    }

    @Override
    public boolean hasHoliday(Date from, Date till) {
        return HolidayCache.getInstance().hasHoliday(from, till);
    }

    @Override
    public FreetimeServiceRemote createService(RemoteSession remoteSession) {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
