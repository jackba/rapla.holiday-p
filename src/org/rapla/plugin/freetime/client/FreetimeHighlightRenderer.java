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
import org.rapla.components.util.TimeInterval;
import org.rapla.facade.ModificationEvent;
import org.rapla.facade.ModificationListener;
import org.rapla.framework.Configuration;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.framework.RaplaLocale;
import org.rapla.gui.internal.RaplaDateRenderer;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.FreetimeServiceRemote;
import org.rapla.plugin.freetime.FreetimeServiceRemote.Holiday;

/** Renders holidays in a special color. */
public class FreetimeHighlightRenderer extends RaplaDateRenderer implements ModificationListener  
{
	private static final int INVALIDATE_IN_MILLIS = 10000;
	long holidayRepositoryVersion = 0;
	TimeInterval invalidateInterval;
	private SortedMap<Date, String> cache = new TreeMap<Date, String>();
	private long lastCachedTime;
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
		getUpdateModule().addModificationListener( this);
		updateCache();
		this.config = config;
	}
	
	private void updateCache() throws RaplaException
	{
        long version = webservice.getHolidayRepositoryVersion();
        if ( version > holidayRepositoryVersion)
        {
	        Date start = null;
			Date end = null;
			cache.clear();
	        List<Holiday> list = webservice.getHolidays(start, end);
	        Iterator<Holiday> it = list.iterator();
	        while (it.hasNext())
	        {
	            Holiday holiday = it.next();
	            cache.put( holiday.date, holiday.name);
	        }
	        holidayRepositoryVersion = version;
	        invalidateInterval = null;
        }
        if ( System.currentTimeMillis() - INVALIDATE_IN_MILLIS > 5000)
        {
            invalidateInterval = null;
        }
        lastCachedTime = System.currentTimeMillis();
   }

	/**
	 * returns the color if day is set for highlight and null if not.
	 */
	synchronized public RenderingInfo getRenderingInfo(int dayOfWeek, int day, int month, int year) {
        RenderingInfo renderingInfo = super.getRenderingInfo(dayOfWeek, day, month, year);
        try {
        	RaplaLocale raplaLocale = getRaplaLocale();
        	// if five seconds passed since last check, check again
        	if (System.currentTimeMillis() - lastCachedTime > INVALIDATE_IN_MILLIS || invalidateInterval != null )
        	{
        		updateCache();
        	}
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

	@Override
	public void dataChanged(ModificationEvent evt) throws RaplaException {
		TimeInterval interval = evt.getInvalidateInterval();
		if ( interval != null)
		{
			if ( invalidateInterval != null)
			{
				invalidateInterval = invalidateInterval.union( interval);
			}
			else
			{
				invalidateInterval = interval;
			}
		}
	}	
	
}
