/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.SENTINEL;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Component;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SoftwareRequirementMsWord;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Description;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Parent;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_PL;
import static org.eclipse.osee.framework.core.enums.ModificationType.APPLICABILITY;
import static org.eclipse.osee.framework.core.enums.ModificationType.MODIFIED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Branden W. Phillips
 */
public class RevisionChangeTest {

   @Rule
   public TestInfo method = new TestInfo();

   private final ArtifactToken chassisDeleted = ArtifactToken.valueOf(200016L, "Chassis", Component);
   private final ArtifactToken controlsToModifyAttrs = ArtifactToken.valueOf(200021L, "Controls", Component);
   private final ArtifactToken navigationToModifyApplic = ArtifactToken.valueOf(200023L, "Navigation", Component);

   private BranchToken workingBranch;
   private Artifact sawProductDecomp;
   private Artifact newSoftReq;

   @Before
   public void setUp() {
      workingBranch = BranchManager.createWorkingBranch(SAW_PL, "Test Working Branch");
      sawProductDecomp =
         ArtifactQuery.getArtifactFromTypeAndName(Component, CoreArtifactTokens.SAW_PRODUCT_DECOMP, workingBranch);

      newSoftReq = ArtifactTypeManager.addArtifact(SoftwareRequirementMsWord, workingBranch);
      newSoftReq.setName("New Software Requirement");
      newSoftReq.addAttribute(ParagraphNumber);
      newSoftReq.setSoleAttributeFromString(ParagraphNumber, "1.1");
      newSoftReq.addRelation(DefaultHierarchical_Parent, sawProductDecomp);
      newSoftReq.persist("Add New Software Requirement");

      Artifact chassisDeletedArt = ArtifactQuery.getArtifactFromId(chassisDeleted, workingBranch);
      chassisDeletedArt.deleteAndPersist("Delete Chassis");

      Artifact controlsToModifyAttrsArt = ArtifactQuery.getArtifactFromId(controlsToModifyAttrs, workingBranch);
      controlsToModifyAttrsArt.addAttribute(Description);
      controlsToModifyAttrsArt.setSoleAttributeFromString(Description, "New Description");
      controlsToModifyAttrsArt.persist("Add Description Attribute");
      controlsToModifyAttrsArt.setSoleAttributeFromString(Description, "Modified Description");
      controlsToModifyAttrsArt.persist("Modify Description Attribute");

      ApplicabilityEndpoint appl = ServiceUtil.getOseeClient().getApplicabilityEndpoint(workingBranch);
      Collection<ApplicabilityToken> appTokens = appl.getApplicabilityTokens();
      ApplicabilityToken robotIncluded = appTokens.stream().filter(
         appToken -> "ROBOT_ARM_LIGHT = Included".equals(appToken.getName())).findAny().orElse(SENTINEL);

      List<ArtifactToken> list = new ArrayList<>();
      list.add(navigationToModifyApplic);
      ServiceUtil.getOseeClient().getApplicabilityEndpoint(workingBranch).setApplicability(robotIncluded, list);
   }

   /**
    * This is for testing an artifact that was introduced on the branch. Testing for the following cases. <br/>
    * 1. There are no was values since everything should be a new change <br/>
    * 2. The only modification types should be applicability, or new <br/>
    * 3. That the applicability modification was found <br/>
    * 4. The changes are current since this is the most recent change, this will allow the artifact to be opened and
    * editable <br/>
    * 5. There should only be 1 transaction in the changes
    */
   @Test
   public void testNewArtifact() {
      List<Change> changes = new ArrayList<>();
      Artifact newSoftReqArt = ArtifactQuery.getArtifactFromId(newSoftReq, workingBranch);
      boolean containsEmptyWas = true, newModTypesOnly = true, applicabilityMod = false, isCurrent = true;
      Set<TransactionToken> txs = new HashSet<>();

      changes.addAll(ChangeManager.getChangesPerArtifact(newSoftReqArt, 25, null));
      assertFalse(changes.isEmpty());

      for (Change change : changes) {
         TransactionToken tx = change.getTxDelta().getEndTx();
         if (!txs.contains(tx)) {
            txs.add(tx);
         }
         String wasValue = change.getWasValue();
         if (!wasValue.isEmpty()) {
            containsEmptyWas = false;
         }
         ModificationType modType = change.getModificationType();
         if (modType.equals(APPLICABILITY)) {
            applicabilityMod = true;
         } else if (!modType.equals(NEW)) {
            newModTypesOnly = false;
         }
         isCurrent = !change.isHistorical();
      }

      assertTrue(containsEmptyWas);
      assertTrue(newModTypesOnly);
      assertTrue(applicabilityMod);
      assertTrue(isCurrent);
      assertEquals(txs.size(), 1);
   }

