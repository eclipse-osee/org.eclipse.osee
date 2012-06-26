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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.OrcsIntegrationRule;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.GraphReadable;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Rule;

/**
 * Test Case for {@link OrcsApi}
 * 
 * @author Andrew M. Finkbeiner
 */
public class OrcsRelationLoadingTest {

   @Rule
   public OrcsIntegrationRule osgi = new OrcsIntegrationRule(this);

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @OsgiService
   OrcsApi orcsApi;

   @org.junit.Test
   public void testSearchById() throws Exception {
      ApplicationContext context = null; // TODO use real application context

      QueryFactory queryFactory = orcsApi.getQueryFactory(context);
      GraphReadable graph = orcsApi.getGraph(context);
      checkRelationsForCommonBranch(orcsApi, queryFactory, graph, context);
      checkRelationsForSawBranch(orcsApi, queryFactory, graph, context);

   }

   private void checkRelationsForCommonBranch(OrcsApi oseeApi, QueryFactory queryFactory, GraphReadable graph, ApplicationContext context) throws OseeCoreException {
      QueryBuilder builder = queryFactory.fromBranch(CoreBranches.COMMON).andLocalIds(Arrays.asList(7, 8, 9));
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = creatLookup(moreArts);
      ArtifactReadable art7 = lookup.get(7);
      ArtifactReadable art8 = lookup.get(8);
      ArtifactReadable art9 = lookup.get(9);

      //art 7 has no relations
      Assert.assertEquals(0, graph.getExistingRelationTypes(art7).size());
      //art 8 has 4 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      2  397   1  8     36 1  1  2  6  36
      //      3  397   8  16    37 1  1  2  6  37
      //      1  397   8  9     41 1  1  2  6  41
      //      173   397   8  121      699   1  1  2  16
      Assert.assertEquals(2, graph.getExistingRelationTypes(art8).size());
      Assert.assertEquals(3,
         graph.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child, art8).getList().size());
      Assert.assertEquals(1,
         graph.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent, art8).getList().size());

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
      Assert.assertEquals(2, graph.getExistingRelationTypes(art9).size());
      Assert.assertEquals(1,
         graph.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent, art9).getList().size());
      Assert.assertEquals(20, graph.getRelatedArtifacts(CoreRelationTypes.Users_User, art9).getList().size());

   }

   private void checkRelationsForSawBranch(OrcsApi oseeApi, QueryFactory queryFactory, GraphReadable graph, ApplicationContext context) throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(oseeApi.getBranchCache().getByName("SAW_Bld_1").iterator().next()).and(
            CoreAttributeTypes.Name, Operator.EQUAL, "Design Constraints");
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      Assert.assertFalse(moreArts.isEmpty());
      ArtifactReadable artifact = moreArts.iterator().next();

      //art 7 has no relations

      //artifact has 3 children and 1 parent

      Assert.assertEquals(2, graph.getExistingRelationTypes(artifact).size());
      Assert.assertEquals(3,
         graph.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child, artifact).getList().size());
      Assert.assertEquals(1,
         graph.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Parent, artifact).getList().size());
   }

   Map<Integer, ArtifactReadable> creatLookup(List<ArtifactReadable> arts) {
      Map<Integer, ArtifactReadable> lookup = new HashMap<Integer, ArtifactReadable>();
      for (ArtifactReadable artifact : arts) {
         lookup.put(artifact.getLocalId(), artifact);
      }
      return lookup;
   }
}
