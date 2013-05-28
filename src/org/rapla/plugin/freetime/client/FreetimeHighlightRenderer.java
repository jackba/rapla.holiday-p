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

import org.rapla.components.util.SerializableDateTimeFormat;
import org.rapla.components.util.TimeInterval;
import org.rapla.facade.ModificationEvent;
import org.rapla.facade.ModificationListener;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.internal.RaplaDateRenderer;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.FreetimeServiceRemote;

import java.awt.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/** Renders holidays in a special color. */
public class FreetimeHighlightRenderer extends RaplaDateRenderer implements ModificationListener  
{
	long holidayRepositoryVersion = 0;
	TimeInterval invalidateInterval;
	private SortedMap<Date, String> cache = new TreeMap<Date, String>();
	private long lastCachedTime;
	   
	/**
	 * 
	 * @param context
	 * @throws RaplaException
	 */
	public FreetimeHighlightRenderer(RaplaContext context) throws RaplaException {
		super(context);
		getUpdateModule().addModificationListener( this);
		updateCache();
	}
	
	private void updateCache() throws RaplaException
	{
        FreetimeServiceRemote webservice = getWebservice(FreetimeServiceRemote.class);
        long version = webservice.getHolidayRepositoryVersion();
        if ( version > holidayRepositoryVersion)
        {
	        Date start = null;
			Date end = null;
			cache.clear();
	        String[][] holidays = webservice.getHolidays(start, end);	
	        Map<Date, String> map = toMap(holidays);
	        cache.putAll( map);
	        holidayRepositoryVersion = version;
	        invalidateInterval = null;
        }
        if ( System.currentTimeMillis() - lastCachedTime > 5000)
        {
            invalidateInterval = null;
        }
        lastCachedTime = System.currentTimeMillis();
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

	/**
	 * returns the color if day is set for highlight and null if not.
	 */
	public RenderingInfo getRenderingInfo(int dayOfWeek, int day, int month, int year) {
        RenderingInfo renderingInfo = super.getRenderingInfo(dayOfWeek, day, month, year);
        try {
        	Date date = getRaplaLocale().toDate(year, month, day);
        	// if five seconds passed since last check, check again
        	if (System.currentTimeMillis() - lastCachedTime > 5000 || invalidateInterval != null )
        	{
        		updateCache();
        	}
        	String holidayNames = cache.get( date);
        	if (holidayNames != null){
                Color backgroundColor = FreetimePlugin.BACKGROUND_COLOR; 
                Color foregroundColor = FreetimePlugin.FOREGROUND_COLOR;
                String tooltipText = holidayNames;
                renderingInfo = new RenderingInfo(backgroundColor, foregroundColor, tooltipText);
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

	@Override
	public boolean isInvokedOnAWTEventQueue() {
		return true;
	}

	
	
}
