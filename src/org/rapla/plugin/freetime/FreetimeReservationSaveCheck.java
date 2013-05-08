package org.rapla.plugin.freetime;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rapla.components.layout.TableLayout;
import org.rapla.components.util.DateTools;
import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.AppointmentBlock;
import org.rapla.entities.domain.Reservation;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.RaplaGUIComponent;
import org.rapla.gui.ReservationCheck;
import org.rapla.gui.toolkit.DialogUI;


public class FreetimeReservationSaveCheck extends RaplaGUIComponent implements ReservationCheck {

    public FreetimeReservationSaveCheck(RaplaContext context) {
        super(context);
        setChildBundleName(FreetimePlugin.RESOURCE_FILE);
    }

    public boolean check(Reservation reservation, Component sourceComponent) throws RaplaException {
        Appointment[] appointments = reservation.getAppointments();
        HashMap<FreetimeCalculator,Appointment> onFreetime = new HashMap<FreetimeCalculator,Appointment>();
        for(int i=0;i<appointments.length;i++){
            
            Appointment appointment = appointments[i];
            Date start  = appointment.getStart();
            Collection<AppointmentBlock> blocks = new ArrayList<AppointmentBlock>();
            //todo: why two years?
            appointment.createBlocks(start, DateTools.addDays( start, 366 * 2), blocks);
            for ( AppointmentBlock block: blocks)
            {
                Date blockStart = new Date(block.getStart());
                Date blockEnd = new Date(block.getEnd());
                FreetimeCalculator fc = new FreetimeCalculator(blockStart,blockEnd,getQuery(), getRaplaLocale());
                if(fc.isFreetime()){
                    onFreetime.put( fc,appointments[i]);
                }
            }
        }
        boolean result = false;
        // HashMap Length > 0 => At least one Appointment overlaps with freetime
        if(!onFreetime.isEmpty()){
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
            
            ConflictInfoOldUI freetimeConflicts = new ConflictInfoOldUI();
            FreetimeOverlapTableModel model = new FreetimeOverlapTableModel(getContext(),onFreetime, getI18n());
            freetimeConflicts.getTable().setModel(model);
            contentFreetime.add(freetimeConflicts.getComponent(),"0,2");
            //todo: i18n
            DialogUI dialog = DialogUI.create(
                    getContext()
                    ,sourceComponent
                        ,true
                        ,contentFreetime
                        ,new String[] {
                                getString("save")
                            //    ,"Ausnahmen in Serien hinzufügen / Einzeltermine löschen?"
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
