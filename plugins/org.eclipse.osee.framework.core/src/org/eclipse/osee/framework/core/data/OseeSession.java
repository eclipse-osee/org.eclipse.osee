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
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * @author Roberto E. Escobar
 */
public class OseeSession extends OseeClientInfo {

   private static final long serialVersionUID = 8144856081780854567L;
   private static final String SESSION_ID = "sessionId";
   private static final String USER_ID = "userId";
   private static final String CREATED_ON = "createdOn";
   private static final String LAST_INTERACTION_DATE = "lastInteractionDate";
   private static final String LAST_INTERACTION = "lastInteraction";

   private OseeSession() {
      super();
   }

   public OseeSession(String sessionId, String userId, Timestamp createdOn, String machineName, String machineIp, int port, String clientVersion, Timestamp lastInteractionDate, String lastInteractionName) {
      super(clientVersion, machineName, machineIp, port);
      this.backingData.put(SESSION_ID, sessionId);
      this.backingData.put(USER_ID, userId);
      this.backingData.put(CREATED_ON, createdOn.getTime());
      this.backingData.put(LAST_INTERACTION_DATE, lastInteractionDate.getTime());
      this.backingData.put(LAST_INTERACTION, lastInteractionName);
   }

   /**
    * @return the userId
    */
   public String getUserId() {
      return getString(USER_ID);
   }

   /**
    * @return the session id
    */
   public String getSessionId() {
      return getString(SESSION_ID);
   }

   /**
    * @return the session creation date
    */
   public Timestamp getCreation() {
      return new Timestamp(backingData.getLong(CREATED_ON));
   }

   /**
    * @return the last task performed/requested
    */
   public String getLastInteraction() {
      return getString(LAST_INTERACTION);
   }

   /**
    * @return the last communication timestamp
    */
   public Timestamp getLastInteractionDate() {
      return new Timestamp(backingData.getLong(LAST_INTERACTION_DATE));
   }

   /**
    * Set the last interaction name
    */
   public void setLastInteraction(String lastInteractionName) {
      this.backingData.put(LAST_INTERACTION, lastInteractionName);
   }

   /**
    * Set the last interaction date
    */
   public void setLastInteractionDate(Timestamp timestamp) {
      this.backingData.put(LAST_INTERACTION_DATE, timestamp.getTime());
   }

   /**
    * Get a the session id and version in a single string
    * 
    * @return the session id and version
    */
   public String getSessionIdAndVersion() {
      return String.format("%s - %s", getSessionId(), getVersion());
   }

   /**
    * Create new instance from XML input
    * 
    * @return OseeSession the new instance
    */
   public static OseeSession fromXml(InputStream inputStream) {
      OseeSession session = new OseeSession();
      session.loadfromXml(inputStream);
      return session;
   }

}
