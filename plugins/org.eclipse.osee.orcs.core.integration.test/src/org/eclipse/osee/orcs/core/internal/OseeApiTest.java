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

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;
import org.junit.Rule;

public class OseeApiTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testSearchById() throws Exception {
      OseeApi oseeApi = OsgiUtil.getService(OseeApi.class);

      ApplicationContext context = null; // TODO use real application context

      QueryFactory factory = oseeApi.getQueryFactory(context);
      QueryBuilder builder = factory.fromUuids(CoreBranches.COMMON, Arrays.asList(7, 8, 9));
      ResultSet<ReadableArtifact> resultSet = builder.build(LoadLevel.SHALLOW);
      List<ReadableArtifact> moreArts = resultSet.getList();

      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());

      //      Map<Integer, ReadableArtifact> lookup = creatLookup(arts);
      //art 7 has no relations
      //art 8 has 4 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      2  397   1  8     36 1  1  2  6  36
      //      3  397   8  16    37 1  1  2  6  37
      //      1  397   8  9     41 1  1  2  6  41
      //      173   397   8  121      699   1  1  2  16
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

      //      Iterable<ReadableArtifact> it =
      //         masterLoader.getIterable(options, 1, artJoinQuery.getQueryId(), new MockSessionContext());
      //
      //      int count = 0;
      //      for (ReadableArtifact readableArtifact : it) {
      //         count++;
      //      }
      //      Assert.assertEquals(count, 3);
      //      artJoinQuery.delete();

   }
   //   Map<Integer, ReadableArtifact> creatLookup(List<ReadableArtifact> arts) {
   //      Map<Integer, ReadableArtifact> lookup = new HashMap<Integer, ReadableArtifact>();
   //      for (ReadableArtifact artifact : arts) {
   //         lookup.put(artifact.getId(), artifact);
   //      }
   //      return lookup;
   //   }

   //   private void setupAttributeTypeCache(AttributeTypeCache cache) throws OseeCoreException {
   //      AttributeTypeFactory typeFactory = new AttributeTypeFactory();
   //      addType(cache, typeFactory, CoreAttributeTypes.Name, "StringAttribute", "DefaultAttributeDataProvider");
   //      addType(cache, typeFactory, CoreAttributeTypes.UriGeneralStringData, "StringAttribute",
   //         "DefaultAttributeDataProvider");
   //      addType(cache, typeFactory, CoreAttributeTypes.Active, "StringAttribute", "DefaultAttributeDataProvider");
   //      addType(cache, typeFactory, CoreAttributeTypes.DefaultGroup, "StringAttribute", "DefaultAttributeDataProvider");
   //
   //   }
   //
   //   private void addType(AttributeTypeCache cache, AttributeTypeFactory typeFactory, IAttributeType type, String attributeType, String providerType) throws OseeCoreException {
   //      typeFactory.createOrUpdate(cache, type.getGuid(), type.getName(), attributeType, providerType, "name", "name",
   //         new OseeEnumType(type.getGuid(), type.getName()), 1, 1, "", "");
   //
   //   }

}
