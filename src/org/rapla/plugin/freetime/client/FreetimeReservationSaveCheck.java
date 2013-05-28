package org.rapla.plugin.freetime.client;

import java.awt.Component;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rapla.components.layout.TableLayout;
import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.RaplaGUIComponent;
import org.rapla.gui.ReservationCheck;
import org.rapla.gui.toolkit.DialogUI;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.server.FreetimeServiceRemote;


public class FreetimeReservationSaveCheck extends RaplaGUIComponent implements ReservationCheck {

    public FreetimeReservationSaveCheck(RaplaContext context) {
        super(context);
        setChildBundleName(FreetimePlugin.RESOURCE_FILE);
    }
    protected Map<Date, String> toMap(String[][] holidays) {
		Map<Date,String> map = new TreeMap<Date, String>();
		SerializableDateTimeFormat dateParser = new SerializableDateTimeFormat( getRaplaLocale().createCalendar());
		for (String[] holiday:holidays)
		{
			String dateString = holiday[0];
			try {
				Date date = dateParser.parseDate(dateString,false);
				String name = holiday[1];
				map.put( date, name);
			} catch (ParseException e) {
				getLogger().warn("Can't parse date of holiday " + dateString + " Ignoring." );
			}
		}
		return map;
	}
    
    public boolean check(Reservation reservation, Component sourceComponent) throws RaplaException {
        Appointment[] appointments = reservation.getAppointments();
        FreetimeServiceRemote webservice = getWebservice(FreetimeServiceRemote.class);
        String[][] holidays = webservice.getHolidayConflicts(appointments);
        Map<Date, String> holidayMap = toMap(holidays);
        boolean result = true;
        // HashMap Length > 0 => At least one Appointment overlaps with freetime
        if(!holidayMap.isEmpty()){
            // Analog zu Konflikten Dialog aufbauen
            JPanel contentFreetime = new JPanel();
            contentFreetime.setLayout(new TableLayout(new double[][] {
                {TableLayout.FILL}
                ,{TableLayout.PREFERRED,TableLayout.PREFERRED,TableLayout.PREFERRED,2,TableLayout.FILL}
            }));
            JLabel warningLabel = new JLabel();
            warningLabel.setText(getString("infoOverlapHolidays"));
            //warningLabel.setForeground(java.awt.Color.red);
            contentFreetime.add(warningLabel,"0,1");
           
            FreetimeConflictUI freetimeConflicts = new FreetimeConflictUI(getContext(),holidayMap);
            contentFreetime.add(freetimeConflicts.getComponent(),"0,2");
            //todo: i18n
            DialogUI dialog = DialogUI.create(
                    getContext()
                    ,sourceComponent
                        ,true
                        ,contentFreetime
                        ,new String[] {
                                getString("save")
                    //todo: ausnahmen einfügen
                                ,getString("back")
                                
                        }
            );
            dialog.setDefault(1);
            dialog.getButton(0).setIcon(getIcon("icon.save"));
           // dialog.getButton(1).setIcon(getIcon("icon.remove"));
            dialog.getButton(1).setIcon(getIcon("icon.cancel"));
            //dialog.getButton(2).setIcon(getIcon("icon.cancel"));
            dialog.setTitle(getString("warning"));
            dialog.start();
            result = dialog.getSelectedIndex() == 0;
           /* if(dialog.getSelectedIndex()  == 1){
                // delete Appointments
                Set<FreetimeCalculator> fcSet = onFreetime.keySet();
                Iterator<FreetimeCalculator> it = fcSet.iterator();
                while(it.hasNext()){
                    FreetimeCalculator tempCalc = it.next();
                    Appointment temp = onFreetime.get(tempCalc);
                    if(temp.isRepeatingEnabled()){
                        // A Holiday has always exactly one Appointment
                        //todo: is this correct???
                        Date startHoliday = tempCalc.getLastFoundHoliday().getAppointments()[0].getStart();
                        Calendar calendarExceptionTime = createCalendar();
                        calendarExceptionTime.setTime(startHoliday);
                        calendarExceptionTime.set(Calendar.HOUR_OF_DAY,0);
                        calendarExceptionTime.set(Calendar.MINUTE, 0);
                        calendarExceptionTime.set(Calendar.SECOND, 0);
                        Repeating tempRepeat = temp.getRepeating();
                        tempRepeat.addException(calendarExceptionTime.getTime());
                    }else{
                        Reservation tempReservation = temp.getReservation();
                        //todo: what happens if there is only one appointment --> bug
                        //how to handle if reservation is not yet added
                        //modify mutable reservation instead and go back to ui
                        reservation.removeAppointment(temp);
                        //tempReservation.removeAppointment(temp);
                    }
                }
            }     */
        }
        return result;
    }

    public Calendar createCalendar() {
        return getRaplaLocale().createCalendar();
    }
}
