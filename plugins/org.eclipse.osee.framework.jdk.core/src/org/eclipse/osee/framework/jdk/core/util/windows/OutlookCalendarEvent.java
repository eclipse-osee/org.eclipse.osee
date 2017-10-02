/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.windows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OutlookCalendarEvent {

   private final String location;
   private final String event;
   private final DateFormat myDateFormat;
   private final Date startDate;
   private final Date endDate;

   /**
    * @param location - String the event location
    * @param event - String the scheduled event
    * @param startTime - 0800 - 8am
    * @param endTime - 1300 - 1pm
    */
   public OutlookCalendarEvent(String location, String event, Date startDate, Date endDate) {
      super();
      this.location = location;
      this.event = event;
      this.startDate = startDate;
      this.endDate = endDate;
      myDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
   }

   public String getEvent() {
      return "\nBEGIN:VCALENDAR\n" +
      //
         "PRODID:-//Microsoft Corporation//Outlook 10.0 MIMEDIR//EN\n" +
         //
         "VERSION:1.0\n" +
         //
         "BEGIN:VEVENT\n" +
         //
         "DTSTART:" + myDateFormat.format(startDate) + "\n" +
         //
         "DTEND:" + myDateFormat.format(endDate) + "\n" +
         //
         "LOCATION;ENCODING=QUOTED-PRINTABLE:" + location + "\n" +
         //
         "TRANSP:1\n" +
         //
         "DESCRIPTION;ENCODING=QUOTED-PRINTABLE:=0D=0A\n" +
         //
         "SUMMARY;ENCODING=QUOTED-PRINTABLE:Event:" + event + "\n" +
         //
         "PRIORITY:3\n" +
         //
         "END:VEVENT\n" +
         //
         "END:VCALENDAR\n";
   }

}
