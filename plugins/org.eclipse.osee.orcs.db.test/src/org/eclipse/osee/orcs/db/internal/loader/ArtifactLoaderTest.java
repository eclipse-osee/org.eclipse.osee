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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.data.ArtifactDataImpl;
import org.eclipse.osee.orcs.db.internal.loader.data.OrcsObjectFactoryImpl;
import org.eclipse.osee.orcs.db.internal.loader.data.VersionDataImpl;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiRule;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockSystemPreferences;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;

/**
 * Test Case for {@link ArtifactLoader}
 * 
 * @author Andrew M. Finkbeiner
 */
public class ArtifactLoaderTest {

   @Rule
   public OsgiRule osgi = new OsgiRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private static StaticSqlProvider sqlProvider;
   private static Log log;

   @OsgiService
   IOseeDatabaseService oseeDbService;

   @OsgiService
   IdentityService identityService;

   @BeforeClass
   public static void setUp() {
      log = new MockLog();
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(new MockLog());
      sqlProvider.setPreferences(new MockSystemPreferences());
   }

   @AfterClass
   public static void tearDown() {
      sqlProvider = null;
   }

   @org.junit.Test
   public void testArtifactLoadingData() throws OseeCoreException {
      OrcsObjectFactoryImpl factory = new OrcsObjectFactoryImpl(null, identityService);
      ArtifactLoader loader = new ArtifactLoader(log, sqlProvider, oseeDbService, factory);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      try {
         artJoinQuery.add(6, 2, -1);
         artJoinQuery.add(7, 2, -1);
         artJoinQuery.add(8, 2, -1);
         artJoinQuery.store(connection);
         int queryId = artJoinQuery.getQueryId();

         final List<ArtifactData> actuals = new ArrayList<ArtifactData>();
         loader.loadFromQueryId(new ArtifactDataHandler() {
            @Override
            public void onData(ArtifactData data) {
               actuals.add(data);
            }
         }, new LoadOptions(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 10, queryId);

         final List<ArtifactData> expected = new ArrayList<ArtifactData>();
         expected.add(getArtifactRow(6, identityService.getUniversalId(346), 2, 7, "AEmKsTkcwh02JspUtYQA", false,
            "NYN46", ModificationType.getMod(1), -1, -1));
         expected.add(getArtifactRow(7, identityService.getUniversalId(346), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false,
            "7NPJR", ModificationType.getMod(1), -1, -1));
         expected.add(getArtifactRow(8, identityService.getUniversalId(295), 2, 33, "AEmK_YNYKmA66ynLWVgA", false,
            "QHXXC", ModificationType.getMod(1), -1, -1));

         ArtifactData notExpected =
            getArtifactRow(7, identityService.getUniversalId(295), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false, "7NPJR",
               ModificationType.getMod(1), -1, -1);

         artJoinQuery.delete(connection);

         Assert.assertEquals(expected.size(), actuals.size());
         for (ArtifactData row : expected) {
            Assert.assertTrue(String.format("Row [%s] could not be found.", row.toString()), actuals.contains(row));
         }
         Assert.assertTrue(String.format("Row [%s] should not be found.", notExpected.toString()),
            !actuals.contains(notExpected));
      } finally {
         connection.close();
      }
   }

   @org.junit.Test
   public void testDeletedArtifactLoadingData() throws OseeCoreException {
      OrcsObjectFactoryImpl factory = new OrcsObjectFactoryImpl(null, identityService);
      ArtifactLoader loader = new ArtifactLoader(log, sqlProvider, oseeDbService, factory);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      try {
         artJoinQuery.add(169, 7, -1);
         artJoinQuery.add(170, 7, -1);
         artJoinQuery.add(171, 7, -1);
         artJoinQuery.store(connection);
         int queryId = artJoinQuery.getQueryId();

         final List<ArtifactData> actuals = new ArrayList<ArtifactData>();
         loader.loadFromQueryId(new ArtifactDataHandler() {
            @Override
            public void onData(ArtifactData data) {
               actuals.add(data);
            }
         }, new LoadOptions(false, DeletionFlag.INCLUDE_DELETED, LoadLevel.ALL_CURRENT), 10, queryId);

         final List<ArtifactData> expected = new ArrayList<ArtifactData>();
         expected.add(getArtifactRow(169, identityService.getUniversalId(309), 7, 1101, "AEmPlVKm+mTccO2TMlAA", false,
            "DD0WL", ModificationType.getMod(3), -1, -1));
         expected.add(getArtifactRow(170, identityService.getUniversalId(309), 7, 1103, "AEmPmMM0Ahco9TfgjTwA", false,
            "9P19Q", ModificationType.getMod(3), -1, -1));
         expected.add(getArtifactRow(171, identityService.getUniversalId(309), 7, 1097, "AEmPvkGYwn0XgAIig0QA", false,
            "LF8CM", ModificationType.getMod(3), -1, -1));

         ArtifactData notExpected =
            getArtifactRow(7, identityService.getUniversalId(295), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false, "7NPJR",
               ModificationType.getMod(1), -1, -1);

         artJoinQuery.delete(connection);

         Assert.assertEquals(expected.size(), actuals.size());
         for (ArtifactData row : expected) {
            Assert.assertTrue(String.format("Row [%s] could not be found.", row.toString()), actuals.contains(row));
         }
         Assert.assertTrue(String.format("Row [%s] should not be found.", notExpected.toString()),
            !actuals.contains(notExpected));
      } finally {
         connection.close();
      }
   }

   private ArtifactData getArtifactRow(int artId, long artTypeUUID, int branchId, int gammaId, String guid, boolean historical, String humanReadableId, ModificationType modType, int stripeId, int transactionId) {
      VersionData version = new VersionDataImpl();
      version.setHistorical(historical);
      version.setBranchId(branchId);
      version.setGammaId(gammaId);
      version.setStripeId(stripeId);
      version.setTransactionId(transactionId);

      ArtifactData row = new ArtifactDataImpl(version);
      row.setLocalId(artId);
      row.setTypeUuid(artTypeUUID);
      row.setGuid(guid);
      row.setHumanReadableId(humanReadableId);
      row.setModType(modType);
      return row;
   }
}