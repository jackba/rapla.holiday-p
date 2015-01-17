package org.rapla.plugin.freetime;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.rapla.entities.domain.internal.AppointmentImpl;
import org.rapla.framework.TypedComponentRole;
import org.rapla.rest.gwtjsonrpc.common.ResultType;

@WebService
public interface FreetimeServiceRemote {
    TypedComponentRole<String> LAST_FREETIME_CHANGE = new TypedComponentRole<String>("org.rapla.plugin.freetimeChange");
    
    @ResultType(value=Holiday.class,container=List.class)
    List<Holiday> getHolidays(Date from, Date till);
    @ResultType(value=Holiday.class,container=List.class)
    List<Holiday> getHolidayConflicts(List<AppointmentImpl> appointments);

    public class Holiday
    {
    	public Date date;
    	public String name;
    }
}
