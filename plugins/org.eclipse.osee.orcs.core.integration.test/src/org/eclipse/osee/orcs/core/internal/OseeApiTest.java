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
package org.eclipse.osee.orcs.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.event.EventService;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.junit.Rule;
import org.osgi.service.event.EventAdmin;

public class OseeApiTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testSearchById() throws Exception {
      OsgiUtil.getService(Log.class);
      OsgiUtil.getService(SystemPreferences.class);
      OsgiUtil.getService(IdentityService.class);
      OsgiUtil.getService(IOseeDatabaseService.class);
      OsgiUtil.getService(IOseeModelFactoryService.class);
      OsgiUtil.getService(IOseeModelingService.class);
      OsgiUtil.getService(EventAdmin.class);
      OsgiUtil.getService(EventService.class);
      OsgiUtil.getService(IOseeCachingService.class);
      OsgiUtil.getService(QueryEngine.class);
      OsgiUtil.getService(DataStoreTypeCache.class);
      OsgiUtil.getService(DataLoader.class);
      OsgiUtil.getService(AttributeClassResolver.class);

      OseeApi oseeApi = OsgiUtil.getService(OseeApi.class);

      ApplicationContext context = null; // TODO use real application context

      PrettyQuery prettyQuery = new PrettyQuery(oseeApi);

      QueryFactory queryFactory = oseeApi.getQueryFactory(context);
      QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON).andLocalIds(Arrays.asList(7, 8, 9));
      ResultSet<ReadableArtifact> resultSet = builder.build(LoadLevel.FULL);
      List<ReadableArtifact> moreArts = resultSet.getList();

      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());

      Map<Integer, ReadableArtifact> lookup = creatLookup(moreArts);
      ReadableArtifact art7 = lookup.get(7);
      ReadableArtifact art8 = lookup.get(8);
      ReadableArtifact art9 = lookup.get(9);

      //art 7 has no relations
      Assert.assertEquals(0, art7.getAvailableRelationTypes().size());
      //art 8 has 4 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      2  397   1  8     36 1  1  2  6  36
      //      3  397   8  16    37 1  1  2  6  37
      //      1  397   8  9     41 1  1  2  6  41
      //      173   397   8  121      699   1  1  2  16
      Assert.assertEquals(2, art8.getAvailableRelationTypes().size());
      Assert.assertEquals(3,
         prettyQuery.getRelatedArtifacts(context, CoreRelationTypes.Default_Hierarchical__Child, art8).size());
      Assert.assertEquals(1,
         prettyQuery.getRelatedArtifacts(context, CoreRelationTypes.Default_Hierarchical__Parent, art8).size());

      //art9 has 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      1  397   8  9     41 1  1  2  6  41
      //      21 382   9  34    326   1  1  2  14 326
      //      20 382   9  33    327   1  1  2  14 327
      //      23 382   9  36    328   1  1  2  14 328
      //      22 382   9  35    329   1  1  2  14 329
      //      28 382   9  41    334   1  1  2  14 334
      //      29 382   9  42    335   1  1  2  14 335
      //      30 382   9  43    336   1  1  2  14 336
      //      31 382   9  44    337   1  1  2  14 337
      //      24 382   9  37    338   1  1  2  14 338
      //      25 382   9  38    339   1  1  2  14 339
      //      26 382   9  39    340   1  1  2  14 340
      //      27 382   9  40    341   1  1  2  14 341
      //      36 382   9  49    342   1  1  2  14 342
      //      37 382   9  50    343   1  1  2  14 343
      //      38 382   9  51    344   1  1  2  14 344
      //      32 382   9  45    346   1  1  2  14 346
      //      33 382   9  46    347   1  1  2  14 347
      //      34 382   9  47    348   1  1  2  14 348
      //      35 382   9  48    349   1  1  2  14 349
      //      218   382   9  166      898   1  1  2  21 898
      Assert.assertEquals(2, art9.getAvailableRelationTypes().size());
      Assert.assertEquals(1,
         prettyQuery.getRelatedArtifacts(context, CoreRelationTypes.Default_Hierarchical__Parent, art9).size());
      Assert.assertEquals(20, prettyQuery.getRelatedArtifacts(context, CoreRelationTypes.Users_User, art9).size());
   }

   private static class PrettyQuery {
      private final OseeApi oseeApi;

      PrettyQuery(OseeApi oseeApi) {
         this.oseeApi = oseeApi;
      }

      List<ReadableArtifact> getArtifactsFromLocalIds(ApplicationContext context, Integer... ids) throws OseeCoreException {
         return getArtifactsFromLocalIds(context, Arrays.asList(ids));
      }

      List<ReadableArtifact> getArtifactsFromLocalIds(ApplicationContext context, Collection<Integer> ids) throws OseeCoreException {
         QueryFactory queryFactory = oseeApi.getQueryFactory(context);
         QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON).andLocalIds(ids);
         ResultSet<ReadableArtifact> resultSet = builder.build(LoadLevel.FULL);
         return resultSet.getList();
      }

      List<ReadableArtifact> getRelatedArtifacts(ApplicationContext context, IRelationTypeSide relationTypeSide, ReadableArtifact art) throws OseeCoreException {
         List<Integer> results = new ArrayList<Integer>();
         art.getRelatedArtifacts(relationTypeSide, results);
         return getArtifactsFromLocalIds(context, results);
      }
   }

   List<ReadableArtifact> getRelated() {
      return null;
   }

   Map<Integer, ReadableArtifact> creatLookup(List<ReadableArtifact> arts) {
      Map<Integer, ReadableArtifact> lookup = new HashMap<Integer, ReadableArtifact>();
      for (ReadableArtifact artifact : arts) {
         lookup.put(artifact.getId(), artifact);
      }
      return lookup;
   }
}
