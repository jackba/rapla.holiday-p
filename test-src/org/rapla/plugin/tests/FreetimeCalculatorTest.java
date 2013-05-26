package org.rapla.plugin.tests;

import java.util.Date;

import org.rapla.RaplaTestCase;
import org.rapla.entities.domain.Allocatable;
import org.rapla.entities.domain.Reservation;
import org.rapla.facade.ClientFacade;
import org.rapla.facade.QueryModule;
import org.rapla.framework.RaplaLocale;

public class FreetimeCalculatorTest extends RaplaTestCase {

	public FreetimeCalculatorTest(String name) {
		super(name);
	}

	public void testIsFreetime() throws Exception {
		ClientFacade facade = getFacade();
		QueryModule tempQuery = facade;
		// this.Query
		Allocatable freetime = facade.newResource();
		freetime.getClassification().setValue("name", "freetime");
		facade.store( freetime);
		Reservation orig = (Reservation) facade.newReservation();
		orig.getClassification().setValue("name", "freetime");
		RaplaLocale raplaLocale = getRaplaLocale();
		Date start = raplaLocale.toDate(2012, 12, 1);
		Date end = raplaLocale.toDate(start, raplaLocale.toTime(12, 0, 0));
		orig.addAppointment(facade.newAppointment(start, end));

		orig.addAllocatable(freetime);
		facade.store(orig);



	}

}
