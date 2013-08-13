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

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Jeff C. Phillips
 */
public class OrcsAttributeSearchTest {

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory queryFactory;

   @Before
   public void setup() {
      ApplicationContext context = null; // TODO use real application context
      queryFactory = orcsApi.getQueryFactory(context);
   }

   @Test
   public void testNameAttributeNotEqualSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.Name, Operator.NOT_EQUAL, "User Groups");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      for (ArtifactReadable artifact : moreArts) {
         assertTrue(artifact.getLocalId() != 7);
      }
   }

   @Test
   public void testNameAttributeEqualSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.Name, Operator.EQUAL, "User Groups");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      assertEquals(1, moreArts.size());
      assertEquals(1, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = createLookup(moreArts);
      ArtifactReadable art7 = lookup.get(7);

      //Test loading name attributes
      assertEquals(art7.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
   }

   @Test
   public void testWTCAttributeEqualSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(TestBranches.SAW_Bld_1).and(CoreAttributeTypes.WordTemplateContent, "commands",
            TokenDelimiterMatch.ANY, TokenOrderType.MATCH_ORDER, CaseType.IGNORE_CASE,
            MatchTokenCountType.IGNORE_TOKEN_COUNT);

      assertEquals(3, builder.getCount());

      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      assertFalse(moreArts.isEmpty());
      assertEquals(3, moreArts.size());
   }

   @Test
   public void testBooleanAttributeSearch() throws OseeCoreException {
      QueryBuilder builder =
         queryFactory.fromBranch(CoreBranches.COMMON).and(CoreAttributeTypes.DefaultGroup, Operator.EQUAL, "true");
      ResultSet<ArtifactReadable> resultSet = builder.getResults();
      List<ArtifactReadable> moreArts = resultSet.getList();

      assertEquals(1, moreArts.size());
      assertEquals(1, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = createLookup(moreArts);
      ArtifactReadable art8 = lookup.get(8);
      assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");
   }

   private Map<Integer, ArtifactReadable> createLookup(List<ArtifactReadable> arts) {
      Map<Integer, ArtifactReadable> lookup = new HashMap<Integer, ArtifactReadable>();
      for (ArtifactReadable artifact : arts) {
         lookup.put(artifact.getLocalId(), artifact);
      }
      return lookup;
   }
}
