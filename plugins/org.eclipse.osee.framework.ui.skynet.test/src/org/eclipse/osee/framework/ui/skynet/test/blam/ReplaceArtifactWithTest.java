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

package org.eclipse.osee.framework.ui.skynet.test.blam;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceArtifactWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceRelationsWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.Assert;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceArtifactWithTest {

   @org.junit.Test
   public void testReplaceArtifactVersion() throws Exception {
      Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(parentBranch);

      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, parentBranch, getClass().getSimpleName());
      artifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("My Name"));
      artifact.persist(getClass().getName());

      Attribute<?> nameAttribute = artifact.getAttributes(CoreAttributeTypes.Name).iterator().next();
      int previousGamma = nameAttribute.getGammaId();
      String previousName = nameAttribute.getDisplayableString();

      Branch childBranch =
         BranchManager.createWorkingBranch(parentBranch, "Branchy branch", UserManager.getUser(SystemUser.OseeSystem));

      Thread.sleep(3000);

      Artifact childArtifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), childBranch);
      Attribute<?> childNameAttribute = childArtifact.getAttributes(CoreAttributeTypes.Name).iterator().next();

      int parentNumberOfAttrs = childArtifact.getAttributes().size();
      //modify name attribute
      childNameAttribute.setFromString("New Name");
      childNameAttribute.getArtifact().persist(getClass().getName());

      childArtifact.addAttribute(CoreAttributeTypes.StaticId, "I am an ID");
      childArtifact.persist(getClass().getName());

      ChangeManager.getChangesMadeOnCurrentBranch(childArtifact, new NullProgressMonitor());

      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(
         Collections.singleton(childArtifact)));

      childArtifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), childBranch);
      int childNumberOfAttrs = childArtifact.getAttributes().size();
      childNameAttribute = childArtifact.getAttributes(CoreAttributeTypes.Name).iterator().next();

      assertTrue(parentNumberOfAttrs == childNumberOfAttrs);
      assertTrue(nameAttribute.getGammaId() == previousGamma);
      assertTrue(childArtifact.getAttributes(CoreAttributeTypes.Name).iterator().next().getValue().equals(previousName));

      artifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("My New Name"));
      artifact.persist(getClass().getName());

      List<Change> changes = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, changes), new NullProgressMonitor());
      assertTrue(changes.isEmpty());
   }

   @org.junit.Test
   public void testReplaceWithArtifactDeleted() throws OseeCoreException, InterruptedException {
      Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(parentBranch);

      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, parentBranch, getClass().getSimpleName());
      artifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Deleted my name"));
      artifact.persist(getClass().getName());

      Branch childBranch =
         BranchManager.createWorkingBranch(parentBranch, "Branchy Deleted case branch",
            UserManager.getUser(SystemUser.OseeSystem));

      Thread.sleep(3000);

      Artifact childArtifact = ArtifactQuery.getArtifactFromId(artifact.getArtId(), childBranch);
      childArtifact.setName("New name");
      childArtifact.persist("Persist away");

      List<Change> firstChanges = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, firstChanges), new NullProgressMonitor());
      assertTrue(!firstChanges.isEmpty());

      childArtifact.deleteAndPersist();

      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(
         Collections.singleton(childArtifact)));

      List<Change> changes = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, changes), new NullProgressMonitor());
      assertTrue(changes.isEmpty());
   }

   @org.junit.Test
   public void testReplaceWithArtifactNew() throws OseeCoreException {
      Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(parentBranch);

      Branch childBranch =
         BranchManager.createWorkingBranch(parentBranch, "Branchy Deleted case branch",
            UserManager.getUser(SystemUser.OseeSystem));

      Artifact artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, childBranch, getClass().getSimpleName());
      artifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Deleted my name"));
      artifact.persist(getClass().getName());

      List<Change> firstChanges = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, firstChanges), new NullProgressMonitor());
      assertTrue(!firstChanges.isEmpty());

      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(Collections.singleton(artifact)));

      List<Change> changes = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, changes), new NullProgressMonitor());
      assertTrue(changes.isEmpty());
   }

   @org.junit.Test
   public void testReplaceWithArtifactIntroduce() throws OseeCoreException {
      Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(parentBranch);

      Branch childBranch =
         BranchManager.createWorkingBranch(parentBranch, "Branchy Deleted case branch",
            UserManager.getUser(SystemUser.OseeSystem));

      Branch sourceBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_2.getGuid());
      Assert.assertNotNull(sourceBranch);

      Artifact artifactToIntroduce =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, sourceBranch, getClass().getSimpleName());
      artifactToIntroduce.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Deleted my name"));
      artifactToIntroduce.persist(getClass().getName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(childBranch), new Artifact[] {artifactToIntroduce},
            false, false);
      Operations.executeWork(dropHandler);

      List<Change> firstChanges = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, firstChanges), new NullProgressMonitor());
      assertTrue(!firstChanges.isEmpty());

      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(
         Collections.singleton(ArtifactQuery.getArtifactFromId(artifactToIntroduce.getArtId(), childBranch))));

      List<Change> changes = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, changes), new NullProgressMonitor());
      assertTrue(changes.isEmpty());
   }

   @org.junit.Test
   public void testReplaceWithArtifactUpdateExistingIntroduce() throws OseeCoreException {
      Branch parentBranch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(parentBranch);

      Artifact artifactToIntroduce =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, parentBranch, getClass().getSimpleName());
      artifactToIntroduce.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Deleted my name"));
      artifactToIntroduce.persist(getClass().getName());

      Branch childBranch =
         BranchManager.createWorkingBranch(parentBranch, "Branchy Deleted case branch",
            UserManager.getUser(SystemUser.OseeSystem));

      Branch anotherChildBranch =
         BranchManager.createWorkingBranch(parentBranch, "Another child branch",
            UserManager.getUser(SystemUser.OseeSystem));

      Artifact childBranchArtifact =
         ArtifactQuery.getArtifactFromId(artifactToIntroduce.getArtId(), anotherChildBranch);
      childBranchArtifact.setAttributeValues(CoreAttributeTypes.Name, Collections.singletonList("Change my name"));
      childBranchArtifact.persist(getClass().getName());

      InterArtifactExplorerDropHandlerOperation dropHandler =
         new InterArtifactExplorerDropHandlerOperation(
            OseeSystemArtifacts.getDefaultHierarchyRootArtifact(childBranch), new Artifact[] {childBranchArtifact},
            false, false);
      Operations.executeWork(dropHandler);

      List<Change> firstChanges = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, firstChanges), new NullProgressMonitor());
      assertTrue(!firstChanges.isEmpty());

      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(
         Collections.singleton(ArtifactQuery.getArtifactFromId(artifactToIntroduce.getArtId(), childBranch))));

      Operations.executeWorkAndCheckStatus(new ReplaceRelationsWithBaselineOperation(
         Collections.singleton(ArtifactQuery.getArtifactFromId(artifactToIntroduce.getArtId(), childBranch))));

      List<Change> changes = new ArrayList<Change>();
      Operations.executeWork(ChangeManager.comparedToParent(childBranch, changes), new NullProgressMonitor());
      assertTrue(changes.isEmpty());
   }
}
