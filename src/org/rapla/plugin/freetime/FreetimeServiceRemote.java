package org.rapla.plugin.freetime;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.rapla.entities.domain.internal.AppointmentImpl;
import org.rapla.rest.gwtjsonrpc.common.ResultType;

@WebService
public interface FreetimeServiceRemote {
    @ResultType(value=Holiday.class,container=List.class)
    List<Holiday> getHolidays(Date from, Date till);
    long getHolidayRepositoryVersion();
    @ResultType(value=Holiday.class,container=List.class)
    List<Holiday> getHolidayConflicts(List<AppointmentImpl> appointments);

    public class Holiday
    {
    	public Date date;
    	public String name;
    }
}