   /**
    * This is for testing an artifact that is deleted on the branch. This is utilizing the feature of only getting the
    * most recent change by setting transactions to 1. Testing for the following cases <br/>
    * 1. The artifact change found has a deleted modification type <br/>
    * 2. The only other modification types found should be applicability, or artifact deleted. Things such as modified
    * or new are not expected.
    */
   @Test
   public void testDeletedArtifact() {
      List<Change> changes = new ArrayList<>();
      Artifact chassisDeletedArt = ArtifactQuery.getArtifactFromId(chassisDeleted, workingBranch, INCLUDE_DELETED);
      boolean artifactDeleted = false, modTypeArtDeleted = true;

      changes.addAll(ChangeManager.getChangesPerArtifact(chassisDeletedArt, 1, null));
      Collections.reverse(changes);
      assertFalse(changes.isEmpty());

      for (Change change : changes) {
         ModificationType modType = change.getModificationType();
         if (change.getChangeType().isArtifactChange() && modType.isDeleted()) {
            artifactDeleted = true;
         } else if (!modType.equals(APPLICABILITY) && !modType.isArtifactDeleted()) {
            modTypeArtDeleted = false;
         }
      }

      assertTrue(artifactDeleted);
      assertTrue(modTypeArtDeleted);
   }

   /**
    * This is for testing an artifact that has attributes being modified. Testing for the following cases <br/>
    * 1. There should be 2 cases of artifact changes with a modified modification type, these are for both attribute
    * changes <br/>
    * 2. There should be 1 case of an attribute having a modification type of modified, the other cases are of type new
    * <br/>
    * 3. For the modified attribute, both the was and is values should not be empty
    */
   @Test
   public void testModifiedAttributes() {
      List<Change> changes = new ArrayList<>();
      Artifact controlsToModifyAttrsArt = ArtifactQuery.getArtifactFromId(controlsToModifyAttrs, workingBranch);
      List<Change> artModList = new ArrayList<>();
      List<Change> attrModList = new ArrayList<>();
      boolean attrModWasIsValues = true;

      changes.addAll(ChangeManager.getChangesPerArtifact(controlsToModifyAttrsArt, 25, null));
      assertFalse(changes.isEmpty());

      for (Change change : changes) {
         ChangeType changeType = change.getChangeType();
         ModificationType modType = change.getModificationType();

         if (changeType.isArtifactChange() && modType.equals(MODIFIED)) {
            artModList.add(change);
         } else if (changeType.isAttributeChange() && modType.equals(MODIFIED)) {
            attrModList.add(change);
            if (change.getWasValue().isEmpty() || change.getIsValue().isEmpty()) {
               attrModWasIsValues = false;
            }
         }
      }

      assertEquals(artModList.size(), 2);
      assertEquals(attrModList.size(), 1);
      assertTrue(attrModWasIsValues);
   }

   /**
    * This tests for an artifact that has modified applicability. Special processing is being done for the first
    * transaction since that is the modified applicability transaction. After that, this test is being utilized to also
    * make sure applicability changes are showing up on other attributes. The following cases are being tested <br/>
    * 1. The first transaction has an artifact change with a modified modification type <br/>
    * 2. The first transaction has an artifact change with the applicability modification type, along with this, the was
    * and is values should not be empty <br/>
    * 3. In total, there should be 4 changes that have a modification type of applicability
    */
   @Test
   public void testModifiedApplicability() {
      List<Change> changes = new ArrayList<>();
      Artifact navigationToModifyApplicArt = ArtifactQuery.getArtifactFromId(navigationToModifyApplic, workingBranch);
      List<Change> applicMods = new ArrayList<>();
      boolean foundArtMod = false, foundCorrectArtApplic = false;

      changes.addAll(ChangeManager.getChangesPerArtifact(navigationToModifyApplicArt, 25, null));
      Collections.reverse(changes);
      assertFalse(changes.isEmpty());

      boolean recentChange = true;
      TransactionToken firstTx = null;
      for (Change change : changes) {
         ChangeType changeType = change.getChangeType();
         ModificationType modType = change.getModificationType();
         if (recentChange) {
            if (firstTx == null) {
               firstTx = change.getTxDelta().getEndTx();
            } else {
               if (!firstTx.equals(change.getTxDelta().getEndTx())) {
                  recentChange = false;
               }
            }
            if (changeType.isArtifactChange() && modType.equals(MODIFIED)) {
               foundArtMod = true;
            }
            if (changeType.isArtifactChange() && modType.equals(APPLICABILITY)) {
               if (!change.getWasValue().isEmpty() && !change.getIsValue().isEmpty()) {
                  foundCorrectArtApplic = true;
               }
               applicMods.add(change);
            }
            continue;
         }
         if (changeType.isArtifactChange() && modType.equals(APPLICABILITY)) {
            applicMods.add(change);
         }
      }

      assertTrue(foundArtMod);
      assertTrue(foundCorrectArtApplic);
      assertEquals(applicMods.size(), 4);
   }

