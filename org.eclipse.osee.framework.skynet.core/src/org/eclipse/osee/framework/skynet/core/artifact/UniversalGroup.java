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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Donald G. Dunne
 */
public class UniversalGroup {
   public static final String ARTIFACT_TYPE_NAME = "Universal Group";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UniversalGroup.class);

   public static Collection<Artifact> getGroups(Branch branch) {
      Collection<Artifact> artifacts = null;
      try {
         artifacts = ArtifactQuery.getArtifactsFromType(ARTIFACT_TYPE_NAME, branch);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
         artifacts = new LinkedList<Artifact>();
      }
      return artifacts;
   }

   public static Collection<Artifact> getGroups(String groupName, Branch branch) {
      try {
         return ArtifactQuery.getArtifactsFromTypeAndName(ARTIFACT_TYPE_NAME, groupName, branch);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      }
      return new ArrayList<Artifact>();
   }

   public static Artifact addGroup(String name, Branch branch) throws Exception {
      if (getGroups(name, branch).size() > 0) throw new IllegalArgumentException("Group Already Exists");

      Artifact groupArt = ArtifactTypeManager.addArtifact(UniversalGroup.ARTIFACT_TYPE_NAME, branch, name);
      groupArt.persistAttributes();
      Artifact groupRoot = getTopUniversalGroupArtifact(branch);
      if (groupRoot == null) {
         groupRoot = createTopUniversalGroupArtifact(branch);
         if (groupRoot == null) {
            throw new IllegalStateException("Could not create top universal group artifact.");
         }
      }
      groupRoot.relate(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, groupArt, true);
      return groupArt;
   }

   public static Artifact getTopUniversalGroupArtifact(Branch branch) throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      return ArtifactQuery.getArtifactFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME,
            ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME, branch);
   }

   public static Artifact createTopUniversalGroupArtifact(Branch branch) throws SQLException {
      Collection<Artifact> artifacts =
            ArtifactQuery.getArtifactsFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME,
                  ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME, branch);
      if (artifacts.size() == 0) {
         Artifact art =
               ArtifactTypeManager.addArtifact(ARTIFACT_TYPE_NAME, branch,
                     ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME);
         art.persistAttributes();
         return art;
      }
      return artifacts.iterator().next();
   }

}