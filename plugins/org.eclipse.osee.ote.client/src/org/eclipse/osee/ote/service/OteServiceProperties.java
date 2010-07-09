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

import java.io.Serializable;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;

/**
 * @author Ken J. Aguilar
 */
public class OteServiceProperties {
   private IServiceConnector connector;

   private static final String NA = "N.A.";
   private boolean debug = false;

   private EnhancedProperties properties;

   public OteServiceProperties(IServiceConnector connector) {
      this.connector = connector;
   }

   public OteServiceProperties(EnhancedProperties properties) {
      this.properties = properties;
   }

   private String getLocalProperty(String name){
      if(connector != null){
         return (String)connector.getProperty(name, NA);
      } else if(properties != null){
         return (String)properties.getProperty(name, NA);
      }
      return NA;
   }
   
   /**
    * @return the name
    */
   public String getName() {
      return getLocalProperty("name");
   }

   /**
    * @return the station
    */
   public String getStation() {
      return getLocalProperty("station");
   }

   /**
    * @return the type
    */
   public String getType() {
      return getLocalProperty("type");
   }

   /**
    * @return the mode
    */
   public String getMode() {
      return getLocalProperty("mode");
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return getLocalProperty("version");
   }

   /**
    * @return the group
    */
   public String getGroup() {
      return getLocalProperty("groups");
   }

   /**
    * @return the comment
    */
   public String getComment() {
      return getLocalProperty("comment");
   }

   /**
    * @return the dateStart
    */
   public String getDateStarted() {
      return getLocalProperty("date").toString();
   }

   public String getUserList() {
      return getLocalProperty("user_list").toString();
   }

   public void printStats() {
      if (debug) {
         System.out.printf("test service found:\n\tname: %s\n\tstation: %s\n\ttype: %s\n\tcomment: %s\n\t%s\n",
               getName(), getStation(), getType(), getComment(), getGroup());
      }
   }
   
   public Serializable getProperty(String name) {
	   return connector.getProperty(name, null);
   }

   public String getOwner() {
      return (String) connector.getProperty("owner", NA);
   }
}
