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
import junit.framework.Assert;
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
import org.eclipse.osee.orcs.core.ds.ArtifactRow;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.mock.H2Preferences;
import org.eclipse.osee.orcs.db.mock.MockLog;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;

public class ArtifactLoaderTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   private static StaticSqlProvider sqlProvider;
   private static Log log;

   @BeforeClass
   public static void setUp() {
      log = new MockLog();
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(new MockLog());
      sqlProvider.setPreferences(new H2Preferences());
   }

   @AfterClass
   public static void tearDown() {
      sqlProvider = null;
   }

   @org.junit.Test
   public void testArtifactLoadingData() throws OseeCoreException {
      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);

      ArtifactLoader loader = new ArtifactLoader(log, sqlProvider, oseeDbService, identityService);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      try {
         artJoinQuery.add(6, 2, -1);
         artJoinQuery.add(7, 2, -1);
         artJoinQuery.add(8, 2, -1);
         artJoinQuery.store(connection);
         int queryId = artJoinQuery.getQueryId();

         final List<ArtifactRow> actuals = new ArrayList<ArtifactRow>();
         loader.loadFromQueryId(new ArtifactRowHandler() {
            @Override
            public void onRow(ArtifactRow row) {
               actuals.add(row);
            }
         }, new LoadOptions(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 10, queryId);

         final List<ArtifactRow> expected = new ArrayList<ArtifactRow>();
         expected.add(getArtifactRow(6, identityService.getUniversalId(346), 2, 7, "AEmKsTkcwh02JspUtYQA", false,
            "NYN46", ModificationType.getMod(1), -1, -1));
         expected.add(getArtifactRow(7, identityService.getUniversalId(346), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false,
            "7NPJR", ModificationType.getMod(1), -1, -1));
         expected.add(getArtifactRow(8, identityService.getUniversalId(295), 2, 33, "AEmK_YNYKmA66ynLWVgA", false,
            "QHXXC", ModificationType.getMod(1), -1, -1));

         ArtifactRow notExpected =
            getArtifactRow(7, identityService.getUniversalId(295), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false, "7NPJR",
               ModificationType.getMod(1), -1, -1);

         artJoinQuery.delete(connection);

         Assert.assertEquals(expected.size(), actuals.size());
         for (ArtifactRow row : expected) {
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

      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);

      ArtifactLoader loader = new ArtifactLoader(log, sqlProvider, oseeDbService, identityService);

      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      try {
         artJoinQuery.add(169, 7, -1);
         artJoinQuery.add(170, 7, -1);
         artJoinQuery.add(171, 7, -1);
         artJoinQuery.store(connection);
         int queryId = artJoinQuery.getQueryId();

         final List<ArtifactRow> actuals = new ArrayList<ArtifactRow>();
         loader.loadFromQueryId(new ArtifactRowHandler() {
            @Override
            public void onRow(ArtifactRow row) {
               actuals.add(row);
            }
         }, new LoadOptions(false, DeletionFlag.INCLUDE_DELETED, LoadLevel.ALL_CURRENT), 10, queryId);

         final List<ArtifactRow> expected = new ArrayList<ArtifactRow>();
         expected.add(getArtifactRow(169, identityService.getUniversalId(309), 7, 1101, "AEmPlVKm+mTccO2TMlAA", false,
            "DD0WL", ModificationType.getMod(3), -1, -1));
         expected.add(getArtifactRow(170, identityService.getUniversalId(309), 7, 1103, "AEmPmMM0Ahco9TfgjTwA", false,
            "9P19Q", ModificationType.getMod(3), -1, -1));
         expected.add(getArtifactRow(171, identityService.getUniversalId(309), 7, 1097, "AEmPvkGYwn0XgAIig0QA", false,
            "LF8CM", ModificationType.getMod(3), -1, -1));

         ArtifactRow notExpected =
            getArtifactRow(7, identityService.getUniversalId(295), 2, 9, "AEmKsWXLBwVrvjcQvPwA", false, "7NPJR",
               ModificationType.getMod(1), -1, -1);

         artJoinQuery.delete(connection);

         Assert.assertEquals(expected.size(), actuals.size());
         for (ArtifactRow row : expected) {
            Assert.assertTrue(String.format("Row [%s] could not be found.", row.toString()), actuals.contains(row));
         }
         Assert.assertTrue(String.format("Row [%s] should not be found.", notExpected.toString()),
            !actuals.contains(notExpected));
      } finally {
         connection.close();
      }
   }

   private ArtifactRow getArtifactRow(int artId, long artTypeUUID, int branchId, int gammaId, String guid, boolean historical, String humanReadableId, ModificationType modType, int stripeId, int transactionId) {
      ArtifactRow row = new ArtifactRow();
      row.setArtifactId(artId);
      row.setArtTypeUuid(artTypeUUID);
      row.setBranchId(branchId);
      row.setGammaId(gammaId);
      row.setGuid(guid);
      row.setHistorical(historical);
      row.setHumanReadableId(humanReadableId);
      row.setModType(modType);
      row.setStripeId(stripeId);
      row.setTransactionId(transactionId);
      return row;
   }
}