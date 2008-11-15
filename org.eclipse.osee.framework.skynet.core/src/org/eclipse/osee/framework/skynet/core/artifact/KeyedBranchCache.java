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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;

/**
 * @author Donald G. Dunne
 */
public class KeyedBranchCache {
   private Map<String, Branch> keynameBranchMap = null;
   private static final KeyedBranchCache instance = new KeyedBranchCache();
   private static final String GET_BRANCH_NAMES_FROM_CONFIG = "SELECT * FROM osee_branch_definitions";

   private KeyedBranchCache() {
   }

   public static void createKeyedBranch(String keyname, Branch branch) throws OseeCoreException {
      instance.ensurePopulated();
      instance.keynameBranchMap.put(keyname.toLowerCase(), branch);
   }

   public static Branch getKeyedBranch(String keyname) throws OseeCoreException {
      if (keyname == null) throw new IllegalArgumentException("keyname can not be null");

      instance.ensurePopulated();
      String lowerKeyname = keyname.toLowerCase();
      if (instance.keynameBranchMap.containsKey(lowerKeyname)) {
         return instance.keynameBranchMap.get(lowerKeyname);
      } else {
         throw new BranchDoesNotExist("The key \"" + keyname + "\" does not refer to any branch");
      }
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (keynameBranchMap == null) {
         populateCache();
      }
   }

   private void populateCache() throws OseeCoreException {
      keynameBranchMap = new HashMap<String, Branch>();

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(GET_BRANCH_NAMES_FROM_CONFIG);

         while (chStmt.next()) {
            try {
               keynameBranchMap.put(chStmt.getString("static_branch_name").toLowerCase(),
                     BranchManager.getBranch(chStmt.getInt("mapped_branch_id")));
            } catch (BranchDoesNotExist ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }
}