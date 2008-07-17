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
package org.eclipse.osee.framework.jini.service.test;

import java.util.Date;
import org.eclipse.osee.framework.jini.service.core.FormmatedEntry;

/**
 * @author Andrew M. Finkbeiner
 */
public class StaticStationInfo extends FormmatedEntry {

   /**
    * 
    */
   private static final long serialVersionUID = 5954011049797538187L;
   public String station;
   public String maxUsers;
   public String type;
   public String mode;
   public String version;
   public Date dateStarted;

   public StaticStationInfo() {
      this("??", "??", "??", "??", "??", new Date());
   }

   /**
    * 
    */
   public StaticStationInfo(String station, String maxUsers, String type, String mode, String version, Date dateStarted) {
      super();
      this.station = station;
      this.type = type;
      this.dateStarted = dateStarted;
      this.mode = mode;
      this.version = version;
      this.maxUsers = maxUsers;
   }

   /**
    * @return Returns the dateStarted.
    */
   public Date getDateStarted() {
      return dateStarted;
   }

   /**
    * @return Returns the environmentType.
    */
   public String getType() {
      return type;
   }

   /**
    * @return Returns the stationName.
    */
   public String getStation() {
      return station;
   }

   /**
    * @return Returns the mode.
    */
   public String getMode() {
      return mode;
   }

   /**
    * @return Returns the version.
    */
   public String getVersion() {
      return version;
   }

   public String getFormmatedString() {
      return "Host : " + station + "\n" + "Type : " + type + "\n" + "Version : " + version + "\n" + "Started : " + dateStarted + "\n";
   }
}
