package org.rapla.plugin.freetime.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.rapla.components.util.DateTools;
import org.rapla.entities.configuration.Preferences;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.domain.internal.AppointmentImpl;
import org.rapla.facade.AllocationChangeEvent;
import org.rapla.facade.AllocationChangeListener;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.TypedComponentRole;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.FreetimeServiceRemote;
import org.rapla.server.RemoteMethodFactory;
import org.rapla.server.RemoteSession;
import org.rapla.storage.impl.server.LocalAbstractCachableOperator;

public class FreetimeService extends RaplaComponent implements AllocationChangeListener, FreetimeServiceRemote, RemoteMethodFactory<FreetimeServiceRemote> {

    TypedComponentRole<String> LAST_FREETIME_CHANGE_HASH = new TypedComponentRole<String>("org.rapla.plugin.freetimeChangeHash");
    
    private SortedMap<Date, String> cache = Collections.synchronizedSortedMap(new TreeMap<Date, String>());
    Allocatable freetime = null;

    public FreetimeService(RaplaContext context, Configuration config) throws RaplaException {
        super(context);
        getClientFacade().addAllocationChangedListener(this);
        updateHolidays();
    }

    private void updateHolidays() throws RaplaException {
        String hash = getQuery().getSystemPreferences().getEntryAsString( LAST_FREETIME_CHANGE_HASH, null);
        StringBuilder hashableString = new StringBuilder();
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
                    Date firstDate = foundHoliday.getFirstDate();
                    String name = foundHoliday.getName(getRaplaLocale().getLocale());
                    cache.put(firstDate, name);
                    hashableString.append( firstDate.getTime());
                    hashableString.append( name);
                }
            }
		}
        String newHash = LocalAbstractCachableOperator.encrypt("sha-1",hashableString.toString());
        if ( hash == null || !newHash.equals(hash))
        {
            // only write if there were previous holidays
            if ( hash != null || !cache.isEmpty())
            {
                Preferences edit = getModification().edit( getQuery().getSystemPreferences());
                edit.putEntry(FreetimeServiceRemote.LAST_FREETIME_CHANGE, getRaplaLocale().getSerializableFormat().formatTimestamp( getClientFacade().getOperator().getCurrentTimestamp()));
                edit.putEntry(LAST_FREETIME_CHANGE_HASH, newHash);
                getModification().store( edit);
            }
        }

    }
    
    public List<Holiday> getHolidayConflicts(List<AppointmentImpl> appointments)
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
    	return makeList(map);
    } 
    
    public List<Holiday> getHolidays(Date from, Date till) 
    {
		synchronized (cache) {
			SortedMap<Date, String> map = cache;
			return makeList(map);
		}
    }

    private List<Holiday> makeList(SortedMap<Date, String> map) 
    {
        List<Holiday> list = new ArrayList<Holiday>();
        for ( Map.Entry<Date, String> entry:map.entrySet())
        {
            Holiday holiday = new Holiday();
            holiday.date = entry.getKey();
            holiday.name = entry.getValue();
            list.add( holiday);
        }
        return list;
    }

//	protected String[][] serialize(SortedMap<Date, String> map) {
//		SerializableDateTimeFormat dateParser = new SerializableDateTimeFormat( );
//		Set<Date> keySet = map.keySet();
//		String[][] result = new String[keySet.size()][2]; 
//		int i=0;
//		for (Date date:keySet)
//		{
//			result[i] = new String[2];
//			result[i][0] = dateParser.formatDate(date);
//			String name = map.get( date);
//			String remoteServiceSaveName = name.replaceAll("[\\{,\\},\\,]", "");
//			result[i][1] = remoteServiceSaveName;
//			i++;
//		}
//		
//		return result;
//	}

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

	public FreetimeServiceRemote createService(RemoteSession remoteSession) {
        return this;  
    }
}
