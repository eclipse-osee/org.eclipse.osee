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
package org.eclipse.osee.client.integration.tests.integration.skynet.core.utils;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public final class TestUtil {

   private final static Random randomGenerator = new Random();

   private TestUtil() {
      // Utility Class - class should only have static methods
   }

   /**
    * Creates a simple artifact and adds it to the root artifact default hierarchical relation
    */
   public static Artifact createSimpleArtifact(IArtifactType artifactType, String name, BranchId branch)  {
      Artifact softArt = ArtifactTypeManager.addArtifact(artifactType, branch);
      softArt.setName(name);
      if (softArt.isAttributeTypeValid(CoreAttributeTypes.Subsystem)) {
         softArt.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "Electrical");
      }
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      rootArtifact.addRelation(CoreRelationTypes.Default_Hierarchical__Child, softArt);
      return softArt;
   }

   public static Collection<Artifact> createSimpleArtifacts(IArtifactType artifactType, int numArts, String name, BranchId branch)  {
      List<Artifact> arts = new ArrayList<>();
      for (int x = 1; x < numArts + 1; x++) {
         arts.add(createSimpleArtifact(artifactType, name + " " + x, branch));
      }
      return arts;
   }

   public static Map<String, Integer> getTableRowCounts(String... tables)  {
      Map<String, Integer> data = new HashMap<>();
      for (String tableName : tables) {
         data.put(tableName, getTableRowCount(tableName));
      }
      return data;
   }

   private static int getTableRowCount(String tableName)  {
      return ConnectionHandler.getJdbcClient().fetch(0, "SELECT count(1) FROM " + tableName);
   }

   public static RelationLink createRelationLink(int relationId, ArtifactId artA, ArtifactId artB, BranchId branch, RelationType relationType) {
      return createRelationLink(relationId, artA.getId().intValue(), artB.getId().intValue(), branch, relationType);
   }

   public static RelationLink createRelationLink(int relationId, int artA, int artB, BranchId branch, RelationType relationType) {
      return new RelationLink(ArtifactToken.valueOf(artA, branch), ArtifactToken.valueOf(artB, branch), branch,
         relationType, relationId, 0, "relation: " + relationId, ModificationType.MODIFIED, ApplicabilityId.BASE);
   }

   public static List<RelationLink> createLinks(int total, BranchId branch) {
      List<RelationLink> links = new ArrayList<>();
      for (int index = 0; index < total; index++) {
         RelationType relationType = createRelationType(index);
         RelationLink link = createRelationLink(index, index + 1, index + 2, branch, relationType);
         links.add(link);
      }
      return links;
   }

   public static List<RelationLink> createLinks(int total, BranchId branch, RelationType relationType) {
      List<RelationLink> links = new ArrayList<>();
      for (int index = 0; index < total; index++) {
         RelationLink link = createRelationLink(index, index + 1, index + 2, branch, relationType);
         links.add(link);
      }
      return links;
   }

   public static void setEveryOtherToDeleted(Collection<RelationLink> sourceLinks) {
      int count = 0;
      for (RelationLink link : sourceLinks) {
         if (count % 2 == 0) {
            link.delete(false);
         }
         count++;
      }

      int deletedCounts = 0;
      for (RelationLink link : sourceLinks) {
         if (link.isDeleted()) {
            deletedCounts++;
         }
      }
      int expected = sourceLinks.isEmpty() ? 0 : sourceLinks.size() / 2;
      Assert.assertEquals("Deleted relation link count did not match", expected, deletedCounts);
   }

   public static RelationType createRelationType(int index) {
      RelationTypeMultiplicity multiplicity =
         RelationTypeMultiplicity.values()[Math.abs(index % RelationTypeMultiplicity.values().length)];
      RelationSorter order = RelationSorter.values()[index % RelationTypeMultiplicity.values().length];
      return new RelationType(randomGenerator.nextLong(), "relType_" + index, "sideA_" + index, "sideB_" + index,
         Artifact, Artifact, multiplicity, order);
   }
}