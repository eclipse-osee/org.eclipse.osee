/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.OperationReport;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.importer.DispoSetCopier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Angel Avila
 */
public class AnnotationCopierTest {

   private final DispoConnector connector = new DispoConnector();

   String itemName = "ABC.hello_world";

   Discrepancy discrepancy1;
   Discrepancy discrepancy2;
   Discrepancy discrepancy3;
   Discrepancy discrepancy4;
   Discrepancy discrepancy5;

   DispoItemData sourceItem;
   DispoItemData destItem;

   @Before
   public void setup() {
      sourceItem = new DispoItemData();
      sourceItem.setName(itemName);
      List<DispoAnnotationData> sourceAnnotations = new ArrayList<>();

      destItem = new DispoItemData();
      destItem.setName(itemName);
      List<DispoAnnotationData> destAnnotations = new ArrayList<>();

      //Case 1: Source is empty and dest is filled. Want to maintain LastResolution & LastManualResolution
      DispoAnnotationData sourceAnnotation1 = new DispoAnnotationData();
      sourceAnnotation1.setLocationRefs("1");
      sourceAnnotation1.setResolutionType("");
      sourceAnnotation1.setResolution("");
      sourceAnnotation1.setIsResolutionValid(false);
      sourceAnnotation1.setIndex(0);
      sourceAnnotation1.setCustomerNotes("int i = 1");
      sourceAnnotation1.setLastResolutionType(DispoStrings.Test_Unit_Resolution);
      sourceAnnotation1.setLastResolution("last_resolution.source");
      sourceAnnotation1.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation1.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation1);

      DispoAnnotationData destAnnotation1 = new DispoAnnotationData();
      destAnnotation1.setLocationRefs("1");
      destAnnotation1.setResolutionType(DispoStrings.Test_Unit_Resolution);
      destAnnotation1.setResolution("last_resolution.dest");
      destAnnotation1.setIsResolutionValid(true);
      destAnnotation1.setIndex(0);
      destAnnotation1.setCustomerNotes("int i = 1");
      destAnnotations.add(destAnnotation1);

