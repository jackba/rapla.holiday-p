package org.rapla.plugin.freetime.server;

import org.rapla.entities.RaplaObject;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ModificationEvent;
import org.rapla.facade.ModificationListener;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.server.ServerExtension;

import java.util.*;


public class FreetimeCache extends RaplaComponent implements ModificationListener, ServerExtension {

    private static FreetimeCache instance;
    private static final SortedMap<Date, Reservation> cache = new TreeMap<Date, Reservation>();

    public FreetimeCache(RaplaContext context) {
        super(context);
        getClientFacade().addModificationListener(this);
        try {
            updateAll();
        } catch (RaplaException e) {
            getLogger().error(e.getMessage(), e);
        }
        instance = this;
    }

    private void updateAll() throws RaplaException {
        cache.clear();

        final DynamicType freetimeType = getClientFacade().getDynamicType(FreetimePlugin.FREETIME_RESOURCETYPE);
        if (freetimeType != null) {


            final List<ClassificationFilter> filters = new ArrayList<ClassificationFilter>();
            filters.add(freetimeType.newClassificationFilter());
            final Allocatable[] list = getClientFacade().getAllocatables(filters.toArray(ClassificationFilter.CLASSIFICATIONFILTER_ARRAY));
            Allocatable freetime = null;
            for (Allocatable aList : list) {
                if (aList.getName(Locale.getDefault()).equalsIgnoreCase(FreetimePlugin.FREETIME_RESOURCE)) {
                    freetime = aList;
                    break;
                }
            }
            if (freetime != null) {
                Allocatable[] filter = {freetime};
                Reservation[] found = getClientFacade().getReservations(filter, null, null);
                for (Reservation reservation : found) {
                    cache.put(removeTime(reservation.getFirstDate()), reservation);
                }
            }
        }
    }

    private Date removeTime(Date date) {
        final Calendar calendar = getRaplaLocale().createCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();  //To change body of created methods use File | Settings | File Templates.
    }

    public static FreetimeCache getInstance() {
        return instance;
    }

    @Override
    public void dataChanged(ModificationEvent evt) throws RaplaException {
        //check if reservation of type FreetimeResourceType
        for (RaplaObject raplaObject : evt.getChanged()) {
            if (raplaObject instanceof Reservation) {
                if (((Reservation) raplaObject).getClassification().getType().getName(getLocale()).equals(FreetimePlugin.FREETIME_RESOURCETYPE) &&
                ((Reservation) raplaObject).getName(getLocale()).equals(FreetimePlugin.FREETIME_RESOURCE)) {
                    //correct type and resource
                    try {
                        updateAll();
                    } catch (RaplaException e) {
                        getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInvokedOnAWTEventQueue() {
        //todo: check?, true since called by client gui?
        return true;
    }

    public Reservation getFreetime(Date date) {
        return cache.get(removeTime(date));
    }


    public List<Reservation> getFreetime(Date from, Date till) {
        return  new ArrayList<Reservation>(cache.subMap(removeTime(from),removeTime(till)).values());

    }

    public String getFreetimeName(Date date) {
        final Reservation freetime = getFreetime(date);
        return freetime != null ? freetime.getName(getLocale()) : null;
    }

    public List<String> getFreetimeNames(Date from, Date till) {
        final Collection<Reservation> values = cache.subMap(
                removeTime(from), removeTime(till)).values();
        List<String> result = new ArrayList<String>();
        for (Reservation value : values) {
            result.add(value.getName(getLocale()));
        }
        return  result;

    }
}
