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

import static org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil.SAW_Bld_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.integration.util.DispositionInitializer;
import org.eclipse.osee.disposition.rest.integration.util.DispositionIntegrationRule;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author Angel Avila
 */
public class DispositionApiTest {

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

      IOseeBranch dispoProgramBranch = dispoApi.getDispoProgramById(SAW_Bld_1.getGuid());
      String programId = dispoProgramBranch.getGuid();

      ResultSet<DispoSetData> dispoSets = dispoApi.getDispoSets(programId);
      DispoSetData devSet = dispoSets.getExactlyOne();
      String devSetId = devSet.getGuid();

      ResultSet<DispoItemData> dispoItems = dispoApi.getDispoItems(programId, devSetId);
      DispoItemData itemOne = dispoItems.getExactlyOne();
      String itemOneId = itemOne.getGuid();

      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(0, itemOne.getAnnotationsList().length());
      assertEquals(DispoStrings.Item_InComplete, itemOne.getStatus());

      DispoAnnotationData annotationOne = new DispoAnnotationData();
      annotationOne.setLocationRefs("1-10");
      annotationOne.setResolution("VALID");
      String createdOneId = dispoApi.createDispoAnnotation(programId, itemOneId, annotationOne);

      DispoAnnotationData annotationTwo = new DispoAnnotationData();
      annotationTwo.setLocationRefs("12-20");
      annotationTwo.setResolution("VALID");
      String createdTwoId = dispoApi.createDispoAnnotation(programId, itemOneId, annotationTwo);

      DispoAnnotationData annotationThree = new DispoAnnotationData();
      annotationThree.setLocationRefs("23,25,32-90");
      annotationThree.setResolution("VALID");
      String createdThreeId = dispoApi.createDispoAnnotation(programId, itemOneId, annotationThree);

      itemOne = dispoApi.getDispoItemById(programId, itemOneId);

      assertEquals(DispoStrings.Item_Complete, itemOne.getStatus());
      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(3, itemOne.getAnnotationsList().length());
      assertTrue(itemOne.getLastUpdate().after(itemOne.getCreationDate()));

      dispoApi.deleteDispoAnnotation(programId, itemOneId, createdTwoId);

      itemOne = dispoApi.getDispoItemById(programId, itemOneId);

      assertEquals(DispoStrings.Item_Complete, itemOne.getStatus());
      assertEquals(5, itemOne.getDiscrepanciesList().length());
      assertEquals(2, itemOne.getAnnotationsList().length());

      DispoAnnotationData actualAnnotation = dispoApi.getDispoAnnotationByIndex(programId, itemOneId, createdThreeId);
      assertEquals("23,25,32-90", actualAnnotation.getLocationRefs());

      actualAnnotation = dispoApi.getDispoAnnotationByIndex(programId, itemOneId, createdOneId);
      assertEquals("1-10", actualAnnotation.getLocationRefs());
   }

}
