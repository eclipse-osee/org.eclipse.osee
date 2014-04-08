/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.integration.util.DispositionInitializer;
import org.eclipse.osee.disposition.rest.integration.util.DispositionIntegrationRule;
import org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Angel Avila
 */
public class DispositionIntegrationTest {

   @Rule
   public TestRule rule = DispositionIntegrationRule.integrationRule(this, "osee.demo.hsql");

   @OsgiService
   public OrcsApi orcsApi;

   @OsgiService
   public DispoApi dispoApi;

   @Before
   public void setUp() throws Exception {
      DispositionInitializer initializer = new DispositionInitializer(orcsApi, dispoApi);
      initializer.initialize();
   }

   @Test
   public void testDispositionApi() {
      // We have one item with discrepancies: 1-10, 12-20, 23, 25, 32-90

      DispoProgram program = dispoApi.getDispoFactory().createProgram(DispositionTestUtil.SAW_Bld_1_FOR_DISPO);

      List<DispoSet> dispoSets = dispoApi.getDispoSets(program);
      DispoSet devSet = dispoSets.get(0);
      String devSetId = devSet.getGuid();

      List<DispoItem> dispoItems = dispoApi.getDispoItems(program, devSetId);
      DispoItem itemOne = dispoItems.get(0);
      String itemOneId = itemOne.getGuid();

      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(0, itemOne.getAnnotationsList().length());
      assertEquals(DispoStrings.Item_InComplete, itemOne.getStatus());

      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10");
      annotationOne.setResolution("VALID");
      String createdOneId = dispoApi.createDispoAnnotation(program, itemOneId, annotationOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-20");
      annotationTwo.setResolution("VALID");
      String createdTwoId = dispoApi.createDispoAnnotation(program, itemOneId, annotationTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("23,25,32-90");
      annotationThree.setResolution("VALID");
      String createdThreeId = dispoApi.createDispoAnnotation(program, itemOneId, annotationThree);

      itemOne = dispoApi.getDispoItemById(program, itemOneId);

      assertEquals(DispoStrings.Item_Complete, itemOne.getStatus());
      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(3, itemOne.getAnnotationsList().length());
      assertTrue(itemOne.getLastUpdate().after(itemOne.getCreationDate()));

      dispoApi.deleteDispoAnnotation(program, itemOneId, createdTwoId);

      itemOne = dispoApi.getDispoItemById(program, itemOneId);

      assertEquals(DispoStrings.Item_Complete, itemOne.getStatus());
      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(2, itemOne.getAnnotationsList().length());

      DispoAnnotationData actualAnnotation = dispoApi.getDispoAnnotationById(program, itemOneId, createdThreeId);
      assertEquals("23,25,32-90", actualAnnotation.getLocationRefs());

      actualAnnotation = dispoApi.getDispoAnnotationById(program, itemOneId, createdOneId);
      assertEquals("1-10", actualAnnotation.getLocationRefs());
   }

}
