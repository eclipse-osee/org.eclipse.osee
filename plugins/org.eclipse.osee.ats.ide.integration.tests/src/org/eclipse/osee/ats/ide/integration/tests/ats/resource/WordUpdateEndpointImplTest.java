/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.DirtyArtifactCacheTest;
import org.eclipse.osee.define.api.WordArtifactChange;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link RenderEndpoint}
 *
 * @author David W. Miller
 */
public class WordUpdateEndpointImplTest {
   private Artifact artReqt = null;
   private BranchId branch = null;

   @AfterClass
   public static void cleanup() {
      List<Artifact> teamWorkflows = ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.TeamWorkflow,
         "Safety", AtsApiService.get().getAtsBranch(), QueryOption.CONTAINS_MATCH_OPTIONS);
      IAtsChangeSet changes = AtsApiService.get().getStoreService().createAtsChangeSet(
         WordUpdateEndpointImplTest.class.getSimpleName(), AtsCoreUsers.SYSTEM_USER);
      for (Artifact teamArt : teamWorkflows) {
         changes.deleteArtifact(teamArt);
      }
      changes.executeIfNeeded();

      new DirtyArtifactCacheTest().testArtifactCacheNotDirty();
   }

   @Before
   public void setup() {
      branch = getWorkingBranch();
      artReqt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord,
         "Claw Interface Init 15", branch);
   }

   @Test
   public void testCreate() throws Exception {
      // get word xml
      String wordData = getResource();

      List<Long> transferArts = Lists.newLinkedList();
      transferArts.add(artReqt.getId());
      wordData = wordData.replaceAll("ABCgi5iUkGgj2zhlrOgA", artReqt.getGuid());

      WordUpdateChange change = makeRequest(branch, transferArts, wordData, "Testing word update one artifact");
      validateWordUpdateChange(change);
   }

   @Test
   public void testMissingArtifact() throws Exception {
      // get word xml
      InputStream inputStream = getClass().getResourceAsStream("data/testMissingArtifact.xml");
      String wordData = Lib.inputStreamToString(inputStream);
      wordData = wordData.replaceAll("A0UNsNvCigV4SyvaCCAA", artReqt.getGuid());

      List<Long> transferArts = Lists.newLinkedList();
      transferArts.add(artReqt.getId());

      WordUpdateChange change = makeRequest(branch, transferArts, wordData, "Testing word update one artifact");
      validateWordUpdateChange(change);
   }

   private void validateWordUpdateChange(WordUpdateChange change) {
      List<WordArtifactChange> changes = change.getChangedArts();
      Assert.assertTrue(changes.size() == 1);
      WordArtifactChange wac = changes.get(0);
      Assert.assertEquals(wac.getArtId(), artReqt.getId().longValue());
   }

   private WordUpdateChange makeRequest(BranchId branch, List<Long> artifacts, String wordData, String comment) {
      WordUpdateData wud = new WordUpdateData();
      wud.setWordData(wordData.getBytes());
      wud.setArtifacts(artifacts);
      wud.setBranch(branch);
      wud.setThreeWayMerge(false);
      wud.setComment(comment);
      wud.setMultiEdit(false);
      wud.setUserArtId(UserManager.getUser());

      return PublishingRequestHandler.updateWordArtifacts(wud);
   }

   private BranchId getWorkingBranch() {
      BranchId branchReturn = null;
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING);
      for (BranchToken branch : BranchManager.getBranches(branchFilter)) {
         if (branch.getName().contains("More Reqt")) {
            branchReturn = branch;
            break;
         }
      }
      return branchReturn;
   }

   private String getResource() throws Exception {
      InputStream inputStream = getClass().getResourceAsStream("data/testSoftwareRequirement.xml");
      return Lib.inputStreamToString(inputStream);
   }

}
