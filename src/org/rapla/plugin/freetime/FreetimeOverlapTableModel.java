
package org.rapla.plugin.freetime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.entities.Named;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentFormater;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;

public class FreetimeOverlapTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    Appointment[] appointments;
    FreetimeCalculator[] freetimeCalculators;
    String[] columnNames;
    AppointmentFormater appointmentFormater;
    I18nBundle i18n;
    RaplaLocale loc;
    /**
     * 
     * @param serviceManager
     * @param onFreetime
     * @param i18nBundle 
     * @throws RaplaException
     */
    public FreetimeOverlapTableModel(RaplaContext serviceManager,HashMap<FreetimeCalculator,Appointment> onFreetime, I18nBundle i18n) throws RaplaException
    {
        Set<FreetimeCalculator> fcSet = onFreetime.keySet();
        this.freetimeCalculators = new FreetimeCalculator[fcSet.size()];
        Iterator<FreetimeCalculator> it = fcSet.iterator();
        int counter = 0;
        while(it.hasNext()){
        	this.freetimeCalculators[counter] = it.next();
        	counter++;
        }
        Collection<Appointment> appointmentCollection = onFreetime.values();
        this.appointments = new Appointment[appointmentCollection.size()];
        Iterator<Appointment> it2 = appointmentCollection.iterator();
        counter = 0;
        while(it2.hasNext()){
        	this.appointments[counter] = it2.next();
        	counter++;
        }

        this.i18n = i18n;
        appointmentFormater = (AppointmentFormater) serviceManager.lookup(AppointmentFormater.ROLE);
        loc = (RaplaLocale) serviceManager.lookup(RaplaLocale.ROLE);
        
        columnNames = new String[]
            {
        		i18n.getString("date")
                , i18n.getString("holidays")
            };
        
    }
    /**
     * @param named
     * @return
     */
    private String getName(Named named) {
        return named.getName(i18n.getLocale());
    }
    public String getColumnName(int c)
    {
       return columnNames[c];
    }
    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return appointments.length;
    }

    public Object getValueAt(int r, int c)
    {
        switch (c) {

        case 0: return(loc.formatDate(this.freetimeCalculators[r].getLastFoundHoliday().getAppointments()[0].getStart()));
        case 1: return(getName(this.freetimeCalculators[r].getLastFoundHoliday()));
        
         }
        return null;
    }



}









