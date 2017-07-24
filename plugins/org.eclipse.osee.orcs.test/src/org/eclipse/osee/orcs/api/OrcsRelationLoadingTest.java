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
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Test Case for {@link OrcsApi}
 *
 * @author Andrew M. Finkbeiner
 */
public class OrcsRelationLoadingTest {

   @Rule
   public TestRule osgi = integrationRule(this);

   @OsgiService
   private OrcsApi orcsApi;

   @Test
   public void testSearchById() throws Exception {
      QueryFactory queryFactory = orcsApi.getQueryFactory();
      checkRelationsForCommonBranch(orcsApi, queryFactory);
      checkRelationsForSawBranch(orcsApi, queryFactory);
   }

   private void checkRelationsForCommonBranch(OrcsApi oseeApi, QueryFactory queryFactory) throws OseeCoreException {

      ArtifactReadable art6 =
         queryFactory.fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.OseeTypeDefinition).andNameEquals(
            "org.eclipse.osee.ats.client.demo.OseeTypes_Demo").getResults().getAtMostOneOrNull();
      assertNotNull(art6);
      ArtifactReadable art7 = queryFactory.fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.UserGroups).getResults().getAtMostOneOrNull();
      assertNotNull(art7);
      ArtifactReadable art8 = queryFactory.fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.Everyone).getResults().getAtMostOneOrNull();
      assertNotNull(art8);

      //art 6 has no relations
      assertEquals(0, art6.getExistingRelationTypes().size());
      //art 7 has 3
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID
      //      1  219   7  8     53
      //      3  219   7  15    54
      //      2  219   1  7     52
      assertEquals(1, art7.getExistingRelationTypes().size());
      assertEquals(5, art7.getRelated(CoreRelationTypes.Default_Hierarchical__Child).size());
      assertEquals(1, art7.getRelated(CoreRelationTypes.Default_Hierarchical__Parent).size());

      //art8 has
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID
      //      7  233   8  20    62
      //      8  233   8  21    63
      //      4  233   8  17    74
      //      6  233   8  19    76
      //      5  233   8  18    78
      //      1  219   7  8     53
      assertEquals(2, art8.getExistingRelationTypes().size());
      assertEquals(1, art8.getRelated(CoreRelationTypes.Default_Hierarchical__Parent).size());
      assertEquals(19, art8.getRelated(CoreRelationTypes.Users_User).size());

   }

   private void checkRelationsForSawBranch(OrcsApi oseeApi, QueryFactory queryFactory) throws OseeCoreException {
      QueryBuilder builder = queryFactory.fromBranch(SAW_Bld_1).and(CoreAttributeTypes.Name, "Design Constraints");
      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      assertEquals(1, resultSet.size());

      ArtifactReadable artifact = resultSet.getAtMostOneOrNull();
      assertNotNull(artifact);

      //art 7 has no relations

      //artifact has 3 children and 1 parent
      assertEquals(1, artifact.getExistingRelationTypes().size());
      assertEquals(3, artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Child).size());
      assertEquals(1, artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Parent).size());
   }
}