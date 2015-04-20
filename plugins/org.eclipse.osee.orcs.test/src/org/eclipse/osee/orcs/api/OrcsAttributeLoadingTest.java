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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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
public class OrcsAttributeLoadingTest {

   @Rule
   public TestRule osgi = integrationRule(this);

   @OsgiService
   private OrcsApi orcsApi;

   private QueryFactory query;

   @Before
   public void setUp() throws Exception {
      query = orcsApi.getQueryFactory();
   }

   @Test
   public void testAttributeLoading() throws Exception {
      QueryBuilder builder = query.fromBranch(CoreBranches.COMMON).andUuids(Arrays.asList(6L, 7L, 8L));
      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      assertEquals(3, resultSet.size());
      assertEquals(3, builder.getCount());

      Map<Integer, ArtifactReadable> lookup = createLookup(resultSet);
      ArtifactReadable art6 = lookup.get(6);
      ArtifactReadable art7 = lookup.get(7);
      ArtifactReadable art8 = lookup.get(8);

      //Test loading name attributes
      assertEquals(art6.getSoleAttributeAsString(CoreAttributeTypes.Name),
         "org.eclipse.osee.coverage.OseeTypes_Coverage");
      assertEquals(art7.getSoleAttributeAsString(CoreAttributeTypes.Name), "User Groups");
      assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.Name), "Everyone");

      //Test boolean attributes
      assertEquals(art8.getSoleAttributeAsString(CoreAttributeTypes.DefaultGroup), "true");
   }

   @Test
   public void testLoadWordTemplateContentAttributes() throws OseeCoreException {
      QueryBuilder builder =
         query.fromBranch(TestBranches.SAW_Bld_1).and(CoreAttributeTypes.Name, "Haptic Constraints");

      ResultSet<ArtifactReadable> resultSet = builder.getResults();

      ArtifactReadable artifact = resultSet.iterator().next();
      assertTrue(artifact.getSoleAttributeAsString(CoreAttributeTypes.WordTemplateContent).length() > 2);

      assertFalse(resultSet.isEmpty());
      assertEquals(resultSet.size(), builder.getCount());
   }

   private Map<Integer, ArtifactReadable> createLookup(Iterable<ArtifactReadable> arts) {
      Map<Integer, ArtifactReadable> lookup = new HashMap<Integer, ArtifactReadable>();
      for (ArtifactReadable artifact : arts) {
         lookup.put(artifact.getLocalId(), artifact);
      }
      return lookup;
   }
}
