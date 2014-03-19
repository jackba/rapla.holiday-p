package org.rapla.plugin.freetime;

import java.util.Date;

import javax.jws.WebService;

import org.rapla.entities.domain.Appointment;

@WebService
public interface FreetimeServiceRemote {
    String[][] getHolidays(Date from, Date till);
    long getHolidayRepositoryVersion();
    String[][] getHolidayConflicts(Appointment[] appointments);
}
