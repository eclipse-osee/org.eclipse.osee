/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class DataFactory {
   private final static Random randomGenerator = new Random();

   private DataFactory() {
      // Utility Class
   }

   public static ArtifactType fromToken(IArtifactType artifactType) {
      String name = artifactType.getName();
      String guid = artifactType.getGuid();
      return new ArtifactType(guid, name, true);
   }

   public static IArtifact createArtifact(String name, String guid) {
      int uniqueId = randomGenerator.nextInt();
      return createArtifact(uniqueId, name, guid, null, fromToken(CoreArtifactTypes.Artifact));
   }

   public static IArtifact createArtifact(int uniqueId, String name, String guid, Branch branch, ArtifactType artifactType) {
      return new MockIArtifact(uniqueId, name, guid, branch, artifactType);
   }

   public static IArtifact createArtifact(int uniqueId, String name, String guid, Branch branch) {
      return new MockIArtifact(uniqueId, name, guid, branch, fromToken(CoreArtifactTypes.Artifact));
   }

   public static RelationLink createRelationLink(int relationId, int artA, int artB, Branch branch, RelationType relationType) {
      return new RelationLink(new MockLinker("Linker"), artA, artB, branch, relationType, relationId, 0,
         "relation: " + relationId, ModificationType.MODIFIED);
   }

   public static List<RelationLink> createLinks(int total, int artA, int artB, Branch branch) {
      List<RelationLink> links = new ArrayList<RelationLink>();
      for (int index = 0; index < total; index++) {
         RelationType relationType = createRelationType(index);
         RelationLink link = DataFactory.createRelationLink(index, index + 1, index + 2, branch, relationType);
         links.add(link);
      }
      return links;
   }

   public static List<RelationLink> createLinks(int total, Branch branch) {
      List<RelationLink> links = new ArrayList<RelationLink>();
      for (int index = 0; index < total; index++) {
         RelationType relationType = createRelationType(index);
         RelationLink link = DataFactory.createRelationLink(index, index + 1, index + 2, branch, relationType);
         links.add(link);
      }
      return links;
   }

   public static List<RelationLink> createLinks(int total, Branch branch, RelationType relationType) {
      List<RelationLink> links = new ArrayList<RelationLink>();
      for (int index = 0; index < total; index++) {
         RelationLink link = DataFactory.createRelationLink(index, index + 1, index + 2, branch, relationType);
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

   public static RelationType createRelationType(int id) {
      ArtifactType dummyArtType = MockDataFactory.createArtifactType(id);
      return MockDataFactory.createRelationType(id, dummyArtType, dummyArtType);
   }
}
