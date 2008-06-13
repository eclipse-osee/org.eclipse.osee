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
package org.eclipse.osee.framework.skynet.core;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Jeff C. Phillips
 */
public class EveryoneGroup extends Group {
   private static final String GROUP_NAME = "Everyone";
   private static final EveryoneGroup instance = new EveryoneGroup();
   private static Artifact everyoneGroup;
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(EveryoneGroup.class);

   private EveryoneGroup() {
      super(GROUP_NAME);

      try {
         List<Artifact> artifacts =
               ArtifactQuery.getArtifactsFromTypeAndName("User Group", GROUP_NAME,
                     BranchPersistenceManager.getCommonBranch());

         if (!artifacts.isEmpty()) {
            everyoneGroup = artifacts.get(0);
         } else {
            everyoneGroup =
                  ArtifactTypeManager.addArtifact("User Group", BranchPersistenceManager.getCommonBranch(), GROUP_NAME);

            boolean wasNotInDbInit = !SkynetDbInit.isDbInit();
            if (wasNotInDbInit) { // EveryoneGroup needs to be created under the special condition of the init
               SkynetDbInit.setIsInDbInit(true);
            }
            everyoneGroup.persistAttributes();
            if (wasNotInDbInit) { // if we were not in an init before this method then go back to that state
               SkynetDbInit.setIsInDbInit(false);
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * @return Returns the everyoneGroup.
    */
   public static Artifact getEveryoneGroup() {
      return instance.everyoneGroup;
   }

   /**
    * This does not persist the newly created relation that is the callers responsibility.
    * 
    * @param user
    * @throws SQLException
    */
   public static void addGroupMember(User user) throws SQLException {
      getEveryoneGroup().addRelation(CoreRelationEnumeration.Users_User, user);
   }
}