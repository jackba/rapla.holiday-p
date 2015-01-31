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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.rapla.components.calendar.DateRendererAdapter;
import org.rapla.components.util.DateTools;
import org.rapla.components.util.ParseDateException;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.framework.TypedComponentRole;
import org.rapla.gui.internal.RaplaDateRenderer;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.FreetimeServiceRemote;
import org.rapla.plugin.freetime.FreetimeServiceRemote.Holiday;

/** Renders holidays in a special color. */
public class FreetimeHighlightRenderer extends RaplaDateRenderer 
{
	Date holidayRepositoryVersion = null;
	private SortedMap<Date, String> cache = new TreeMap<Date, String>();
	Configuration config;
	
	/**
	 * 
	 * @param context
	 * @throws RaplaException
	 */
	FreetimeServiceRemote webservice;
	public FreetimeHighlightRenderer(RaplaContext context, Configuration config,FreetimeServiceRemote webservice) throws RaplaException {
		super(context);
		this.webservice = webservice;
		updateCache();
		this.config = config;
	}
	
	synchronized private void updateCache() throws RaplaException
	{
	    String entry = getQuery().getSystemPreferences().getEntryAsString( FreetimeServiceRemote.LAST_FREETIME_CHANGE, null);
	    Date lastChanged = null;
	    if (entry != null)
	    {
	        try {
                lastChanged = getRaplaLocale().getSerializableFormat().parseTimestamp( entry);
            } catch (ParseDateException e) {
                throw new RaplaException( e.getMessage() , e);
            }
	    }
        if ( holidayRepositoryVersion == null || (lastChanged != null && lastChanged.after(holidayRepositoryVersion))) 
	    {
	        
            // we set the cached time to the start in case anything happens during cast
	        Date start = null;
			Date end = null;
			cache.clear();
	        List<Holiday> list = webservice.getHolidays(start, end);
	        Iterator<Holiday> it = list.iterator();
	        while (it.hasNext())
	        {
	            Holiday holiday = it.next();
	            cache.put( DateTools.cutDate(holiday.date), holiday.name);
	        }
            if ( lastChanged == null)
            {
                holidayRepositoryVersion = getClientFacade().getOperator().getCurrentTimestamp();
            }
            else
            {
                holidayRepositoryVersion = lastChanged;
            }
	    }
   }

	/**
	 * returns the color if day is set for highlight and null if not.
	 */
	synchronized public RenderingInfo getRenderingInfo(int dayOfWeek, int day, int month, int year) {
        RenderingInfo renderingInfo = super.getRenderingInfo(dayOfWeek, day, month, year);
        try {
        	RaplaLocale raplaLocale = getRaplaLocale();
        	// if five seconds passed since last check, check again
        	updateCache();
        	
        	// We need to add a DateRendererAdapter hack to make the semantic change in RenderingInfo in 1.8 work for 1.7 as well 
			DateRendererAdapter dateRendererAdapter = new DateRendererAdapter( this, raplaLocale.getTimeZone(), raplaLocale.getLocale())
			{
				public RenderingInfo getRenderingInfo(Date date) {
		        	String holidayNames = cache.get( date);
		        	if (holidayNames != null){
		        		
		                Color backgroundColor = FreetimePlugin.DEFAULT_BACKGROUND_COLOR; 
		                Color foregroundColor = FreetimePlugin.DEFAULT_FOREGROUND_COLOR;
		                String tooltipText = holidayNames;
		                RenderingInfo renderingInfo = new RenderingInfo(backgroundColor, foregroundColor, tooltipText);
		                return renderingInfo;
		            }
		        	return null;
				}
			};
			RenderingInfo newInfo = dateRendererAdapter.getRenderingInfo(dayOfWeek, day, month, year);
			if ( newInfo != null)
			{
				renderingInfo = newInfo;
			}
        } catch (RaplaException e) {

        }
        return renderingInfo;
	}

}
