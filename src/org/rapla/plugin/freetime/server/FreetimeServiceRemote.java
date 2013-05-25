package org.rapla.plugin.freetime.server;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rku
 * Date: 25.05.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public interface FreetimeServiceRemote {
    /**
     * returns true if given date is holiday
     * @param date Date to be checked
     * @return string containing holiday names, null if there is no holiday today
     */
    public String getHoliday (Date date);

    /**
     * checks whether a holiday is in the given time frame
     * @param from
     * @param till
     * @return
     */
    public boolean hasHoliday (Date from, Date till);
}
