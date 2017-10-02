/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.httpRequests.PurgeBranchHttpRequestOperation;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceArtifactWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceAttributeWithBaselineOperation;
import org.eclipse.osee.framework.ui.skynet.update.InterArtifactExplorerDropHandlerOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * <pre>
 *                   New   |  Deleted  | Modified  |  Moved |  Introduced | RelationOrderAttr
 *                 -------------------------------------------------------------------------
 *     Artifact   |  0           1            3                  2
 *     Attribute  |  4           5            6
 *     Relation   |  7           8                     9*                        **
 *                 -------------------------------------------------------------------------
 *
 *
 *    Legend:
 *    * -   artifact will be left with a DELETED attribute change and a SYNTHETIC
 *          modified artifact change. CASE_10_EXPECTED. i.e.:
 *
 *          A           C-
 *          `- B   ->   `- B        B is moved to C
 *                                  C gets a new user defined order attribute
 *
 *                                  Revert on B causes A to have a new relation order attribute because
 *                                  B was the only child of A and when the link was deleted the relation order
 *                                  attribute was also deleted. (link.deleteEmptyRelationOrder()) After the B
 *                                  link is added back from the revert of B. A will get a new relation order attribute.
 *
 *    ** - creates a parent artifact A on the baseline branch and adds 5 children. On the working branch
 *                                  the same artifact A gets additional 5 children. The revert is called A.
 *                                  Which will set the parent (A) back to the baseline state but leaves
 *                                  the new artifacts orphaned on the working branch.
 *
 *    Case 10 is a collection of 1-9, all cases.
 * </pre>
 */
