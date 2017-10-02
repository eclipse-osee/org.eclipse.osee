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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactNameConflictHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPasteOperation;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link ArtifactPasteOperation}.
 *
 * @author Roberto E. Escobar
 */
public class ArtifactPasteOperationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private Artifact parent1;
   private Artifact child1;
   private Artifact child2;
   private Artifact child3;
   private Artifact destination;
   private RelationOrderFactory relationOrderFactory;

   @Before
   public void setup() {
      List<Artifact> emptyList = Collections.emptyList();
      relationOrderFactory = new RelationOrderFactory();

      parent1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "Parent");

      child1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "child_a");
      child1.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);

      child2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "child_b");
      child3 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "child_c");

      parent1.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);
      parent1.addChild(child1);
      parent1.addChild(child3);
      parent1.addChild(child2);

      destination = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "Destination");
      destination.setRelationOrder(CoreRelationTypes.Default_Hierarchical__Child, emptyList);
      destination.addChild(parent1);
   }

   @After
   public void cleanup() {
      delete(child1);
      delete(child2);
      delete(child3);
      delete(parent1);
      delete(destination);
      relationOrderFactory = null;
   }

   private static void delete(Artifact artifact) {
      if (artifact != null) {
         ArtifactCache.deCache(artifact);
         artifact.deleteAndPersist();
      }
   }

   @Test
   public void testPasteArtifactNoChildren() throws Exception {
      String resolvedName = "Empty Parent First";
      try {
         performPaste(false, true, destination, Arrays.asList(parent1), resolvedName);
         checkPaste(destination, resolvedName, parent1, USER_DEFINED);
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
         checkPaste(destination, resolvedName, parent1, USER_DEFINED);

         Artifact newArtifact = destination.getChild(resolvedName);
         performPaste(false, true, newArtifact, Arrays.asList(child1), null);
         checkPaste(newArtifact, child1.getName(), child1, USER_DEFINED);

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
         checkPaste(destination, resolvedName, parent1, USER_DEFINED, "child_a", "child_c", "child_b");
      } finally {
         Artifact toDelete = destination.getChild(resolvedName);
         delete(toDelete);
      }
   }

   private void checkPaste(Artifact destination, String expectedChild, Artifact copiedArtifact, RelationSorter expectedOrderType, String... names) {
      Artifact newArtifact = destination.getChild(expectedChild);
      Assert.assertNotNull(newArtifact);
      Assert.assertTrue(!copiedArtifact.getGuid().equals(newArtifact.getGuid()));

      boolean hasChildren = names != null && names.length > 0;

      List<Artifact> childArtifacts = newArtifact.getChildren();

      Assert.assertEquals(hasChildren, !childArtifacts.isEmpty());
      if (hasChildren) {
         Assert.assertNotNull(names);
         Assert.assertEquals(names.length, childArtifacts.size());

         List<Artifact> sourceChildren = parent1.getChildren();
         List<String> guids = Artifacts.toGuids(sourceChildren);
         for (int index = 0; index < names.length; index++) {
            Artifact childArtifact = childArtifacts.get(index);
            Assert.assertEquals(names[index], childArtifact.getName());
            Assert.assertTrue(!guids.contains(childArtifact.getGuid()));
         }
      } else {
         Assert.assertTrue(childArtifacts.isEmpty());
      }
      checkRelationOrder(newArtifact, expectedOrderType, hasChildren);
      checkRelationOrder(destination, USER_DEFINED, true);
   }

   private void checkRelationOrder(Artifact artifactToCheck, RelationSorter expectedOrderType, boolean hasChildren) {
      RelationOrderData data = relationOrderFactory.createRelationOrderData(artifactToCheck);
      Assert.assertEquals(1, data.size());

      List<Artifact> childArtifacts = artifactToCheck.getChildren();
      Assert.assertEquals(hasChildren, !childArtifacts.isEmpty());
      for (Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> entry : data.entrySet()) {
         IRelationType relationTypeId = entry.getKey().getFirst();
         RelationSide relationSide = entry.getKey().getSecond();
         RelationSorter orderGuid = entry.getValue().getFirst();
         List<String> guids = entry.getValue().getSecond();

         Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child, relationTypeId);
         Assert.assertEquals(RelationSide.SIDE_B, relationSide);
         Assert.assertEquals(expectedOrderType, orderGuid);
         if (hasChildren && expectedOrderType == USER_DEFINED) {
            Assert.assertEquals(childArtifacts.size(), guids.size());
            for (int index = 0; index < guids.size(); index++) {
               Artifact orderedChild = childArtifacts.get(index);
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
      Operations.executeWorkAndCheckStatus(op);
   }

   private final static class MockArtifactNameConflictHandler extends ArtifactNameConflictHandler {

      private String resolveWith;
      private ArtifactToken conflictedArtifact;

      public MockArtifactNameConflictHandler() {
         resolveWith = null;
         conflictedArtifact = null;
      }

      public void setConflictedArtifact(ArtifactToken conflictedArtifact) {
         this.conflictedArtifact = conflictedArtifact;
      }

      public ArtifactToken getConflictedArtifact() {
         return conflictedArtifact;
      }

      public void setResolveWith(String resolveWith) {
         this.resolveWith = resolveWith;
      }

      public String getResolveWith() {
         return resolveWith;
      }

      @Override
      public String resolve(ArtifactToken artifact) {
         Assert.assertNotNull(artifact);
         Assert.assertEquals(getConflictedArtifact(), artifact);
         return getResolveWith();
      }

   }
}
