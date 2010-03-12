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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactNameConflictHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPasteOperation;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link ArtifactPasteOperation}.
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactPasteOperationTest {

   private static Artifact parent1;

   private static Artifact child1;
   private static Artifact child2;
   private static Artifact child3;
   private static Artifact destination;
   private static RelationOrderFactory relationOrderFactory;

   @BeforeClass
   public static void setup() throws OseeCoreException {
      List<Artifact> emptyList = Collections.emptyList();
      relationOrderFactory = new RelationOrderFactory();

      Branch branch = BranchManager.getCommonBranch();
      String artifactType = "Folder";
      parent1 = ArtifactTypeManager.addArtifact(artifactType, branch, "Parent");

      child1 = ArtifactTypeManager.addArtifact(artifactType, branch, "child_a");
      child1.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);

      child2 = ArtifactTypeManager.addArtifact(artifactType, branch, "child_b");
      child3 = ArtifactTypeManager.addArtifact(artifactType, branch, "child_c");

      parent1.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);
      parent1.addChild(child1);
      parent1.addChild(child3);
      parent1.addChild(child2);

      destination = ArtifactTypeManager.addArtifact(artifactType, branch, "Destination");
      destination.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);
      destination.addChild(parent1);
   }

   @AfterClass
   public static void cleanup() throws OseeCoreException {
      delete(child1);
      delete(child2);
      delete(child3);
      delete(parent1);
      delete(destination);
      relationOrderFactory = null;
   }

   private static void delete(Artifact artifact) throws OseeCoreException {
      if (artifact != null) {
         artifact.deleteAndPersist();
      }
   }

   @Test
   public void testPasteArtifactNoChildren() throws Exception {
      String resolvedName = "Empty Parent First";
      try {
         performPaste(false, true, destination, Arrays.asList(parent1), resolvedName);
         checkPaste(destination, resolvedName, parent1, RelationOrderBaseTypes.USER_DEFINED);
      } finally {
         Artifact toDelete = destination.getChild(resolvedName);
         delete(toDelete);
      }
   }

   @Test
   public void testPasteArtifactFolderThenAddChild() throws Exception {
      String resolvedName = "Parent No Children";
      try {
         performPaste(false, true, destination, Arrays.asList(parent1), resolvedName);
         checkPaste(destination, resolvedName, parent1, RelationOrderBaseTypes.USER_DEFINED);

         Artifact newArtifact = destination.getChild(resolvedName);
         performPaste(false, true, newArtifact, Arrays.asList(child1), null);
         checkPaste(newArtifact, child1.getName(), child1, RelationOrderBaseTypes.USER_DEFINED);

      } finally {
         Artifact toDelete = destination.getChild(resolvedName);
         delete(toDelete);
      }

   }

   @Test
   public void testPasteArtifactsWithChildren() throws Exception {
      String resolvedName = "Parent With Children";
      try {
         performPaste(true, true, destination, Arrays.asList(parent1), resolvedName);
         checkPaste(destination, resolvedName, parent1, RelationOrderBaseTypes.USER_DEFINED, "child_a", "child_c",
               "child_b");
      } finally {
         Artifact toDelete = destination.getChild(resolvedName);
         delete(toDelete);
      }
   }

   private void checkPaste(Artifact destination, String expectedChild, Artifact copiedArtifact, RelationOrderBaseTypes expectedOrderType, String... names) throws OseeCoreException {
      Artifact newArtifact = destination.getChild(expectedChild);
      Assert.assertNotNull(newArtifact);
      Assert.assertTrue(!copiedArtifact.getGuid().equals(newArtifact.getGuid()));

      boolean hasChildren = names != null && names.length > 0;

      List<Artifact> childArtifacts = newArtifact.getChildren();

      Assert.assertEquals(hasChildren, !childArtifacts.isEmpty());
      if (hasChildren) {
         Assert.assertEquals(names.length, childArtifacts.size());

         List<Artifact> sourceChildren = parent1.getChildren();
         List<String> guids = Artifacts.toGuids(sourceChildren);
         for (int index = 0; index < names.length; index++) {
            IArtifact childArtifact = childArtifacts.get(index);
            Assert.assertEquals(names[index], childArtifact.getName());
            Assert.assertTrue(!guids.contains(childArtifact.getGuid()));
         }
      } else {
         Assert.assertTrue(childArtifacts.isEmpty());
      }
      checkRelationOrder(newArtifact, expectedOrderType, hasChildren);
      checkRelationOrder(destination, RelationOrderBaseTypes.USER_DEFINED, true);
   }

   private void checkRelationOrder(Artifact artifactToCheck, RelationOrderBaseTypes expectedOrderType, boolean hasChildren) throws OseeCoreException {
      RelationOrderData data = relationOrderFactory.createRelationOrderData(artifactToCheck);
      Assert.assertEquals(1, data.size());

      List<Artifact> childArtifacts = artifactToCheck.getChildren();
      Assert.assertEquals(hasChildren, !childArtifacts.isEmpty());
      for (Entry<Pair<String, String>, Pair<String, List<String>>> entry : data.entrySet()) {
         String relationType = entry.getKey().getFirst();
         String relationSide = entry.getKey().getSecond();
         String orderGuid = entry.getValue().getFirst();
         List<String> guids = entry.getValue().getSecond();

         Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getName(), relationType);
         Assert.assertEquals(RelationSide.SIDE_B.name(), relationSide);
         Assert.assertEquals(expectedOrderType.getGuid(), orderGuid);
         if (hasChildren && expectedOrderType == RelationOrderBaseTypes.USER_DEFINED) {
            Assert.assertEquals(childArtifacts.size(), guids.size());
            for (int index = 0; index < guids.size(); index++) {
               IArtifact orderedChild = childArtifacts.get(index);
               String guid = guids.get(index);
               Assert.assertEquals(orderedChild.getGuid(), guid);
            }
         } else {
            Assert.assertTrue(guids.isEmpty());
         }
      }
   }

   private void performPaste(boolean includeChildren, boolean pasteRelationOrder, Artifact destination, List<Artifact> itemsToCopy, String resolvedName) throws Exception {
      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();
      config.setIncludeChildrenOfCopiedElements(includeChildren);
      config.setKeepRelationOrderSettings(pasteRelationOrder);

      Assert.assertEquals(includeChildren, config.isIncludeChildrenOfCopiedElements());
      Assert.assertEquals(pasteRelationOrder, config.isKeepRelationOrderSettings());

      MockArtifactNameConflictHandler handler = new MockArtifactNameConflictHandler();
      handler.setResolveWith(resolvedName);
      handler.setConflictedArtifact(itemsToCopy.get(0));

      ArtifactPasteOperation op = new ArtifactPasteOperation(config, destination, itemsToCopy, handler);
      Operations.executeWork(op, new NullProgressMonitor(), -1);
      Operations.checkForErrorStatus(op.getStatus());
   }

   private final static class MockArtifactNameConflictHandler extends ArtifactNameConflictHandler {

      private String resolveWith;
      private IArtifact conflictedArtifact;

      public MockArtifactNameConflictHandler() {
         resolveWith = null;
         conflictedArtifact = null;
      }

      public void setConflictedArtifact(IArtifact conflictedArtifact) {
         this.conflictedArtifact = conflictedArtifact;
      }

      public IArtifact getConflictedArtifact() {
         return conflictedArtifact;
      }

      public void setResolveWith(String resolveWith) {
         this.resolveWith = resolveWith;
      }

      public String getResolveWith() {
         return resolveWith;
      }

      @Override
      public String resolve(IArtifact artifact) throws CoreException {
         Assert.assertNotNull(artifact);
         Assert.assertEquals(getConflictedArtifact(), artifact);
         return getResolveWith();
      }

   }
}
