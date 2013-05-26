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

import org.rapla.entities.domain.Appointment;
import org.rapla.entities.domain.Reservation;
import org.rapla.entities.storage.internal.SimpleIdentifier;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaException;
import org.rapla.gui.internal.RaplaDateRenderer;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.server.FreetimeServiceRemote;

/** Renders holidays in a special color. */
public class FreetimeHighlightRenderer extends RaplaDateRenderer {
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
	    RenderingInfo renderingInfo = super.getRenderingInfo(dayOfWeek, day, month, year);
        try {
            final String freetime = getWebservice(FreetimeServiceRemote.class).getFreetimeName(
                    getRaplaLocale().toDate(year, month, day)
            );

            if (freetime != null && !freetime.isEmpty()){
                renderingInfo = new RenderingInfo(
                        FreetimePlugin.BACKGROUND_COLOR,
                        FreetimePlugin.FOREGROUND_COLOR,
                        freetime);
            }
        } catch (RaplaException e) {
            getLogger().error(e.getMessage(), e);
        }
        return renderingInfo;
	}
	
}
