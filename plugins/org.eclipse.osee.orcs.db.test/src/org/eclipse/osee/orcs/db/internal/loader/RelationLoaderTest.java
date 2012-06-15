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
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockSystemPreferences;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;

/**
 * Test Case for {@link RelationLoader}
 * 
 * @author Andrew M. Finkbeiner
 */
public class RelationLoaderTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private static StaticSqlProvider sqlProvider;

   @BeforeClass
   public static void setUp() {
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(new MockLog());
      sqlProvider.setPreferences(new MockSystemPreferences());
   }

   @AfterClass
   public static void tearDown() {
      sqlProvider = null;
   }

   @org.junit.Test
   public void testRelationLoadingData() throws OseeCoreException {

      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);
      RelationLoader relationLoader = new RelationLoader(new MockLog(), sqlProvider, oseeDbService, identityService);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      artJoinQuery.add(1, 2, -1);
      artJoinQuery.store(connection);
      int queryId = artJoinQuery.getQueryId();

      final List<RelationData> expected = new ArrayList<RelationData>();
      expected.add(getRelationRow(1, 8, 2, 36, 1, 1, "", 2, identityService.getUniversalId(397)));
      expected.add(getRelationRow(1, 17, 2, 60, 1, 1, "", 4, identityService.getUniversalId(397)));
      expected.add(getRelationRow(1, 22, 2, 94, 1, 1, "", 9, identityService.getUniversalId(397)));

      RelationData notExpected = getRelationRow(1, 22, 2, 94, 1, 1, "Idon'tExist", 9, 397);

      final List<RelationData> actuals = new ArrayList<RelationData>();

      relationLoader.loadFromQueryId(new RelationDataHandler() {
         @Override
         public void onData(RelationData data) {
            actuals.add(data);
         }
      }, new LoadOptions(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 100, queryId);

      artJoinQuery.delete(connection);
      connection.close();

      Assert.assertEquals(expected.size(), actuals.size());
      for (RelationData row : expected) {
         Assert.assertTrue(String.format("Row [%s] could not be found.", row.toString()), actuals.contains(row));
      }
      Assert.assertTrue(String.format("Row [%s] should not be found.", notExpected.toString()),
         !actuals.contains(notExpected));
   }

   @org.junit.Test
   public void testNoRelationsFound() throws OseeCoreException {

      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);
      RelationLoader relationLoader = new RelationLoader(new MockLog(), sqlProvider, oseeDbService, identityService);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      artJoinQuery.add(789, 2, -1);
      artJoinQuery.store(connection);
      int queryId = artJoinQuery.getQueryId();

      final List<RelationData> actuals = new ArrayList<RelationData>();

      relationLoader.loadFromQueryId(new RelationDataHandler() {
         @Override
         public void onData(RelationData data) {
            actuals.add(data);
         }
      }, new LoadOptions(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 100, queryId);

      artJoinQuery.delete(connection);
      connection.close();

      Assert.assertEquals(0, actuals.size());

   }

   @org.junit.Test
   public void testHistoricalLoad() throws OseeCoreException {
      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);
      RelationLoader relationLoader = new RelationLoader(new MockLog(), sqlProvider, oseeDbService, identityService);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      artJoinQuery.add(1, 2, -1);
      artJoinQuery.store(connection);
      int queryId = artJoinQuery.getQueryId();

      final List<RelationData> actuals = new ArrayList<RelationData>();
      relationLoader.loadFromQueryId(new RelationDataHandler() {
         @Override
         public void onData(RelationData data) {
            actuals.add(data);
         }
      }, new LoadOptions(true, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 100, queryId);

      artJoinQuery.delete(connection);
      connection.close();

      Assert.assertEquals(0, actuals.size());

   }

   private RelationData getRelationRow(int artIdA, int artIdB, int branchId, int gammaId, int modType, int parentId, String rationale, int relationId, long relationTypeId) throws OseeArgumentException {
      RelationData row = new RelationData();
      row.setArtIdA(artIdA);
      row.setArtIdB(artIdB);
      row.setBranchId(branchId);
      row.setGammaId(gammaId);
      row.setModType(ModificationType.getMod(modType));
      row.setParentId(parentId);
      row.setRationale(rationale);
      row.setRelationId(relationId);
      row.setRelationTypeId(relationTypeId);
      return row;
   }
}