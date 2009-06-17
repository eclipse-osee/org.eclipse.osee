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
package org.eclipse.osee.ote.service;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.OSEEPerson1_4;

/**
 * @author Ken J. Aguilar
 */
public class OteServiceProperties {
   private final IServiceConnector connector;

   private static final String NA = "N.A.";

   public OteServiceProperties(IServiceConnector connector) {
      this.connector = connector;
   }

   /**
    * @return the name
    */
   public String getName() {
      return (String) connector.getProperty("name", NA);
   }

   /**
    * @return the station
    */
   public String getStation() {
      return (String) connector.getProperty("station", NA);
   }

   /**
    * @return the type
    */
   public String getType() {
      return (String) connector.getProperty("type", NA);
   }

   /**
    * @return the mode
    */
   public String getMode() {
      return (String) connector.getProperty("mode", NA);
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return (String) connector.getProperty("version", NA);
   }

   /**
    * @return the group
    */
   public String getGroup() {
      return (String) connector.getProperty("groups", NA);
   }

   /**
    * @return the comment
    */
   public String getComment() {
      return (String) connector.getProperty("comment", NA);
   }

   /**
    * @return the dateStart
    */
   public Date getDateStarted() {
	return (Date) connector.getProperty("date", null);
   }

   public Collection<OSEEPerson1_4> getUserList() {
      return (Collection<OSEEPerson1_4>) connector.getProperty("user_list", new LinkedList<OSEEPerson1_4>());
   }

   public void printStats() {
      System.out.printf("test service found:\n\tname: %s\n\tstation: %s\n\ttype: %s\n\tcomment: %s\n\t%s\n", getName(),
            getStation(), getType(), getComment(), getGroup());
   }

   public String getOwner() {
	return (String) connector.getProperty("owner", NA);
    }
}
