package org.rapla.plugin.freetime;

import java.util.Date;

import org.rapla.entities.domain.Appointment;

/**
 * Created with IntelliJ IDEA.
 * User: rku
 * Date: 25.05.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public interface FreetimeServiceRemote {
    String[][] getHolidays(Date from, Date till);
    long getHolidayRepositoryVersion();
    String[][] getHolidayConflicts(Appointment[] appointments);
}
