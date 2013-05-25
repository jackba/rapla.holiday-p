/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas                                  |
 |                                                                          |
 | This program is free software; you can redistribute it and/or modify     |
 | it under the terms of the GNU General Public License as published by the |
 | Free Software Foundation. A copy of the license has been included with   |
 | these distribution in the COPYING file, if not go to www.fsf.org         |
 |                                                                          |
 | As a special exception, you are granted the permissions to link this     |
 | program with every library, which license fulfills the Open Source       |
 | Definition as published by the Open Source Initiative (OSI).             |
 *--------------------------------------------------------------------------*/

package org.rapla.plugin.freetime.client;

import java.awt.Color;

import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.internal.RaplaDateRenderer;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.server.FreetimeServiceRemote;

/** Renders holidays in a special color. */
public class FreetimeHighlightRenderer extends RaplaDateRenderer {

	//Performance boost
	private boolean lastSet = false;
	private int lastDay;
	private int lastMonth;
	private int lastYear;
	private boolean lastResult;
	//Performance boost variables end
	
	/**
	 * 
	 * @param context
	 * @throws RaplaException
	 */
	public FreetimeHighlightRenderer(RaplaContext context) throws RaplaException {
		super(context);
	}

	/**
	 * returns the color if day is set for highlight and null if not.
	 */
	public RenderingInfo getRenderingInfo(int dayOfWeek, int day, int month, int year) {
	    /*FreetimeCalculator fc = new FreetimeCalculator(day, month, year, this.getQuery(), getRaplaLocale());
		boolean compare;
		if(this.isOldResult(day, month, year)){
			compare = this.lastResult;
		}else{
			compare = fc.isFreetime();
			this.lastDay = day;
			this.lastMonth = month;
			this.lastYear = year;
			this.lastSet = true;
			this.lastResult = compare;
		}*/

        RenderingInfo renderingInfo = super.getRenderingInfo(dayOfWeek, day, month, year);
        final String holidayNames;
        try {
            holidayNames = getWebservice(FreetimeServiceRemote.class).getHoliday(
                    getRaplaLocale().toDate(year, month, day)
            );
            if (holidayNames != null){
                //FreetimeMapper fm = new FreetimeMapper();
                //CalendarOptions calenderOptions = this.getCalendarOptions();
                int configValue = 0;
                Color backgroundColor = FreetimePlugin.BACKGROUND_COLOR; //fm.getFreetimeColorBackground(configValue);
                Color foregroundColor = FreetimePlugin.FOREGROUND_COLOR;//fm.getForegroundColor(configValue);
                String tooltipText = holidayNames;
                //String tooltipText = fc.getFreetimeName();
//          CalendarOptions calenderOptions = this.getCalendarOptions();
                renderingInfo = new RenderingInfo(backgroundColor, foregroundColor, tooltipText);
            }
        } catch (RaplaException e) {

        }
        return renderingInfo;
	}
	
	// performance boost
	private boolean isOldResult(int day,int month, int year){
		if(!this.lastSet){
			return false;
		}else{
			if(this.lastDay == day && this.lastMonth == month && this.lastYear == year){
				return true;
			}else{
				return false;
			}
		}
	}
}