@RunWith(Parameterized.class)
public final class ReplaceWithBaselineTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final RelationSorter ascOrder = LEXICOGRAPHICAL_ASC;
   private static final RelationSorter descOrder = LEXICOGRAPHICAL_DESC;

   /**
    * Refer to table for Case 10*
    */
   private static final int CASE_10_EXPECTED = 2;
   private static final int CASE_0_EXPECTED = 10;
   private static final IArtifactType DOC = CoreArtifactTypes.GeneralDocument;
   private static final IProgressMonitor MONITOR = new NullProgressMonitor();

   //@formatter:off
   private static enum Item { ARTIFACT, ATTRBUTE, RELATION, };
   private static enum ChangeItem { NEW, DELETED, MODIFIED, MOVED, INTRODUCED, RELATION_ORDER_ATTR };
   //@formatter:on

   private BranchId workingBranch;
   private BranchId baselineBranch;

   private final String testName;
   private final int expectedChangesLeft;
   private final List<TestData> testDatas;

   public ReplaceWithBaselineTest(String testCaseName, List<TestData> testDatas, int expectedChangesLeft) {
      this.testName = String.format("%s : %s", ReplaceWithBaselineTest.class.getSimpleName(), testCaseName);
      this.testDatas = testDatas;
      this.expectedChangesLeft = expectedChangesLeft;
   }

   @After
   public void tearDown() throws Exception {
      Operations.executeWorkAndCheckStatus(new PurgeBranchHttpRequestOperation(workingBranch, true));
   }

   @Test
   public void testReplaceWithBaselineVersion() throws Exception {
      runTest();
      Collection<Change> changes = getBranchChanges(workingBranch);
      switch (expectedChangesLeft) {
         case CASE_10_EXPECTED:
            Assert.assertTrue(changes.size() == CASE_10_EXPECTED);
            break;
         case CASE_0_EXPECTED:
            Assert.assertTrue(changes.size() == CASE_0_EXPECTED);
            break;
         case CASE_0_EXPECTED + CASE_10_EXPECTED:
            Assert.assertTrue(changes.size() == CASE_0_EXPECTED + CASE_10_EXPECTED);
            break;
         default:
            Assert.assertTrue(changes.isEmpty());
            break;
      }
   }

   @Parameters
   public static List<Object[]> getData() {

      List<Object[]> data = new LinkedList<>();

      data.add(new Object[] {"Case 0", Arrays.asList(new TestData(Item.ARTIFACT, ChangeItem.NEW, false)), 0});
      data.add(new Object[] {"Case 1", Arrays.asList(new TestData(Item.ARTIFACT, ChangeItem.DELETED, true)), 0});
      data.add(new Object[] {"Case 2", Arrays.asList(new TestData(Item.ARTIFACT, ChangeItem.INTRODUCED, false)), 0});
      data.add(new Object[] {"Case 3", Arrays.asList(new TestData(Item.ARTIFACT, ChangeItem.MODIFIED, true)), 0});

      data.add(new Object[] {"Case 4", Arrays.asList(new TestData(Item.ATTRBUTE, ChangeItem.NEW, true)), 0});
      data.add(new Object[] {"Case 5", Arrays.asList(new TestData(Item.ATTRBUTE, ChangeItem.DELETED, true)), 0});
      data.add(new Object[] {"Case 6", Arrays.asList(new TestData(Item.ATTRBUTE, ChangeItem.MODIFIED, true)), 0});

      List<TestData> combinedCases = new ArrayList<>(data.size());

      for (Object[] objects : data) {
         @SuppressWarnings("unchecked")
         List<TestData> caseTestDatas = (List<TestData>) objects[1];
         combinedCases.addAll(caseTestDatas);
      }
      data.add(new Object[] {"Case 7", combinedCases, 0});
      Collections.reverse(combinedCases);
      data.add(new Object[] {"Case 8", combinedCases, 0});

      return data;
   }

   /**
    * Sets up the test based on <code>ReplaceWithBaselineTest.testItem</code> and performs a revert.
    */
   private void runTest() throws Exception {
      setupBackgroundForScenario();

      makeChange(testDatas);

      Collection<Change> changesAfterRevert = getBranchChanges(workingBranch);

      List<Artifact> artifactsToRevert = new ArrayList<>();
      Artifact artifactToRevert;
      for (TestData testData : testDatas) {
         artifactToRevert =
            ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch, DeletionFlag.INCLUDE_DELETED);

         switch (testData.item) {
            case ARTIFACT:
               artifactsToRevert.add(artifactToRevert);
               break;

            case ATTRBUTE:
               revertAttributes(changesAfterRevert, artifactToRevert.getAttributeById(testData.getAttrId(), true));
               break;

            case RELATION:
               artifactsToRevert.add(artifactToRevert);
               break;
         }
      }

      revertArtifacts(changesAfterRevert, artifactsToRevert); //Only if not attributes
   }

   private void setupBackgroundForScenario() throws Exception {
      baselineBranch = SAW_Bld_1;

      //Setup data before the working branch has been created
      for (TestData testData : this.testDatas) {

         if (testData.isBaseline) {
            testData.setArtifactId(createNewArtifact(baselineBranch, GUID.create()));
         }

         switch (testData.item) {
            case ARTIFACT:
               break;
            case ATTRBUTE:
               break;
            case RELATION:
               switch (testData.changeItem) {
                  case NEW:
                     //setup a and b artifacts so a new relation can be created.
                     testData.setbArtifactId(createNewArtifact(baselineBranch, GUID.create()).getArtId());
                     testData.setRelationType(CoreRelationTypes.SupportingInfo_SupportingInfo);
                     break;
                  case DELETED:
                     //set up relation to be deleted
                     Artifact c = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), baselineBranch);
                     Artifact d = createNewArtifact(baselineBranch, GUID.create());

                     testData.setbArtifactId(d.getArtId());

                     RelationManager.addRelation(CoreRelationTypes.SupportingInfo_SupportingInfo, c, d,
                        "Supporting info for c to d");

                     testData.setRelationType(CoreRelationTypes.SupportingInfo_SupportingInfo);
                     c.persist(testName);
                     break;
                  case MOVED:
                     //setup parent child relation so it can be moved on the working branch
                     Artifact parent = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), baselineBranch);
                     Artifact child = createNewArtifact(baselineBranch, GUID.create());
                     Artifact newParent = createNewArtifact(baselineBranch, GUID.create());

                     testData.setbArtifactId(child.getArtId());
                     testData.setcArtId(newParent.getArtId());

                     parent.addChild(USER_DEFINED, child);

                     parent.persist(testName);

                     testData.setRelationType(CoreRelationTypes.Default_Hierarchical__Child);
                     break;
                  case RELATION_ORDER_ATTR:
                     Artifact parentArtifact = createNewArtifact(baselineBranch, "parent");

                     int numOfChildren = 5;

                     for (int i = 0; i < numOfChildren; i++) {
                        parentArtifact.addRelation(ascOrder, CoreRelationTypes.Default_Hierarchical__Child,
                           createNewArtifact(baselineBranch, GUID.create()));
                     }
                     parentArtifact.persist(testName);
                     testData.setArtifactId(parentArtifact);
                     break;
               }
               break;
         }
      }

      workingBranch = BranchManager.createWorkingBranch(baselineBranch, "working branch", SystemUser.OseeSystem);

      SkynetTransaction workingBranchTransaction = TransactionManager.createTransaction(workingBranch, testName);

      workingBranchTransaction.execute();

      //Setup data on the working branch for testing
      for (TestData testData : testDatas) {
         switch (testData.item) {
            case ATTRBUTE:
               if (testData.changeItem == ChangeItem.NEW) {
                  Artifact newArtifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                  newArtifact.addAttributeFromString(CoreAttributeTypes.ParagraphNumber, "1.0");
                  newArtifact.persist(testName);
                  testData.setAttrId(newArtifact.getSoleAttribute(CoreAttributeTypes.ParagraphNumber));

               }
               break;
            case ARTIFACT:
               switch (testData.changeItem) {
                  case NEW:
                     testData.setArtifactId(createNewArtifact(workingBranch, GUID.create()));
                     break;
                  case INTRODUCED:
                     BranchId anotherBranch = BranchManager.createWorkingBranch(workingBranch, "another working branch",
                        SystemUser.OseeSystem);

                     Artifact artifactToIntroduce = createNewArtifact(anotherBranch, "introduce artifact");

                     InterArtifactExplorerDropHandlerOperation dropHandler =
                        new InterArtifactExplorerDropHandlerOperation(
                           OseeSystemArtifacts.getDefaultHierarchyRootArtifact(workingBranch),
                           new Artifact[] {artifactToIntroduce}, false);
                     Operations.executeWork(dropHandler);

                     testData.setArtifactId(artifactToIntroduce);
               }
               break;
            case RELATION:
               if (testData.changeItem == ChangeItem.NEW) {
                  Artifact a = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                  Artifact b = ArtifactQuery.getArtifactFromId(testData.getbArtifactId(), workingBranch);
                  RelationManager.addRelation(testData.getRelationType(), a, b, "Link add for testing");
                  a.persist(testName);
               }
               break;
         }
      }

   }

   private void makeChange(List<TestData> datas) throws Exception {
      Artifact artifact = null;
      Attribute<?> attribute = null;

      for (TestData testData : testDatas) {

         switch (testData.item) {
            case ATTRBUTE:
               switch (testData.changeItem) {
                  case NEW:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     attribute = artifact.getAttributeById(testData.getAttrId(), true);
                     artifact.persist(testName);
                     break;
                  case DELETED:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     attribute = artifact.getSoleAttribute(CoreAttributeTypes.Name);
                     attribute.delete();
                     artifact.persist(testName);

                     testData.setAttrId(attribute);
                     break;
                  case MODIFIED:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     attribute = artifact.getSoleAttribute(CoreAttributeTypes.Name);
                     attribute.setFromString("I am changed");
                     artifact.persist(testName);
                     testData.setAttrId(attribute);
                     break;
               }
               break;
            case ARTIFACT:
               switch (testData.changeItem) {
                  case DELETED:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     artifact.delete();
                     artifact.persist(testName);
                     break;
                  case NEW:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     artifact.persist(testName);
                     break;
                  case INTRODUCED:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     artifact.persist(testName);
                     break;
                  case MODIFIED:
                     artifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     artifact.addAttribute(CoreAttributeTypes.Active, true);
                     artifact.persist(testName);
                     break;
               }
               break;
            case RELATION:
               switch (testData.changeItem) {
                  case NEW:
                     Artifact a = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     Artifact b = ArtifactQuery.getArtifactFromId(testData.getbArtifactId(), workingBranch);

                     RelationManager.addRelation(testData.getRelationType(), a, b, "This is a new relation");
                     a.persist(testName);
                     break;

                  case DELETED:
                     Artifact c = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     Artifact d = ArtifactQuery.getArtifactFromId(testData.getbArtifactId(), workingBranch);

                     RelationManager.deleteRelation(testData.getRelationType(), c, d);
                     c.persist(testName);
                     break;

                  case MOVED:
                     Artifact parent = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);
                     Artifact child = ArtifactQuery.getArtifactFromId(testData.getbArtifactId(), workingBranch);
                     Artifact newParent = ArtifactQuery.getArtifactFromId(testData.getcArtId(), workingBranch);

                     parent.deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);
                     parent.persist(testName);
                     newParent.addChild(USER_DEFINED, child);
                     newParent.persist(testName);

                     //This is to revert the child artifact
                     testData.setArtifactId(child);
                     child.persist(testName);
                     break;
                  case RELATION_ORDER_ATTR:
                     Artifact parentArtifact = ArtifactQuery.getArtifactFromId(testData.getArtifactId(), workingBranch);

                     int numOfChildren = 5;

                     for (int i = 0; i < numOfChildren; i++) {
                        parentArtifact.addRelation(descOrder, CoreRelationTypes.Default_Hierarchical__Child,
                           createNewArtifact(workingBranch, GUID.create()));
                     }
                     parentArtifact.persist(testName);
                     break;
               }
               break;
         }
      }

   }

   private Artifact createNewArtifact(BranchId branch, String name) throws Exception {
      Artifact artifact = ArtifactTypeManager.addArtifact(DOC, branch, name);
      artifact.persist(testName + name);
      return artifact;
   }

   private void revertArtifacts(Collection<Change> changes, Collection<Artifact> artifactsToRevert) {
      Operations.executeWorkAndCheckStatus(new ReplaceArtifactWithBaselineOperation(changes, artifactsToRevert));
   }

   private void revertAttributes(Collection<Change> changes, Attribute<?> attributeToRevert) {
      List<Change> attrChanges = new ArrayList<>(1);

      for (Change change : changes) {
         if (change.getChangeType() == LoadChangeType.attribute && change.getItemId().getId().intValue() == attributeToRevert.getId()) {
            attrChanges.add(change);
         }
      }
      Operations.executeWorkAndCheckStatus(new ReplaceAttributeWithBaselineOperation(attrChanges));
   }

   private List<Change> getBranchChanges(BranchId branch) {
      List<Change> changes = new ArrayList<>();
      Operations.executeWorkAndCheckStatus(ChangeManager.comparedToParent(branch, changes), MONITOR);
      return changes;
   }

   void createArtifact(String name) {
      ArtifactTypeManager.addArtifact(DOC, workingBranch, name);
   }

   private static class TestData {
      public final Item item;
      public final ChangeItem changeItem;
      public final boolean isBaseline;

      private ArtifactId artifactId;
      private RelationTypeSide relationType;
      private int bArtifactId;

      private AttributeId attrId;

      private int cArtId;

      public TestData(Item item, ChangeItem changeItem, boolean isBaseline) {
         this.item = item;
         this.changeItem = changeItem;
         this.isBaseline = isBaseline;
      }

      public int getcArtId() {
         return cArtId;
      }

      public void setcArtId(int cArtId) {
         this.cArtId = cArtId;
      }

      public AttributeId getAttrId() {
         return attrId;
      }

      public void setAttrId(AttributeId attrId) {
         this.attrId = attrId;
      }

      public RelationTypeSide getRelationType() {
         return relationType;
      }

      public void setRelationType(RelationTypeSide relationType) {
         this.relationType = relationType;
      }

      public int getbArtifactId() {
         return bArtifactId;
      }

      public void setbArtifactId(int bArtifactId) {
         this.bArtifactId = bArtifactId;
      }

      public ArtifactId getArtifactId() {
         return artifactId;
      }

      public void setArtifactId(ArtifactId artId) {
         artifactId = artId;
      }

      @Override
      public String toString() {
         String message =
            "Item: %s ChangeItem %s isBaseline %s ArtifactId %s RelationType %s artifactId %s bArtifactId %s";
         return String.format(message, item, changeItem, isBaseline, artifactId, relationType, artifactId, bArtifactId);
      }
   }
}
