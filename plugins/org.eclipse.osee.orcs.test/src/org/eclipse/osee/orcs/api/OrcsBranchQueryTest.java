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

import static org.eclipse.osee.framework.core.enums.BranchState.CREATED;
import static org.eclipse.osee.framework.core.enums.BranchState.MODIFIED;
import static org.eclipse.osee.framework.core.enums.BranchType.BASELINE;
import static org.eclipse.osee.framework.core.enums.BranchType.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.eclipse.osee.orcs.api.TestBranches.CIS_Bld_1;
import static org.eclipse.osee.orcs.api.TestBranches.SAW_Bld_1;
import static org.eclipse.osee.orcs.api.TestBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.Iterator;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * @author Roberto E. Escobar
 */
public class OrcsBranchQueryTest {

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @Rule
   public TestName testName = new TestName();

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory factory;

   private static IOseeBranch SYS_ROOT = CoreBranches.SYSTEM_ROOT;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      factory = orcsApi.getQueryFactory(context);
   }

   @Test
   public void testGetAll() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(5, results.size());
      assertEquals(5, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();
      BranchReadable branch3 = iterator.next();
      BranchReadable branch4 = iterator.next();
      BranchReadable branch5 = iterator.next();

      // actual, IOseeBranch, localId, type, state, isArchived, parentId, baseTx, sourceTx, assocArtId
      assertBranch(branch1, SYS_ROOT, 1, SYSTEM_ROOT, MODIFIED, false, -1, 1, 1, -1);
      assertBranch(branch2, COMMON, 2, BASELINE, MODIFIED, false, 1, 4, 3, -1);
      assertBranch(branch3, SAW_Bld_1, 3, BASELINE, MODIFIED, false, 1, 15, 3, -1);
      assertBranch(branch4, CIS_Bld_1, 4, BASELINE, MODIFIED, false, 1, 17, 3, -1);
      assertBranch(branch5, SAW_Bld_2, 5, BASELINE, CREATED, false, 3, 23, 22, 9);
   }

   @Test
   public void testGetByType() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andIsOfType(SYSTEM_ROOT);

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

      assertEquals(COMMON, branch1);
      assertEquals(SAW_Bld_1, branch2);
      assertEquals(CIS_Bld_1, branch3);
      assertEquals(SAW_Bld_2, branch4);
   }

   @Test
   public void testGetByUuid() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andUuids(COMMON.getGuid(), SAW_Bld_2.getGuid());

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();

      assertEquals(COMMON, branch1);
      assertEquals(SAW_Bld_2, branch2);
   }

   @Test
   public void testGetByLocalId() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andLocalId(5, 3);

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
   public void testGetByNameEquals() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andNameEquals("CIS_Bld_1");

      assertEquals(CIS_Bld_1, query.getResults().getExactlyOne());
      assertEquals(1, query.getCount());
   }

   @Test
   public void testGetByNamePatternEquals() throws OseeCoreException {
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
   public void testGetByState() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andStateIs(CREATED);

      assertEquals(SAW_Bld_2, query.getResults().getExactlyOne());
      assertEquals(1, query.getCount());
   }

   @Test
   public void testGetByChildOf() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andIsChildOf(SAW_Bld_1);

      assertEquals(SAW_Bld_2, query.getResults().getExactlyOne());
      assertEquals(1, query.getCount());

      query = factory.branchQuery();
      query.andIsChildOf(SYS_ROOT);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(4, results.size());
      assertEquals(4, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();
      BranchReadable branch3 = iterator.next();
      BranchReadable branch4 = iterator.next();

      assertEquals(COMMON, branch1);
      assertEquals(SAW_Bld_1, branch2);
      assertEquals(CIS_Bld_1, branch3);
      assertEquals(SAW_Bld_2, branch4);
   }

   @Test
   public void testGetByAncestorOf() throws OseeCoreException {
      BranchQuery query = factory.branchQuery();
      query.andIsAncestorOf(SAW_Bld_2);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(2, results.size());
      assertEquals(2, query.getCount());

      Iterator<BranchReadable> iterator = results.iterator();
      BranchReadable branch1 = iterator.next();
      BranchReadable branch2 = iterator.next();

      assertEquals(SYS_ROOT, branch1);
      assertEquals(SAW_Bld_1, branch2);

      query = factory.branchQuery();
      query.andIsAncestorOf(COMMON);

      results = query.getResults();
      assertEquals(1, results.size());
      assertEquals(1, query.getCount());

      iterator = results.iterator();
      branch1 = iterator.next();

      assertEquals(SYS_ROOT, branch1);
   }

   @Test
   public void testGetIncludeArchived() throws Exception {
      IOseeBranch child = TokenFactory.createBranch(GUID.create(), testName.getMethodName());

      BranchQuery query = factory.branchQuery();
      query.andIds(CIS_Bld_1);
      BranchReadable cisBranch = query.getResults().getExactlyOne();

      IOseeBranch actual = createBranch(cisBranch, child);
      assertEquals(child, actual);

      getBranchOps().archiveUnarchiveBranch(actual, ArchiveOperation.ARCHIVE).call();

      query = factory.branchQuery();
      query.andIds(child);
      query.excludeArchived();
      assertNull(query.getResults().getOneOrNull());

      query.includeArchived();
      assertEquals(child, query.getResults().getExactlyOne());
   }

   @Test
   public void testGetIncludeDeleted() throws Exception {
      IOseeBranch child = TokenFactory.createBranch(GUID.create(), testName.getMethodName());

      BranchQuery query = factory.branchQuery();
      query.andIds(CIS_Bld_1);
      BranchReadable cisBranch = query.getResults().getExactlyOne();

      IOseeBranch actual = createBranch(cisBranch, child);
      assertEquals(child, actual);

      query = factory.branchQuery();
      query.andIds(child);
      assertEquals(child, query.getResults().getOneOrNull());

      getBranchOps().changeBranchState(actual, BranchState.DELETED).call();

      query = factory.branchQuery();
      query.andIds(child);
      query.excludeDeleted();
      assertNull(query.getResults().getOneOrNull());

      query.includeDeleted();
      assertEquals(child, query.getResults().getExactlyOne());
   }

   @Test
   public void testGetWithMultipleConditions1() throws Exception {
      IOseeBranch child = TokenFactory.createBranch(GUID.create(), testName.getMethodName());

      IOseeBranch actual = createBranch(SAW_Bld_2, child);

      BranchQuery query = factory.branchQuery();
      query.andIsOfType(BranchType.WORKING).andIsChildOf(SAW_Bld_1).andStateIs(BranchState.CREATED);

      ResultSet<BranchReadable> results = query.getResults();
      assertEquals(actual, results.getExactlyOne());
      assertEquals(1, query.getCount());
   }

   private IOseeBranch createBranch(IOseeBranch parent, IOseeBranch id) throws Exception {
      ArtifactReadable author = getSystemUser();
      return getBranchOps().createWorkingBranch(id, author, parent, null).call();
   }

   private ArtifactReadable getSystemUser() throws OseeCoreException {
      return factory.fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private OrcsBranch getBranchOps() {
      return orcsApi.getBranchOps(null);
   }

   private static void assertBranch(BranchReadable actual, IOseeBranch token, int localId, BranchType type, BranchState state, boolean isArchived, int parent, int baseTx, int sourceTx, int assocArtId) {
      assertEquals(token, actual);
      assertBranch(actual, token.getName(), token.getGuid(), localId, type, state, isArchived, parent, baseTx,
         sourceTx, assocArtId);
   }

   private static void assertBranch(BranchReadable actual, String name, String guid, int localId, BranchType type, BranchState state, boolean isArchived, int parent, int baseTx, int sourceTx, int assocArtId) {
      assertEquals(guid, actual.getGuid());
      assertEquals(name, actual.getName());
      assertEquals(localId, actual.getLocalId());

      assertEquals(type, actual.getBranchType());
      assertEquals(state, actual.getBranchState());
      assertEquals(isArchived, actual.getArchiveState().isArchived());
      assertEquals(parent, actual.getParentBranch());
      assertEquals(baseTx, actual.getBaseTransaction());
      assertEquals(sourceTx, actual.getSourceTransaction());
      assertEquals(assocArtId, actual.getAssociatedArtifactId());
   }
}
