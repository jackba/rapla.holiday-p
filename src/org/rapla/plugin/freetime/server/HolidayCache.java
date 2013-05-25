package org.rapla.plugin.freetime.server;

import org.rapla.entities.RaplaObject;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.ModificationEvent;
import org.rapla.facade.ModificationListener;
import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.plugin.freetime.FreetimePlugin;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rku
 * Date: 25.05.13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class HolidayCache extends RaplaComponent implements ModificationListener {

    private static HolidayCache instance;
    private static SortedMap<Date, String> cache = new TreeMap<Date, String>();

    private HolidayCache(RaplaContext context) {
        super(context);
        getClientFacade().addModificationListener(this);
    }

    public static void initialize(RaplaContext context) {
        instance = new HolidayCache(context);
        try {
            instance.updateHolidays();
        } catch (RaplaException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void updateHolidays() throws RaplaException {
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
                Reservation[] foundHolidays = getClientFacade().getReservations(filter, null, null);
                for (Reservation foundHoliday : foundHolidays) {
                    cache.put(foundHoliday.getFirstDate(), foundHoliday.getName(getRaplaLocale().getLocale()));
                }
            }
        }
    }

    public static HolidayCache getInstance() {
        return instance;
    }

    @Override
    public void dataChanged(ModificationEvent evt) throws RaplaException {
        //todo: update cache if is nescessary    +
        //check if reservation of type FreetimeResourceType
        if (false)
            updateCache(evt.getChanged());

    }

    private void updateCache(Set<RaplaObject> changed) {
        //update holiday cache
    }

    @Override
    public boolean isInvokedOnAWTEventQueue() {
        //todo: check?, true since called by client gui?
        return true;
    }

    public String getHolidayNames(Date date) {
        return cache.get(date);
    }

    public boolean hasHoliday(Date from, Date till) {
        // do this efficiently cache.headMap()
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
