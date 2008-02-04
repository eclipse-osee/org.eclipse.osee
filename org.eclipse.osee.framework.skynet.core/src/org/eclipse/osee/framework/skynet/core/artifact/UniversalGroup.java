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

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;

/**
 * @author Donald G. Dunne
 */
public class UniversalGroup {
   public static final String ARTIFACT_TYPE_NAME = "Universal Group";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UniversalGroup.class);

   public static Collection<Artifact> getGroups(Branch branch) {
      Collection<Artifact> artifacts = null;
      try {
         artifacts = ArtifactPersistenceManager.getInstance().getArtifactsFromSubtypeName(ARTIFACT_TYPE_NAME, branch);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
         artifacts = new LinkedList<Artifact>();
      }
      return artifacts;
   }

   public static Collection<Artifact> getGroups(String groupName, Branch branch) {
      try {
         List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch(ARTIFACT_TYPE_NAME, EQUAL));
         criteria.add(new AttributeValueSearch("Name", groupName, EQUAL));

         return ArtifactPersistenceManager.getInstance().getArtifacts(criteria, true, branch);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      }
      return new ArrayList<Artifact>();
   }

   public static Artifact addGroup(String name, Branch branch) throws Exception {
      if (getGroups(name, branch).size() > 0) throw new IllegalArgumentException("Group Already Exists");

      AddGroupTx addGroupTx = null;
      addGroupTx = new AddGroupTx(branch, name);
      addGroupTx.execute();
      return addGroupTx.getGroupArtifact();
   }

   public static Artifact getTopUniversalGroupArtifact(Branch branch) throws SQLException {
      return ArtifactPersistenceManager.getInstance().getArtifactFromTypeName(UniversalGroup.ARTIFACT_TYPE_NAME,
            ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME, branch, false);
   }

   public static Artifact createTopUniversalGroupArtifact(Branch branch) throws SQLException {
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(UniversalGroup.ARTIFACT_TYPE_NAME,
                  ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME, branch);
      if (srch.getArtifacts(Artifact.class).size() == 0) {
         Artifact art =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(ARTIFACT_TYPE_NAME, branch).makeNewArtifact();
         art.setDescriptiveName(ArtifactPersistenceManager.ROOT_ARTIFACT_TYPE_NAME);
         art.persistAttributes();
         return art;
      }
      return srch.getArtifacts(Artifact.class).iterator().next();
   }

   static private final class AddGroupTx extends AbstractSkynetTxTemplate {
      private String name;
      private Artifact groupArt;

      public AddGroupTx(Branch branch, String name) {
         super(branch);
         this.name = name;
         this.groupArt = null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.transaction.AbstractTxTemplate#handleTxWork()
       */
      @Override
      protected void handleTxWork() throws Exception {
         Artifact groupArt =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     UniversalGroup.ARTIFACT_TYPE_NAME, getTxBranch()).makeNewArtifact();
         groupArt.setDescriptiveName(name);
         groupArt.persistAttributes();
         Artifact groupRoot = getTopUniversalGroupArtifact(getTxBranch());
         if (groupRoot == null) {
            groupRoot = createTopUniversalGroupArtifact(getTxBranch());
            if (groupRoot == null) {
               throw new IllegalStateException("Could not create top universal group artifact.");
            }
         }
         groupRoot.relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS, groupArt, true);
      }

      private Artifact getGroupArtifact() {
         return groupArt;
      }
   }
}