package org.rapla.plugin.freetime.server;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.rapla.components.util.DateTools;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.facade.AllocationChangeEvent;
import org.rapla.facade.AllocationChangeListener;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.server.RemoteMethodFactory;
import org.rapla.server.RemoteSession;

public class FreetimeService extends RaplaComponent implements AllocationChangeListener, FreetimeServiceRemote, RemoteMethodFactory<FreetimeServiceRemote> {

    private SortedMap<Date, String> cache = Collections.synchronizedSortedMap(new TreeMap<Date, String>());
    Allocatable freetime = null;
    long repositoryVersion = 0;

    public FreetimeService(RaplaContext context, Configuration config) throws RaplaException {
        super(context);
        getClientFacade().addAllocationChangedListener(this);
        updateHolidays();
    }

    private void updateHolidays() throws RaplaException {
        synchronized (cache) {
         	cache.clear();
            final Allocatable[] list = getClientFacade().getAllocatables(null);
            for (Allocatable aList : list) {
                if (aList.getName(Locale.getDefault()).equalsIgnoreCase(FreetimePlugin.DEFAULT_FREETIME_RESOURCE)) {
                    freetime = aList;
                    break;
                }
            }
            if (freetime != null) {
                Allocatable[] filter = {freetime};
                Reservation[] foundHolidays = getClientFacade().getReservations(filter, null, null);
                for (Reservation foundHoliday : foundHolidays) {
                    cache.put(foundHoliday.getFirstDate(), foundHoliday.getName(getRaplaLocale().getLocale()));
                }
            }
            repositoryVersion++;
		}
    }
    
    public String[][] getHolidayConflicts(Appointment[] appointments)
    {
    	SortedMap<Date, String> map = new TreeMap<Date, String>();
    	synchronized (cache) {
    		for ( Appointment appointment: appointments)
    		{
    			Date start = appointment.getStart();
    			Date maxEnd = appointment.getMaxEnd();
    			SortedMap<Date, String> tailMap = cache.tailMap(DateTools.subDay(start));
    			for (Date date: tailMap.keySet())
    			{
    				if ( maxEnd != null && date.after( maxEnd))
    				{
    					break;
    				}
    				if (appointment.overlaps(DateTools.cutDate(date), DateTools.fillDate(date)))
    				{
    					map.put( date,  tailMap.get( date));
    				}
    			}
    		}
    	}
		String[][] result = serialize(map);
    	return result;
    } 
    
    public String[][] getHolidays(Date from, Date till) 
    {
		synchronized (cache) {
			SortedMap<Date, String> map = cache;
			String[][] result = serialize(map);
			return result;
		}
    }

	protected String[][] serialize(SortedMap<Date, String> map) {
		SerializableDateTimeFormat dateParser = new SerializableDateTimeFormat( getRaplaLocale().createCalendar());
		Set<Date> keySet = map.keySet();
		String[][] result = new String[keySet.size()][2]; 
		int i=0;
		for (Date date:keySet)
		{
			result[i] = new String[2];
			result[i][0] = dateParser.formatDate(date);
			String name = map.get( date);
			String remoteServiceSaveName = name.replaceAll("[\\{,\\},\\,]", "");
			result[i][1] = remoteServiceSaveName;
			i++;
		}
		
		return result;
	}

	public void changed(AllocationChangeEvent[] changeEvents) {
		boolean update = false;
		for ( AllocationChangeEvent event: changeEvents)
		{
			Allocatable allocatable = event.getAllocatable();
			if ( allocatable != null && allocatable.equals( freetime))
			{
				update = true;
			}
		}
		if ( update)
		{
			try {
				updateHolidays();
			} catch (RaplaException e) {
				getLogger().error(e.getMessage(), e);
			}
		}
	}

	public long getHolidayRepositoryVersion() 
	{
		return repositoryVersion;
	}
	
    public FreetimeServiceRemote createService(RemoteSession remoteSession) {
        return this;  
    }
}
