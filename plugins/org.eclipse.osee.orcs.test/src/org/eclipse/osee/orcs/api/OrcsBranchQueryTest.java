/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.enums.BranchState.MODIFIED;
import static org.eclipse.osee.framework.core.enums.BranchType.BASELINE;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreBranches.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.mock.OrcsIntegrationByClassRule;
import org.eclipse.osee.orcs.db.mock.OseeClassDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;

/**
 * @author Roberto E. Escobar
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrcsBranchQueryTest {

   @Rule
   public TestRule db = OrcsIntegrationByClassRule.integrationRule(this);

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory factory;

   @Before
   public void setup() {
      factory = orcsApi.getQueryFactory();
   }

   @AfterClass
   public static void cleanup() throws Exception {
      OseeClassDatabase.cleanup();
   }

   @Test
   public void testGetAll() {
      BranchQuery query = factory.branchQuery();

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(8, results.size());
      assertEquals(8, query.getCount());

      List<BranchReadable> list = results.getList();

      // list, IOseeBranch, BranchType, BranchState, isArchived, parentId, baseTx, sourceTx, assocArtId
      assertBranch(list, SYSTEM_ROOT, BranchType.SYSTEM_ROOT, MODIFIED, false, BranchId.SENTINEL, ArtifactId.SENTINEL);
      assertBranch(list, SAW_Bld_1, BASELINE, MODIFIED, false, SYSTEM_ROOT, OseeSystem);
      assertBranch(list, CIS_Bld_1, BASELINE, MODIFIED, false, SYSTEM_ROOT, OseeSystem);
      assertBranch(list, SAW_Bld_2, BASELINE, MODIFIED, false, SAW_Bld_1, OseeSystem);
      assertBranch(list, COMMON, BASELINE, MODIFIED, false, SYSTEM_ROOT, OseeSystem);
   }

   @Test
   public void testGetByType() {
      BranchQuery query = factory.branchQuery();
      query.andIsOfType(BranchType.SYSTEM_ROOT);

      assertEquals(1, query.getCount());
      assertEquals(CoreBranches.SYSTEM_ROOT, query.getResults().getExactlyOne());

      query = factory.branchQuery();
      query.andIsOfType(BASELINE);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();
      BranchReadable branch3 = iterator.next();
      BranchReadable branch4 = iterator.next();

      assertEquals(SAW_Bld_1, branch1);
      assertEquals(CIS_Bld_1, branch2);
      assertEquals(SAW_Bld_2, branch3);
      assertEquals(COMMON, branch4);
   }

   @Test
   public void testGetById() {
      BranchQuery query = factory.branchQuery();
      query.andIds(Arrays.asList(SAW_Bld_2, SAW_Bld_1));

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();

      assertEquals(SAW_Bld_1, branch1);
      assertEquals(SAW_Bld_2, branch2);
   }

   @Test
   public void testGetByNameEquals() {
      BranchQuery query = factory.branchQuery();
      query.andNameEquals("CIS_Bld_1");

      assertEquals(CIS_Bld_1, query.getResults().getExactlyOne());
      assertEquals(1, query.getCount());
   }

   @Test
   public void testGetByNamePatternEquals() {
      BranchQuery query = factory.branchQuery();
      query.andNamePattern("SAW.*?_Bld.*");

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();

      assertEquals(SAW_Bld_1, branch1);
      assertEquals(SAW_Bld_2, branch2);
   }

   @Test
   public void testGetByState() {
      BranchQuery query = factory.branchQuery();
      query.andStateIs(MODIFIED);

      assertEquals(7, query.getCount());
   }

   @Test
   public void testGetByChildOf() {
      BranchQuery query = factory.branchQuery();
      query.andIsChildOf(SAW_Bld_1);

      ResultSet<BranchReadable> children = query.getResults();
      assertEquals(4, query.getCount());
      assertEquals(SAW_Bld_2, children.iterator().next());

      query = factory.branchQuery();
      query.andIsChildOf(SYSTEM_ROOT);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(7, results.size());
      assertEquals(7, query.getCount());

      List<BranchReadable> list = results.getList();

      for (IOseeBranch branch : Arrays.asList(SAW_Bld_1, CIS_Bld_1, SAW_Bld_2, COMMON)) {
         assertTrue("Missing expected branch " + branch, list.contains(branch));
      }
   }

   @Test
   public void testGetByAncestorOf() {
      BranchQuery query = factory.branchQuery();
      query.andIsAncestorOf(SAW_Bld_2);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();

      assertEquals(SYSTEM_ROOT, branch1);
      assertEquals(SAW_Bld_1, branch2);

      query = factory.branchQuery();
      query.andIsAncestorOf(COMMON);

      results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      iterator = results.iterator();
      branch1 = iterator.next();

      assertEquals(SYSTEM_ROOT, branch1);
   }

   @Test
   public void zTestGetIncludeArchived() throws Exception {
      IOseeBranch child = IOseeBranch.create(testName.getMethodName());

      BranchQuery query = factory.branchQuery();
      query.andId(CIS_Bld_1);
      BranchReadable cisBranch = query.getResults().getExactlyOne();

      IOseeBranch actual = createBranch(cisBranch, child);
      assertEquals(child, actual);

      getBranchOps().archiveUnarchiveBranch(actual, ArchiveOperation.ARCHIVE).call();

      query = factory.branchQuery();
      query.andId(child);
      query.excludeArchived();
      assertNull(query.getResults().getOneOrNull());

      query.includeArchived();
      assertEquals(child, query.getResults().getExactlyOne());
   }

   // Do remaining tests last cause they create branches which changes query counts

   @Test
   public void zTestGetIncludeDeleted() throws Exception {
      IOseeBranch child = IOseeBranch.create(testName.getMethodName());

      BranchQuery query = factory.branchQuery();
      query.andId(CIS_Bld_1);
      BranchReadable cisBranch = query.getResults().getExactlyOne();

      IOseeBranch actual = createBranch(cisBranch, child);
      assertEquals(child, actual);

      query = factory.branchQuery();
      query.andId(child);
      assertEquals(child, query.getResults().getOneOrNull());

      getBranchOps().changeBranchState(actual, BranchState.DELETED).call();

      query = factory.branchQuery();
      query.andId(child);
      query.excludeDeleted();
      assertNull(query.getResults().getOneOrNull());

      query.includeDeleted();
      assertEquals(child, query.getResults().getExactlyOne());

   }

   @Test
   public void zTestGetWithMultipleConditions1() throws Exception {
      IOseeBranch child = IOseeBranch.create(testName.getMethodName());

      BranchId actual = createBranch(SAW_Bld_2, child);

      BranchQuery query = factory.branchQuery();
      query.andIsOfType(BranchType.WORKING).andIsChildOf(SAW_Bld_1).andStateIs(BranchState.CREATED);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(actual, results.getExactlyOne());
      assertEquals(1, query.getCount());

   }

   private IOseeBranch createBranch(IOseeBranch parent, IOseeBranch uuid) throws Exception {
      return getBranchOps().createWorkingBranch(uuid, OseeSystem, parent, ArtifactId.SENTINEL).call();
   }

   private OrcsBranch getBranchOps() {
      return orcsApi.getBranchOps();
   }

   private static void assertBranch(List<BranchReadable> list, IOseeBranch token, BranchType type, BranchState state, boolean isArchived, BranchId parent, ArtifactId associatedArtifact) {
      int index = list.indexOf(token);
      Assert.assertNotEquals(index, -1);
      assertBranch(list.get(index), token.getName(), token, type, state, isArchived, parent, associatedArtifact);
   }

   private static void assertBranch(BranchReadable actual, String name, BranchId id, BranchType type, BranchState state, boolean isArchived, BranchId parent, ArtifactId associatedArtifact) {
      assertEquals(name, actual.getName());
      assertEquals(id, actual);
      assertEquals(type, actual.getBranchType());
      assertEquals(state, actual.getBranchState());
      assertEquals(isArchived, actual.getArchiveState().isArchived());
      assertEquals(parent, actual.getParentBranch());
      assertEquals(associatedArtifact, actual.getAssociatedArtifact());
   }
}