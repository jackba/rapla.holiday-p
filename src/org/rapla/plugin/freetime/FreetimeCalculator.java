package org.rapla.plugin.freetime;

import java.util.*;

import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.dynamictype.ClassificationFilter;
import org.rapla.entities.dynamictype.DynamicType;
import org.rapla.facade.QueryModule;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;

public class FreetimeCalculator{
	
	//DateMode
	private int day;
	private int month;
	private int year;
	
	//PeriodMode
	private Date from;
	private Date till;
	
	private QueryModule qm;
	private Reservation[] lastfound;
	private boolean called = false; // performance
	private String mode = "UNDEFINED";
	
	private static final String DATE_MODE = "DATE_MODE";
	
	private RaplaLocale raplaLocale;
	/**
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @param qm
	 * @param raplaLocale
	 */
	public FreetimeCalculator(int day, int month, int year,QueryModule qm, RaplaLocale raplaLocale){
		this.day = day;
		this.month = month;
		this.year = year;
		this.qm = qm;
		this.mode = FreetimeCalculator.DATE_MODE;
		this.raplaLocale = raplaLocale;
	}
	/**
	 * 
	 * @param from
	 * @param till
	 * @param qm
	 */
	public FreetimeCalculator(Date from,Date till, QueryModule qm, RaplaLocale raplaLocale){
		this.from = from;
		this.till = till;
		this.qm = qm;
		this.raplaLocale =raplaLocale;
		
	}
	/**
	 * Checks for holiday time for the give Date or period in constructor
	 * @return
	 */
	public boolean isFreetime(){
		this.called = true;
		try {
			//this.Query
            //todo: very inefficient
            //todo: make configurable
            DynamicType freetimeType = this.qm.getDynamicType("freetimeType");
            if (freetimeType == null) {
                this.lastfound = null;
                return false;
            }
            List<ClassificationFilter> filters = new ArrayList<ClassificationFilter>();
            filters.add(freetimeType.newClassificationFilter());
            Allocatable[] list = qm.getAllocatables(filters.toArray(ClassificationFilter.CLASSIFICATIONFILTER_ARRAY));
			Allocatable freetime = null;
			for(int i =0;i<list.length;i++){
                //todo: adapt to be configurable
				if(list[i].getName(Locale.getDefault()).equalsIgnoreCase("freetime")){
					freetime = list[i];
				}
			}
			Allocatable[] filter = {freetime};
			if(this.mode.equals(FreetimeCalculator.DATE_MODE)){
				Calendar calendar = raplaLocale.createCalendar();
				calendar.set(Calendar.DATE,this.day);
		        calendar.set(Calendar.MONTH,this.month);
		        calendar.set(Calendar.YEAR,this.year);
		        calendar.set(Calendar.HOUR_OF_DAY,0);
		        calendar.set(Calendar.MINUTE,0);
		        calendar.set(Calendar.SECOND,0);
		        calendar.set(Calendar.MILLISECOND,0);
		        
		        Calendar calendar2 = raplaLocale.createCalendar();
				calendar2.set(Calendar.DATE,this.day);
		        calendar2.set(Calendar.MONTH,this.month);
		        calendar2.set(Calendar.YEAR,this.year);
		        calendar2.set(Calendar.HOUR_OF_DAY,23);
		        calendar2.set(Calendar.MINUTE,58);
		        calendar2.set(Calendar.SECOND,58);
		        calendar2.set(Calendar.MILLISECOND,0);
		        
		        this.from = calendar.getTime();
		        this.till = calendar2.getTime();
			}
            //todo: very inefficient ... implement a server function isHoliday() which caches requests ...
            //todo:  observes freetime resource to adapt changes to holiday
			Reservation[] foundHolidays = qm.getReservations(filter,this.from, this.till);
			if(foundHolidays.length == 0){
				this.lastfound = null;
				return false;
			}else{
				this.lastfound = foundHolidays; //Just one possible Holiday per day
				return true;
			}
			
		} catch (RaplaException e) {
			return false;
		}
	
	}
	/**
	 * Returns the name of the holiday, if there was a holiday found in the period or on date
	 * @return
	 */
	public String getFreetimeName(){
		Locale locale = raplaLocale.getLocale();
        if(this.called){
			return this.lastfound[0].getName(locale);
		}else{
			if(this.isFreetime()){
				return this.lastfound[0].getName(locale);
			}else{
				return null;
			}
		}
	}
	/**
	 * Returns the Reservation of the last found Holiday
	 * @return
	 */
	public Reservation getLastFoundHoliday(){
		if(this.called){
			return this.lastfound[0];
		}else{
			return null;
		}
	}
	


}