      //Case 2: Both source and dest are unresolved. Source has a valid Last Manual Resolution. Want to put LastManualResolution in Resolution.
      DispoAnnotationData sourceAnnotation2 = new DispoAnnotationData();
      sourceAnnotation2.setLocationRefs("2");
      sourceAnnotation2.setResolutionType("");
      sourceAnnotation2.setResolution("");
      sourceAnnotation2.setIsResolutionValid(false);
      sourceAnnotation2.setIndex(1);
      sourceAnnotation2.setCustomerNotes("int i = 2");
      sourceAnnotation2.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation2.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation2);

      DispoAnnotationData destAnnotation2 = new DispoAnnotationData();
      destAnnotation2.setLocationRefs("2");
      destAnnotation2.setResolutionType("");
      destAnnotation2.setResolution("");
      destAnnotation2.setIsResolutionValid(false);
      destAnnotation2.setIndex(1);
      destAnnotation2.setCustomerNotes("int i = 2");
      destAnnotations.add(destAnnotation2);

      //Case 3: Both source and dest are unresolved. Source has a MODIFY Last Manual Resolution. Want to put LastManualResolution in Resolution. Want to make sure modify is set.
      DispoAnnotationData sourceAnnotation3 = new DispoAnnotationData();
      sourceAnnotation3.setLocationRefs("3");
      sourceAnnotation3.setResolutionType("");
      sourceAnnotation3.setResolution("");
      sourceAnnotation3.setIsResolutionValid(true);
      sourceAnnotation3.setIndex(2);
      sourceAnnotation3.setCustomerNotes("int i = 3");
      sourceAnnotation3.setLastManualResolutionType(DispoStrings.MODIFY_CODE);
      sourceAnnotation3.setLastManualResolution("last_manual_resolution_modify.source");
      sourceAnnotations.add(sourceAnnotation3);

      DispoAnnotationData destAnnotation3 = new DispoAnnotationData();
      destAnnotation3.setLocationRefs("3");
      destAnnotation3.setResolutionType("");
      destAnnotation3.setResolution("");
      destAnnotation3.setIsResolutionValid(false);
      destAnnotation3.setIndex(2);
      destAnnotation3.setCustomerNotes("int i = 3");
      destAnnotations.add(destAnnotation3);

      //Case 4: Both source and dest are covered by a default resolution type. Want to put src resolution into dest Last Resolution. Maintain Last Manual Resolution.
      DispoAnnotationData sourceAnnotation4 = new DispoAnnotationData();
      sourceAnnotation4.setLocationRefs("4");
      sourceAnnotation4.setResolutionType(DispoStrings.Test_Unit_Resolution);
      sourceAnnotation4.setResolution("resolution.source");
      sourceAnnotation4.setIsResolutionValid(true);
      sourceAnnotation4.setIndex(3);
      sourceAnnotation4.setCustomerNotes("int i = 4");
      sourceAnnotation4.setLastResolutionType(DispoStrings.Test_Unit_Resolution);
      sourceAnnotation4.setLastResolution("last_resolution.source");
      sourceAnnotation4.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation4.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation4);

      DispoAnnotationData destAnnotation4 = new DispoAnnotationData();
      destAnnotation4.setLocationRefs("4");
      destAnnotation4.setResolutionType(DispoStrings.Test_Unit_Resolution);
      destAnnotation4.setResolution("resolution.dest");
      destAnnotation4.setIsResolutionValid(true);
      destAnnotation4.setIndex(3);
      destAnnotation4.setCustomerNotes("int i = 4");
      destAnnotations.add(destAnnotation4);

      //Case 5: Source is covered by a default resolution type and dest is blank. Put source's Last Manual Resolution as dest's resolution.
      DispoAnnotationData sourceAnnotation5 = new DispoAnnotationData();
      sourceAnnotation5.setLocationRefs("5");
      sourceAnnotation5.setResolutionType(DispoStrings.Test_Unit_Resolution);
      sourceAnnotation5.setResolution("resolution.source");
      sourceAnnotation5.setIsResolutionValid(true);
      sourceAnnotation5.setIndex(4);
      sourceAnnotation5.setCustomerNotes("int i = 5");
      sourceAnnotation5.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation5.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation5);

      DispoAnnotationData destAnnotation5 = new DispoAnnotationData();
      destAnnotation5.setLocationRefs("5");
      destAnnotation5.setResolutionType("");
      destAnnotation5.setResolution("");
      destAnnotation5.setIsResolutionValid(false);
      destAnnotation5.setIndex(4);
      destAnnotation5.setCustomerNotes("int i = 5");
      destAnnotations.add(destAnnotation5);

      //Case 6: Source is a default resolution type and dest is unresolved. Source has a MODIFY Last Manual Resolution. Want to put LastManualResolution in Resolution.
      DispoAnnotationData sourceAnnotation6 = new DispoAnnotationData();
      sourceAnnotation6.setLocationRefs("6");
      sourceAnnotation6.setResolutionType(DispoStrings.Test_Unit_Resolution);
      sourceAnnotation6.setResolution("resolution.source");
      sourceAnnotation6.setIsResolutionValid(true);
      sourceAnnotation6.setIndex(5);
      sourceAnnotation6.setCustomerNotes("int i = 6");
      sourceAnnotation6.setLastManualResolutionType(DispoStrings.MODIFY_CODE);
      sourceAnnotation6.setLastManualResolution("last_manual_resolution_modify.source");
      sourceAnnotations.add(sourceAnnotation6);

      DispoAnnotationData destAnnotation6 = new DispoAnnotationData();
      destAnnotation6.setLocationRefs("6");
      destAnnotation6.setResolutionType("");
      destAnnotation6.setResolution("");
      destAnnotation6.setIsResolutionValid(false);
      destAnnotation6.setIndex(5);
      destAnnotation6.setCustomerNotes("int i = 6");
      destAnnotations.add(destAnnotation6);

      //Case 7: Source is manual resolution, dest is default resolution. Carry over source resolution into Last Resolution and Last Manual Resolution field.
      DispoAnnotationData sourceAnnotation7 = new DispoAnnotationData();
      sourceAnnotation7.setLocationRefs("7");
      sourceAnnotation7.setResolutionType("Manual_Disposition");
      sourceAnnotation7.setResolution("manual_resolution.source");
      sourceAnnotation7.setIsResolutionValid(true);
      sourceAnnotation7.setIndex(6);
      sourceAnnotation7.setCustomerNotes("int i = 7");
      sourceAnnotation7.setLastResolutionType("Manual_Disposition");
      sourceAnnotation7.setLastResolution("last_resolution.source");
      sourceAnnotation7.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation7.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation7);

      DispoAnnotationData destAnnotation7 = new DispoAnnotationData();
      destAnnotation7.setLocationRefs("7");
      destAnnotation7.setResolutionType("Manual_Disposition");
      destAnnotation7.setResolution("manual_resolution.dest");
      destAnnotation7.setIsResolutionValid(true);
      destAnnotation7.setIndex(6);
      destAnnotation7.setCustomerNotes("int i = 7");
      destAnnotation7.setLastResolutionType("Manual_Disposition");
      destAnnotation7.setLastResolution("last_resolution.dest");
      destAnnotation7.setLastManualResolutionType("Manual_Disposition");
      destAnnotation7.setLastManualResolution("last_manual_resolution.dest");
      destAnnotations.add(destAnnotation7);

      //Case 8: Source is manual resolution, dest is blank. Carry over all dispositioning data one to one.
      DispoAnnotationData sourceAnnotation8 = new DispoAnnotationData();
      sourceAnnotation8.setLocationRefs("8");
      sourceAnnotation8.setResolutionType("Manual_Disposition");
      sourceAnnotation8.setResolution("manual_resolution.source");
      sourceAnnotation8.setIsResolutionValid(true);
      sourceAnnotation8.setIndex(7);
      sourceAnnotation8.setCustomerNotes("int i = 8");
      sourceAnnotation8.setLastResolutionType("Manual_Disposition");
      sourceAnnotation8.setLastResolution("last_resolution.source");
      sourceAnnotation8.setLastManualResolutionType("Manual_Disposition");
      sourceAnnotation8.setLastManualResolution("last_manual_resolution.source");
      sourceAnnotations.add(sourceAnnotation8);

      DispoAnnotationData destAnnotation8 = new DispoAnnotationData();
      destAnnotation8.setLocationRefs("8");
      destAnnotation8.setResolutionType("");
      destAnnotation8.setResolution("");
      destAnnotation8.setIsResolutionValid(false);
      destAnnotation8.setIndex(7);
      destAnnotation8.setCustomerNotes("int i = 8");
      destAnnotations.add(destAnnotation8);

      //Case 9: Source is manual resolution (MODIFY), dest is blank. Carry over all dispositioning data one to one. Want resolution to be invalid.
      DispoAnnotationData sourceAnnotation9 = new DispoAnnotationData();
      sourceAnnotation9.setLocationRefs("9");
      sourceAnnotation9.setResolutionType(DispoStrings.MODIFY_CODE);
      sourceAnnotation9.setResolution("last_manual_resolution_modify.source");
      sourceAnnotation9.setIsResolutionValid(true);
      sourceAnnotation9.setIndex(8);
      sourceAnnotation9.setCustomerNotes("int i = 9");
      sourceAnnotations.add(sourceAnnotation9);

      DispoAnnotationData destAnnotation9 = new DispoAnnotationData();
      destAnnotation9.setLocationRefs("9");
      destAnnotation9.setResolutionType("");
      destAnnotation9.setResolution("");
      destAnnotation9.setIsResolutionValid(false);
      destAnnotation9.setIndex(8);
      destAnnotation9.setCustomerNotes("int i = 9");
      destAnnotations.add(destAnnotation9);

      //Case 10: Source and dest are manual resolutions. Set dest's Last Resolution and Last Manual Resolution to source's resolution.
      DispoAnnotationData sourceAnnotation10 = new DispoAnnotationData();
      sourceAnnotation10.setLocationRefs("10");
      sourceAnnotation10.setResolutionType("Manual_Disposition");
      sourceAnnotation10.setResolution("manual_resolution.source");
      sourceAnnotation10.setIsResolutionValid(true);
      sourceAnnotation10.setIndex(9);
      sourceAnnotation10.setCustomerNotes("int i = 10");
      sourceAnnotations.add(sourceAnnotation10);

      DispoAnnotationData destAnnotation10 = new DispoAnnotationData();
      destAnnotation10.setLocationRefs("10");
      destAnnotation10.setResolutionType("Manual_Disposition");
      destAnnotation10.setResolution("manual_resolution.dest");
      destAnnotation10.setIsResolutionValid(true);
      destAnnotation10.setIndex(9);
      destAnnotation10.setCustomerNotes("int i = 10");
      destAnnotations.add(destAnnotation10);

      sourceItem.setAnnotationsList(sourceAnnotations);
      destItem.setAnnotationsList(destAnnotations);
   }

   @Test
   public void testCopyCoverageToNewSet() throws Exception {
      DispoItemData destItemForTest = destItem;

      OperationReport report = new OperationReport();
      DispoSetCopier copier = new DispoSetCopier(connector);
      Map<String, Set<DispoItemData>> nameToItems = new HashMap<>();
      nameToItems.put(destItemForTest.getName(), Collections.singleton(destItemForTest));
      List<DispoItem> toModify = copier.copyAllDispositions(nameToItems,
         Collections.singletonList((DispoItem) sourceItem), true, null, false, Collections.emptySet(), false, report);

      List<DispoAnnotationData> modifiedItemAnnotations = toModify.get(0).getAnnotationsList();

      DispoAnnotationData case1Annot = modifiedItemAnnotations.get(0);
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case1Annot.getResolutionType());
      Assert.assertEquals("last_resolution.dest", case1Annot.getResolution());
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case1Annot.getLastResolutionType());
      Assert.assertEquals("last_resolution.source", case1Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case1Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case1Annot.getLastManualResolution());
      Assert.assertEquals(true, case1Annot.getIsResolutionValid());

      DispoAnnotationData case2Annot = modifiedItemAnnotations.get(1);
      Assert.assertEquals("Manual_Disposition", case2Annot.getResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case2Annot.getResolution());
      Assert.assertEquals("", case2Annot.getLastResolutionType());
      Assert.assertEquals("", case2Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case2Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case2Annot.getLastManualResolution());
      Assert.assertEquals(true, case2Annot.getIsResolutionValid());

      DispoAnnotationData case3Annot = modifiedItemAnnotations.get(2);
      Assert.assertEquals(DispoStrings.MODIFY_CODE, case3Annot.getResolutionType());
      Assert.assertEquals("last_manual_resolution_modify.source", case3Annot.getResolution());
      Assert.assertEquals("", case3Annot.getLastResolutionType());
      Assert.assertEquals("", case3Annot.getLastResolution());
      Assert.assertEquals(DispoStrings.MODIFY_CODE, case3Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution_modify.source", case3Annot.getLastManualResolution());
      Assert.assertEquals(true, case3Annot.getIsResolutionValid());

      DispoAnnotationData case4Annot = modifiedItemAnnotations.get(3);
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case4Annot.getResolutionType());
      Assert.assertEquals("resolution.dest", case4Annot.getResolution());
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case4Annot.getLastResolutionType());
      Assert.assertEquals("resolution.source", case4Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case4Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case4Annot.getLastManualResolution());
      Assert.assertEquals(true, case4Annot.getIsResolutionValid());

      DispoAnnotationData case5Annot = modifiedItemAnnotations.get(4);
      Assert.assertEquals("Manual_Disposition", case5Annot.getResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case5Annot.getResolution());
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case5Annot.getLastResolutionType());
      Assert.assertEquals("resolution.source", case5Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case5Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case5Annot.getLastManualResolution());
      Assert.assertEquals(true, case5Annot.getIsResolutionValid());

      DispoAnnotationData case6Annot = modifiedItemAnnotations.get(5);
      Assert.assertEquals(DispoStrings.MODIFY_CODE, case6Annot.getResolutionType());
      Assert.assertEquals("last_manual_resolution_modify.source", case6Annot.getResolution());
      Assert.assertEquals(DispoStrings.Test_Unit_Resolution, case6Annot.getLastResolutionType());
      Assert.assertEquals("resolution.source", case6Annot.getLastResolution());
      Assert.assertEquals(DispoStrings.MODIFY_CODE, case6Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution_modify.source", case6Annot.getLastManualResolution());
      Assert.assertEquals(true, case6Annot.getIsResolutionValid());

      DispoAnnotationData case7Annot = modifiedItemAnnotations.get(6);
      Assert.assertEquals("Manual_Disposition", case7Annot.getResolutionType());
      Assert.assertEquals("manual_resolution.dest", case7Annot.getResolution());
      Assert.assertEquals("Manual_Disposition", case7Annot.getLastResolutionType());
      Assert.assertEquals("manual_resolution.source", case7Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case7Annot.getLastManualResolutionType());
      Assert.assertEquals("manual_resolution.source", case7Annot.getLastManualResolution());
      Assert.assertEquals(true, case7Annot.getIsResolutionValid());

      DispoAnnotationData case8Annot = modifiedItemAnnotations.get(7);
      Assert.assertEquals("Manual_Disposition", case8Annot.getResolutionType());
      Assert.assertEquals("manual_resolution.source", case8Annot.getResolution());
      Assert.assertEquals("Manual_Disposition", case8Annot.getLastResolutionType());
      Assert.assertEquals("last_resolution.source", case8Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case8Annot.getLastManualResolutionType());
      Assert.assertEquals("last_manual_resolution.source", case8Annot.getLastManualResolution());
      Assert.assertEquals(true, case8Annot.getIsResolutionValid());

      DispoAnnotationData case9Annot = modifiedItemAnnotations.get(8);
      Assert.assertEquals(DispoStrings.MODIFY_CODE, case9Annot.getResolutionType());
      Assert.assertEquals("last_manual_resolution_modify.source", case9Annot.getResolution());
      Assert.assertEquals("", case9Annot.getLastResolutionType());
      Assert.assertEquals("", case9Annot.getLastResolution());
      Assert.assertEquals("", case9Annot.getLastManualResolutionType());
      Assert.assertEquals("", case9Annot.getLastManualResolution());
      Assert.assertEquals(true, case9Annot.getIsResolutionValid());

      DispoAnnotationData case10Annot = modifiedItemAnnotations.get(9);
      Assert.assertEquals("Manual_Disposition", case10Annot.getResolutionType());
      Assert.assertEquals("manual_resolution.dest", case10Annot.getResolution());
      Assert.assertEquals("Manual_Disposition", case10Annot.getLastResolutionType());
      Assert.assertEquals("manual_resolution.source", case10Annot.getLastResolution());
      Assert.assertEquals("Manual_Disposition", case10Annot.getLastManualResolutionType());
      Assert.assertEquals("manual_resolution.source", case10Annot.getLastManualResolution());
      Assert.assertEquals(true, case10Annot.getIsResolutionValid());
   }
}