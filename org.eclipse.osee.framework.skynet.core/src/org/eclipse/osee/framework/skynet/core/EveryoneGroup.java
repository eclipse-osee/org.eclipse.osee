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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Jeff C. Phillips
 */
public class EveryoneGroup extends Group {
   private static final String GROUP_NAME = "Everyone";
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final EveryoneGroup reference = new EveryoneGroup();
   private Artifact everyoneGroup;
   private List<ISearchPrimitive> searchCriteria;
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(EveryoneGroup.class);

   public static EveryoneGroup getInstance() {
      return reference;
   }

   private EveryoneGroup() {
      super(GROUP_NAME);

      searchCriteria = new LinkedList<ISearchPrimitive>();
      searchCriteria.add(new ArtifactTypeSearch("User Group", Operator.EQUAL));
      searchCriteria.add(new AttributeValueSearch("Name", GROUP_NAME, Operator.EQUAL));

      try {
         Collection<Artifact> searchResults =
               artifactManager.getArtifacts(searchCriteria, true, branchManager.getCommonBranch());

         if (searchResults != null && searchResults.size() != 0) {
            everyoneGroup = searchResults.iterator().next();
         } else {
            ArtifactSubtypeDescriptor descriptor =
                  configurationManager.getArtifactSubtypeDescriptor("User Group", branchManager.getCommonBranch());

            if (descriptor != null) {
               everyoneGroup = descriptor.makeNewArtifact();
               everyoneGroup.setDescriptiveName(GROUP_NAME);

               boolean wasNotInDbInit = !SkynetDbInit.isDbInit();
               if (wasNotInDbInit) { // EveryoneGroup needs to be created under the special condition of the init
                  SkynetDbInit.setIsInDbInit(true);
               }
               everyoneGroup.persistAttributes();
               if (wasNotInDbInit) { // if we were not in an init before this method then go back to that state
                  SkynetDbInit.setIsInDbInit(false);
               }
            } else {
               throw new IllegalStateException("No User Group Descriptor.");
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * @return Returns the everyoneGroup.
    */
   public Artifact getEveryoneGroup() {
      return everyoneGroup;
   }

   /**
    * This does not persist the newly created relation that is the callers responsibility.
    * 
    * @param user
    * @throws SQLException
    */
   public void addGroupMember(User user) throws SQLException {
      everyoneGroup.relate(RelationSide.Users_User, user);
   }
}
