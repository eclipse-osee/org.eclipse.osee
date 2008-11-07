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
package org.eclipse.osee.framework.core.server;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UserDataStore {
   private static final String LOAD_OSEE_USER =
         "select oa.value as user_id from osee_attribute_type oat, osee_attribute oa, osee_txs txs where oat.name = 'User Id' and oat.attr_type_id = oa.attr_type_id and oa.gamma_id = txs.gamma_id and txs.tx_current = 1 and oa.value = ?";

   private UserDataStore() {
   }

   public static IOseeUserInfo getOseeUserFromOseeDb(String bemsId) {
      IOseeUserInfo toReturn = null;
      try {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         chStmt.runPreparedQuery(LOAD_OSEE_USER, bemsId);
         if (chStmt.next()) {
            // Only need the userId all other fields will be loaded by the client
            toReturn = new OseeUserInfo(true, "-", chStmt.getString("user_id"), "-", false);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.SEVERE, String.format(
               "Unable to find bems id [%s] in msa database.", bemsId), ex);
      }
      return toReturn;
   }

   public static IOseeUserInfo createUser(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return new OseeUserInfo(isCreationRequired, userName, userId, userEmail, isActive);
   }

   private final static class OseeUserInfo implements IOseeUserInfo {
      private final boolean isCreationRequired;
      private final String userName;
      private final String userId;
      private final String userEmail;
      private final boolean isActive;

      private OseeUserInfo(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
         this.isCreationRequired = isCreationRequired;
         this.userName = userName;
         this.userId = userId;
         this.userEmail = userEmail;
         this.isActive = isActive;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUserInfo#isCreationRequired()
       */
      @Override
      public boolean isCreationRequired() {
         return isCreationRequired;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getEmail()
       */
      @Override
      public String getEmail() {
         return userEmail;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getName()
       */
      @Override
      public String getName() {
         return userName;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#getUserID()
       */
      @Override
      public String getUserID() {
         return userId;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.data.IOseeUser#isActive()
       */
      @Override
      public boolean isActive() {
         return isActive;
      }

   }
}