   /**
    * This is testing the relation changes found on the artifact being used as the parent for the other tests.
    * Specifically, the new and deleted artifacts. The most recent 2 transactions are the only ones being taken. The
    * following cases are being tested <br/>
    * 1. Any artifact change must be of modification type modified since they are modified relations <br/>
    * 2. For one relation change, it must have a modification type of artifact deleted <br/>
    * 3. For the other relation change, it must have a modification type of new
    */
   @Test
   public void testModifiedRelations() {
      List<Change> changes = new ArrayList<>();
      boolean artifactsModified = true, foundRelationArtDeleted = false, foundRelationNew = false;

      changes.addAll(ChangeManager.getChangesPerArtifact(sawProductDecomp, 2, null));
      assertFalse(changes.isEmpty());

      for (Change change : changes) {
         ChangeType changeType = change.getChangeType();
         ModificationType modType = change.getModificationType();

         if (changeType.isArtifactChange()) {
            if (!modType.equals(MODIFIED)) {
               artifactsModified = false;
            }
         } else if (changeType.isRelationChange()) {
            if (modType.isArtifactDeleted()) {
               foundRelationArtDeleted = true;
            } else if (modType.equals(NEW)) {
               foundRelationNew = true;
            }
         }
      }

      assertTrue(artifactsModified);
      assertTrue(foundRelationArtDeleted);
      assertTrue(foundRelationNew);
   }

   /**
    * This test is testing edge cases for the numberTransactionsToShow variable. The following cases are being tested
    * <br/>
    * 1. -1 being entered, this will default to as many changes as can be found <br/>
    * 2. 0 being entered, this will default to as many changes as can be found <br/>
    * 3. 1 being entered, this will return only 1 transactions worth of changes <br/>
    * 4. 50 being entered, for this case, this will return all 4 transactions worth of changes
    */
   @Test
   public void testNumberTransactionsToShow() {
      List<Change> changes = new ArrayList<>();
      Set<TransactionToken> txs = new HashSet<>();
      Artifact navigationToModifyApplicArt = ArtifactQuery.getArtifactFromId(navigationToModifyApplic, workingBranch);

      changes.addAll(ChangeManager.getChangesPerArtifact(navigationToModifyApplicArt, -1, null));
      assertFalse(changes.isEmpty());
      for (Change change : changes) {
         TransactionToken tx = change.getTxDelta().getEndTx();
         if (!txs.contains(tx)) {
            txs.add(tx);
         }
      }
      assertEquals(txs.size(), 4);

      changes.clear();
      txs.clear();
      changes.addAll(ChangeManager.getChangesPerArtifact(navigationToModifyApplicArt, 0, null));
      assertFalse(changes.isEmpty());
      for (Change change : changes) {
         TransactionToken tx = change.getTxDelta().getEndTx();
         if (!txs.contains(tx)) {
            txs.add(tx);
         }
      }
      assertEquals(txs.size(), 4);

      changes.clear();
      txs.clear();
      changes.addAll(ChangeManager.getChangesPerArtifact(navigationToModifyApplicArt, 1, null));
      assertFalse(changes.isEmpty());
      for (Change change : changes) {
         TransactionToken tx = change.getTxDelta().getEndTx();
         if (!txs.contains(tx)) {
            txs.add(tx);
         }
      }
      assertEquals(txs.size(), 1);

      changes.clear();
      txs.clear();
      changes.addAll(ChangeManager.getChangesPerArtifact(navigationToModifyApplicArt, 50, null));
      assertFalse(changes.isEmpty());
      for (Change change : changes) {
         TransactionToken tx = change.getTxDelta().getEndTx();
         if (!txs.contains(tx)) {
            txs.add(tx);
         }
      }
      assertEquals(txs.size(), 4);
   }

   @After
   public void tearDown() {
      BranchManager.purgeBranch(workingBranch);
   }
}
