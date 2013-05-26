package org.rapla.plugin.freetime.server;

import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.storage.internal.SimpleIdentifier;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface FreetimeServiceRemote {
    /**
     * returns true if given date is holiday
     * @param date Date to be checked
     * @return string containing holiday names, null if there is no holiday today
     */
    public String getFreetimeName(Date date);


    /**
     * get Holidays from
     *
     * @param from
     * @param till
     * @return
     */
    public String [] getFreetimeNames(Date from, Date till);
}
