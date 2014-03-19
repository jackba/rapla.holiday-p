package org.rapla.plugin.freetime;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.rapla.entities.domain.internal.AppointmentImpl;

@WebService
public interface FreetimeServiceRemote {
    HolidayMap getHolidays(Date from, Date till);
    long getHolidayRepositoryVersion();

    HolidayMap getHolidayConflicts(List<AppointmentImpl> appointments);
    public class HolidayMap
    {
    	Map<Date, String> map;
    	public HolidayMap(Map<Date, String> map) {
    		this.map = map;
		}
    	HolidayMap() {
		}
    	public Map<Date, String> get() 
    	{
			return map;
		}
    }
}
