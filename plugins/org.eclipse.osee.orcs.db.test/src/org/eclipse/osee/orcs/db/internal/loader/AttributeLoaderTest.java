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
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.loader.AttributeLoader.ProxyDataFactory;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.db.mocks.MockDataProxy;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockSystemPreferences;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;

/**
 * Test Case for {@link AttributeLoader}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeLoaderTest {

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
   public void testAttributeLoadingData() throws OseeCoreException {
      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);
      IdentityService identityService = OsgiUtil.getService(IdentityService.class);

      final List<AttributeRow> actuals = new ArrayList<AttributeRow>();

      ProxyDataFactory factory = new ProxyDataFactory() {

         @Override
         public DataProxy createProxy(long typeUuid, String value, String uri) {
            return new AttributeDataProxy(typeUuid, value, uri);
         }

      };
      AttributeLoader loader = new AttributeLoader(new MockLog(), sqlProvider, oseeDbService, identityService, factory);
      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      try {
         artJoinQuery.add(6, 2, -1);
         artJoinQuery.add(7, 2, -1);
         artJoinQuery.add(8, 2, -1);
         artJoinQuery.store(connection);
         int queryId = artJoinQuery.getQueryId();

         loader.loadFromQueryId(new AttributeRowHandler() {
            @Override
            public void onRow(AttributeRow row) {
               actuals.add(row);
            }
         }, new LoadOptions(false, DeletionFlag.EXCLUDE_DELETED, LoadLevel.ALL_CURRENT), 10, queryId);
         artJoinQuery.delete(connection);
      } finally {
         connection.close();
      }

      final List<AttributeRow> expected = new ArrayList<AttributeRow>();
      //@formatter:off
      expected.add(getAttributeRow(null, 6, 2, 12, 21, ModificationType.NEW, identityService.getUniversalId(130), false, -1, 5));
      expected.add(getAttributeRow(null, 6, 2, 13, 24, ModificationType.NEW, identityService.getUniversalId(114), false, -1, 5));
      expected.add(getAttributeRow(null, 6, 2, 14, 23, ModificationType.NEW, identityService.getUniversalId(123), false, -1, 5));
      expected.add(getAttributeRow(null, 7, 2, 15, 10, ModificationType.NEW, identityService.getUniversalId(130), false, -1, 5));
      expected.add(getAttributeRow(null, 7, 2, 16, 11, ModificationType.NEW, identityService.getUniversalId(114), false, -1, 5));
      expected.add(getAttributeRow(null, 7, 2, 17, 12, ModificationType.NEW, identityService.getUniversalId(123), false, -1, 5));
      expected.add(getAttributeRow(null, 8, 2, 20, 48, ModificationType.NEW, identityService.getUniversalId(130), false, -1, 5));
      //@formatter:on

      Assert.assertEquals(expected.size(), actuals.size());
      // TODO: finish this test

      //      for (AttributeRow row : expected) {
      //         Assert.assertTrue(String.format("Row [%s] could not be found.", row.toString()), actuals.contains(row));
      //      }
      //      Assert.assertTrue(String.format("Row [%s] should not be found.", notExpected.toString()),
      //         !actuals.contains(notExpected));
   }

   private AttributeRow getAttributeRow(DataProxy proxy, int artId, int branchId, int attrId, int gammaId, ModificationType modType, long attrType, boolean historical, int stripeId, int transactionId) {
      AttributeRow row = new AttributeRow();
      row.setArtifactId(artId);
      row.setBranchId(branchId);
      row.setAttrId(attrId);
      row.setGammaId(gammaId);

      row.setHistorical(historical);
      row.setModType(modType);
      row.setStripeId(stripeId);
      row.setTransactionId(transactionId);

      row.setAttrTypeUuid(attrType);
      row.setDataProxy(proxy);

      row.setStripeId(stripeId);
      return row;
   }

   @SuppressWarnings("unused")
   private final class AttributeDataProxy extends MockDataProxy {
      private final long typeUuid;
      private final String value;
      private final String uri;

      public AttributeDataProxy(long typeUuid, String value, String uri) {
         super();
         this.typeUuid = typeUuid;
         this.value = value;
         this.uri = uri;
      }

      public long getTypeUuid() {
         return typeUuid;
      }

      public String getValue() {
         return value;
      }

      public String getUri() {
         return uri;
      }

   }
}
