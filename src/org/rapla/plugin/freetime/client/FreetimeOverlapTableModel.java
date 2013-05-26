
package org.rapla.plugin.freetime.client;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.entities.Named;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentFormater;
import org.rapla.entities.domain.Reservation;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;

public class FreetimeOverlapTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private final String [] freetimeList;

    private final String[] columnNames;
    private final RaplaLocale loc;

    /**
     * @param context
     * @param i18n
     * @throws RaplaException
     */
    public FreetimeOverlapTableModel(RaplaContext context, String []  freetimeList, I18nBundle i18n) throws RaplaException {

        loc = context.lookup(RaplaLocale.class);

        columnNames = new String[]
                {
                        i18n.getString("date")
                        , i18n.getString("holidays")
                };


        this.freetimeList = freetimeList;
    }

    public String getColumnName(int c) {
        return columnNames[c];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return freetimeList.length;
    }

    public Object getValueAt(int r, int c) {
        if (r > freetimeList.length) {
            return null;
        }
        final String reservation = freetimeList[r];

        switch (c) {
            case 0:
                return "";//(loc.formatDate(reservation.getFirstDate()));
            case 1:
                return (reservation);

        }
        return null;
    }


}









