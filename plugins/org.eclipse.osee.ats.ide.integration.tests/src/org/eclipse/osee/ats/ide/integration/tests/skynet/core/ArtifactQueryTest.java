/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ExceptionLogBlocker;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactMatch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactSearchCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeCriteria;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchOptions;
import org.eclipse.osee.framework.skynet.core.artifact.search.SearchRequest;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * @author Donald G. Dunne
 */
public class ArtifactQueryTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   @Rule
   public ExitDatabaseInitializationRule exitDatabaseInitializationRule = new ExitDatabaseInitializationRule();

   @Test
   public void testGetArtifactFromGUIDDeleted() {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, DemoBranches.CIS_Bld_1);
      newArtifact.persist(getClass().getSimpleName());

      // Should exist
      Artifact searchedArtifact = ArtifactQuery.getArtifactFromToken(newArtifact);
      Assert.assertNotNull(searchedArtifact);

      // Should exist with allowDeleted
      searchedArtifact =
         ArtifactQuery.getArtifactFromId(newArtifact, DemoBranches.CIS_Bld_1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNotNull(searchedArtifact);

      newArtifact.deleteAndPersist(getClass().getSimpleName());

      try {
         Artifact ret = ArtifactQuery.checkArtifactFromId(newArtifact, DemoBranches.CIS_Bld_1);
         Assert.assertNull(ret);
      } catch (ArtifactDoesNotExist ex) {
         Assert.fail("ArtifactQuery should never throw ArtifactDoesNotExist with QueryType.CHECK");
      }

      // Should NOT exist, cause deleted
      try {
         ArtifactQuery.getArtifactFromId(newArtifact.getGuid(), DemoBranches.CIS_Bld_1);
         Assert.fail("artifact query should have thrown does not exist exception");
      } catch (ArtifactDoesNotExist ex) {
         // do nothing, this is the expected case
      }

      // Should still exist with allowDeleted
      searchedArtifact =
         ArtifactQuery.getArtifactFromId(newArtifact, DemoBranches.CIS_Bld_1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNotNull(searchedArtifact);

   }

   @Test
   public void testGetArtifactListFromType() {
      // Should exist
      Set<Artifact> searchedArtifacts = new LinkedHashSet<>();
      for (BranchId branch : BranchManager.getBranches(new BranchFilter(BranchType.BASELINE))) {
         List<Artifact> results = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirementMsWord,
            branch, DeletionFlag.INCLUDE_DELETED);
         searchedArtifacts.addAll(results);
      }
      // make sure at least one artifact exists
      Assert.assertTrue("No artifacts found", searchedArtifacts.size() > 0);

      //check to see if there are multiple branches found
      BranchId firstId = BranchId.SENTINEL;
      Boolean pass = false;
      for (Artifact a : searchedArtifacts) {
         if (firstId.isInvalid()) {
            firstId = a.getBranch();
         } else {
            if (firstId.notEqual(a.getBranch())) {
               pass = true;
               break;
            }
         }
      }
      Assert.assertTrue("No artifacts on multiple branches found", pass);
   }

   @Test
   public void testNotTaggableGetArtifactListFromAttributeType() {
      List<ArtifactSearchCriteria> criteria = new ArrayList<>();

      criteria.add(new AttributeCriteria(CoreAttributeTypes.FavoriteBranch, "DemoBranches.CIS_Bld_1",
         QueryOption.TOKEN_DELIMITER__ANY));

      // test against a couple of attributes types that are not taggable; expect exception

      //@formatter:off
      try(
            var exceptionLogBlocker =
               new ExceptionLogBlocker
                      (
                         "org.eclipse.osee.framework.jdk.core.type.OseeArgumentException",
                         null,
                         "javax.ws.rs.InternalServerErrorException",
                         null //"Attribute types \\[\\[[0-9]+\\]\\] is not taggable"
                      )
         )
      {
         try {
            ArtifactQuery.getArtifactListFromCriteria(DemoBranches.CIS_Bld_1, 1000, criteria);
            exceptionLogBlocker.assertNoException();
         } catch (Exception e) {
            exceptionLogBlocker.assertExpectedExceptionNoServerExceptionClassNameChecks(e);
         }
      }
      //@formatter:on

      //@formatter:off
      try(
            var exceptionLogBlocker =
               new ExceptionLogBlocker
                      (
                         "org.eclipse.osee.framework.jdk.core.type.OseeArgumentException",
                         null,
                         "javax.ws.rs.InternalServerErrorException",
                         null //"Attribute types \\[\\[[0-9]+\\]\\] is not taggable"
                      )
         )
      {
         try {
            ArtifactQuery.getArtifactListFromTypeAndAttribute
               (
                  CoreArtifactTypes.User,
                  CoreAttributeTypes.FavoriteBranch,
                  "DemoBranches.CIS_Bld_1",
                  DemoBranches.CIS_Bld_1
               );
            exceptionLogBlocker.assertNoException();
         } catch (Exception e) {
            exceptionLogBlocker.assertExpectedExceptionNoServerExceptionClassNameChecks(e);
         }
      }
      //@formatter:on

      // test against a couple attributes types that are taggable; do not expect exception
      criteria.clear();
      criteria.add(
         new AttributeCriteria(CoreAttributeTypes.Email, "john.doe@somewhere.com", QueryOption.TOKEN_DELIMITER__ANY));
      try {
         ArtifactQuery.getArtifactListFromCriteria(DemoBranches.CIS_Bld_1, 1000, criteria);
         Assert.assertTrue("This attribute type is taggable", Boolean.TRUE);
      } catch (Exception ex) {
         Assert.fail(ex.getMessage());
      }

      try {
         ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.User, CoreAttributeTypes.Notes, "My Notes",
            DemoBranches.CIS_Bld_1);
         Assert.assertTrue("This attribute type is taggable", Boolean.TRUE);
      } catch (Exception ex) {
         Assert.fail(ex.getMessage());
      }
   }

   private TransactionToken createArtifactFortestQueryById(List<ArtifactId> newIdsInOrder, BranchToken branch) {
      Artifact created = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch);
      created.persist(testInfo.getTestName());
      ArtifactCache.deCache(created);
      newIdsInOrder.add(created);
      return TransactionManager.getHeadTransaction(branch);
   }

   @Test
   public void testQueryById() {
      BranchToken branch = BranchManager.createTopLevelBranch(testInfo.getTestName() + " branch");
      ServiceUtil.getOseeClient().getAccessControlService().setPermission(UserManager.getUser(DemoUsers.Joe_Smith),
         branch, PermissionEnum.FULLACCESS);

      List<ArtifactId> newIdsInOrder = new LinkedList<>();
      createArtifactFortestQueryById(newIdsInOrder, branch);
      TransactionToken beforeDelete = createArtifactFortestQueryById(newIdsInOrder, branch);
      Assert.assertEquals(2, newIdsInOrder.size());

      //create a new tx deleting the first created
      Artifact firstCreated = ArtifactQuery.getArtifactFromId(newIdsInOrder.get(0), branch);
      firstCreated.deleteAndPersist(getClass().getSimpleName());
      ArtifactCache.deCache(firstCreated);

      Artifact toCheck = ArtifactQuery.checkArtifactFromId(newIdsInOrder.get(0), branch, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertNull(toCheck);
      toCheck = ArtifactQuery.checkArtifactFromId(newIdsInOrder.get(0), branch, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNotNull(toCheck);
      ArtifactCache.deCache(toCheck);

      Assert.assertNotNull(
         ArtifactQuery.getHistoricalArtifactOrNull(firstCreated, beforeDelete, DeletionFlag.EXCLUDE_DELETED));
   }

   @Test
   public void testMultipleMatchLocations() {
      SearchOptions options = new SearchOptions();
      options.setIsSearchAll(true);
      options.setCaseSensive(false);
      SearchRequest request = new SearchRequest(SAW_Bld_1, "robot", options);
      Iterable<ArtifactMatch> matches = ArtifactQuery.getArtifactMatchesFromAttributeKeywords(request);
      boolean found = false;
      for (ArtifactMatch match : matches) {
         if (match.getArtifact().getName().equals("Read-Write Minimum Rate")) {
            HashCollection<AttributeId, MatchLocation> matchData = match.getMatchData();
            for (AttributeId attr : matchData.keySet()) {
               if (match.getArtifact().getAttributeById(attr, false) != null) {
                  if (match.getArtifact().getAttributeById(attr, false).isOfType(
                     CoreAttributeTypes.WordTemplateContent)) {
                     found = true;
                     Assert.assertEquals(2, matchData.getValues(attr).size());
                     break;
                  }
               }
            }
         }
      }
      Assert.assertTrue(found);
   }

   @Test
   public void testMultipleValues() {
      QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      builder.and(CoreAttributeTypes.Name,
         Arrays.asList(CoreUserGroups.Everyone.getName(), CoreUserGroups.OseeAdmin.getName()));
      int count = builder.getCount();
      Assert.assertEquals(2, count);
   }

   @Test
   public void testMultipleValuesIgnoreCase() {
      QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      builder.and(CoreAttributeTypes.Name, Arrays.asList(CoreUserGroups.Everyone.getName().toLowerCase(),
         CoreUserGroups.OseeAdmin.getName().toLowerCase()), QueryOption.CASE__IGNORE);
      List<Artifact> arts = builder.getResults().getList();
      int count = arts.size();
      Assert.assertEquals(2, count);
   }

   @Test
   public void testMultipleTypes() {
      QueryBuilderArtifact builder = ArtifactQuery.createQueryBuilder(CoreBranches.COMMON);
      builder.and(Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.StaticId), "everyone",
         QueryOption.CASE__IGNORE);
      int count = builder.getCount();
      Assert.assertEquals(1, count);
   }

   @Test
   public void testIsArtifactChangedViaEntries() {
      Artifact folder =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoBranches.CIS_Bld_1, "ArtifactQueryTest");
      folder.persist(getClass().getName());
      Assert.assertFalse(ArtifactQuery.isArtifactChangedViaEntries(folder));

      TransactionToken transaction = folder.getTransaction();

      folder.addAttribute(CoreAttributeTypes.StaticId, "testing");
      folder.persist(getClass().getName());
      Assert.assertFalse(ArtifactQuery.isArtifactChangedViaEntries(folder));

      // load historical artifact; this should return as changed cause of new attribute
      Artifact historicalArtifactFromId =
         ArtifactQuery.getHistoricalArtifactFromId(folder, transaction, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertTrue(ArtifactQuery.isArtifactChangedViaEntries(historicalArtifactFromId));

      transaction = folder.getTransaction();

      Artifact rootArt =
         ArtifactQuery.getArtifactFromId(CoreArtifactTokens.DefaultHierarchyRoot, DemoBranches.CIS_Bld_1);
      rootArt.addChild(folder);
      rootArt.persist(getClass().getName());
      Assert.assertFalse(ArtifactQuery.isArtifactChangedViaEntries(folder));

   }
}
