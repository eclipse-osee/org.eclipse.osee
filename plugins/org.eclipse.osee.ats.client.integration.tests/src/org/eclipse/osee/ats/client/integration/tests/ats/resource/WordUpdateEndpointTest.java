/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import java.io.InputStream;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.define.report.api.WordArtifactChange;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.define.report.api.WordUpdateEndpoint;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpWordUpdateRequest;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.Lists;

/**
 * Test unit for {@link WordUpdateEndpoint}
 *
 * @author David W. Miller
 */
public class WordUpdateEndpointTest extends AbstractRestTest {
   private Artifact artReqt = null;
   private Branch branch = null;

   @Before
   public void setup() {
      branch = getWorkingBranch();
      artReqt =
         ArtifactQuery.getArtifactFromTypeAndAttribute(CoreArtifactTypes.SoftwareRequirement, CoreAttributeTypes.Name,
            "Claw Interface Init 15", branch);
   }

   @Test(expected = OseeWebApplicationException.class)
   public void testInvalidData() throws Exception {
      HttpWordUpdateRequest.updateWordArtifacts(null);
   }

   @Test(expected = OseeWebApplicationException.class)
   public void testInvalidDataElements() throws Exception {
      WordUpdateData wud = new WordUpdateData();
      wud.setBranch(570L);
      wud.setThreeWayMerge(false);
      wud.setComment("other data invalid");
      wud.setMultiEdit(false);
      wud.setUserArtId((long) UserManager.getUser().getArtId());
      HttpWordUpdateRequest.updateWordArtifacts(wud);
   }

   @Test(expected = OseeWebApplicationException.class)
   public void testBadGuid() throws Exception {
      // get word xml
      String wordData = getResource();
      // get branch and artifact
      Branch branch = getWorkingBranch();
      List<Long> transferArts = Lists.newLinkedList();
      transferArts.add(artReqt.getUuid());
      makeRequest(branch.getUuid(), transferArts, wordData, "Testing word update one artifact");
   }

   @Test
   public void testCreate() throws Exception {
      // get word xml
      String wordData = getResource();

      List<Long> transferArts = Lists.newLinkedList();
      transferArts.add(artReqt.getUuid());
      wordData = wordData.replaceAll("ABCgi5iUkGgj2zhlrOgA", artReqt.getGuid());

      WordUpdateChange change =
         makeRequest(branch.getUuid(), transferArts, wordData, "Testing word update one artifact");
      validateWordUpdateChange(change);
      validateSafetyTeamWFExists();
   }

   @Test
   public void testMissingArtifact() throws Exception {
      // get word xml
      InputStream inputStream = getClass().getResourceAsStream("data/testMissingArtifact.xml");
      String wordData = Lib.inputStreamToString(inputStream);
      wordData = wordData.replaceAll("A0UNsNvCigV4SyvaCCAA", artReqt.getGuid());

      List<Long> transferArts = Lists.newLinkedList();
      transferArts.add(artReqt.getUuid());

      WordUpdateChange change =
         makeRequest(branch.getUuid(), transferArts, wordData, "Testing word update one artifact");
      validateWordUpdateChange(change);
   }

   private void validateWordUpdateChange(WordUpdateChange change) {
      List<WordArtifactChange> changes = change.getChangedArts();
      Assert.assertTrue(changes.size() == 1);
      WordArtifactChange wac = changes.get(0);
      Assert.assertTrue(wac.getArtId() == artReqt.getUuid());
   }

   private void validateSafetyTeamWFExists() {
      boolean exists = false;
      List<Artifact> teamWorkflows =
         ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.TeamWorkflow, CoreBranches.COMMON);
      for (Artifact art : teamWorkflows) {
         if (art.getName().contains("Safety Workflow for")) {
            exists = true;
            break;
         }
      }
      Assert.assertTrue(exists);
   }

   private WordUpdateChange makeRequest(long branchId, List<Long> artifacts, String wordData, String comment) {
      byte[] data = wordData.getBytes();
      WordUpdateData wud = new WordUpdateData();
      wud.setWordData(data);
      wud.setArtifacts(artifacts);
      wud.setBranch(branchId);
      wud.setThreeWayMerge(false);
      wud.setComment(comment);
      wud.setMultiEdit(false);
      wud.setUserArtId((long) UserManager.getUser().getArtId());

      WordUpdateChange change = HttpWordUpdateRequest.updateWordArtifacts(wud);
      return change;
   }

   private Branch getWorkingBranch() {
      Branch branchReturn = null;
      BranchFilter branchFilter = new BranchFilter(BranchType.WORKING);
      List<Branch> branches = BranchManager.getBranches(branchFilter);
      for (Branch branch : branches) {
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
