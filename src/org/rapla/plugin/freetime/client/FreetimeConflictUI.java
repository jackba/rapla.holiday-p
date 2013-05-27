/*--------------------------------------------------------------------------*
 | Copyright (C) 2006 Christopher Kohlhaas, Bettina Lademann                |
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.rapla.facade.RaplaComponent;
import org.rapla.framework.RaplaContext;
import org.rapla.gui.toolkit.RaplaWidget;
import org.rapla.plugin.freetime.FreetimePlugin;

public class FreetimeConflictUI extends RaplaComponent
    implements RaplaWidget
{
    JPanel content = new JPanel();
    JTable jTable1 = new JTable();

    public FreetimeConflictUI(RaplaContext context,Map<Date,String> onFreetime) {
        super(context);
        setChildBundleName(FreetimePlugin.RESOURCE_FILE);
    	content.setLayout(new BorderLayout());
        jTable1.setPreferredScrollableViewportSize(new Dimension(400, 70));
        JScrollPane scrollPane = new JScrollPane(jTable1);
        content.add(scrollPane,BorderLayout.CENTER);
        Object[][] data = new Object[onFreetime.size()][2];
        int i=0;
        for ( Date date: onFreetime.keySet())
        {
        	String name = onFreetime.get( date);
        	data[i][0] = getRaplaLocale().formatDate(date);
        	data[i][1] = name;
        	i++;
        }
        String[] columnNames = new String[]
                {
            		getString("date")
                    , getString("holidays")
                };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        jTable1.setModel( model);
    }

    public JTable getTable() {
        return jTable1;
    }


    public JComponent getComponent() {
        return content;
    }

}



