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
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.internal.ServerActivator;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UserDataStore {
   private static final String LOAD_OSEE_USER =
      "select oa.value as user_id from osee_attribute_type oat, osee_attribute oa, osee_txs txs where oat.name = 'User Id' and oat.attr_type_id = oa.attr_type_id and oa.gamma_id = txs.gamma_id and txs.tx_current = 1 and oa.value = ?";

   private UserDataStore() {
      // private constructor
   }

   public static IUserToken getUserTokenFromOseeDb(String userId) {
      IUserToken toReturn = null;
      IOseeStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.getStatement();
         chStmt.runPreparedQuery(LOAD_OSEE_USER, userId);
         if (chStmt.next()) {
            // Only need the userId all other fields will be loaded by the client
            toReturn = TokenFactory.createUserToken(null, "-", "-", chStmt.getString("user_id"), false, false, false);
         }
      } catch (OseeCoreException ex) {
         OseeLog.logf(ServerActivator.class, Level.SEVERE, ex, "Unable to find userId [%s] in OSEE database.", userId);
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
      return toReturn;
   }

   public static IUserToken createUserToken(boolean isCreationRequired, String userName, String userId, String userEmail, boolean isActive) {
      return TokenFactory.createUserToken(null, userName, userEmail, userId, isActive, false, isCreationRequired);
   }

}