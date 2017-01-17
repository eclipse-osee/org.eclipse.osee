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
package org.eclipse.osee.disposition.rest.report;

import static org.junit.Assert.assertEquals;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.STRSReport;
import org.eclipse.osee.framework.core.data.BranchId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Angel Avila
 */

public class STRSReportTest {

   @Mock
   DispoApi dispoApi;

   @Mock
   BranchId branch;

   @Mock
   DispoSet set1;

   @Mock
   DispoSet set2;

   private final DispoItemData item1 = new DispoItemData();
   private final DispoItemData item2 = new DispoItemData();

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testReportGeneration() throws Exception {
      initItems();

      STRSReport reportWriter = new STRSReport(dispoApi);

      List<DispoItem> itemsForPrimary = new ArrayList<>();
      itemsForPrimary.add(item1);
      itemsForPrimary.add(item2);
      Mockito.when(dispoApi.getDispoItems(branch, set1.getGuid(), true)).thenReturn(itemsForPrimary);

      List<DispoItem> itemsForSecondary = new ArrayList<>();
      itemsForSecondary.add(item1);
      itemsForSecondary.add(item2);
      Mockito.when(dispoApi.getDispoItems(branch, set1.getGuid(), true)).thenReturn(itemsForSecondary);

      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      reportWriter.runReport(branch, set1, set2, bout);

      String reportOutput = bout.toString();

      Pattern rowPattern = Pattern.compile("</Row>.*?<Row>.*?</Row>", Pattern.DOTALL);
      Matcher rowMatcher = rowPattern.matcher(reportOutput);

      String firstItemRow = "";
      rowMatcher.find();
      firstItemRow = rowMatcher.group();

      // Now that we have the two rows, look at each cell

      Pattern cellPattern = Pattern.compile("<Cell>.*?</Cell>");
      Matcher cellMatcher = cellPattern.matcher(firstItemRow);

      List<String> cellsFirstRow = new ArrayList<>();
      int i = 0;
      while (cellMatcher.find()) {
         cellsFirstRow.add(i++, cellMatcher.group());
      }

      assertEquals("item1", cellsFirstRow.get(0).replaceAll("<.*?>", ""));
      assertEquals("5", cellsFirstRow.get(1).replaceAll("<.*?>", ""));
      assertEquals("2", cellsFirstRow.get(2).replaceAll("<.*?>", ""));
      assertEquals("5", cellsFirstRow.get(3).replaceAll("<.*?>", ""));
      assertEquals("1", cellsFirstRow.get(4).replaceAll("<.*?>", ""));
      assertEquals("1", cellsFirstRow.get(5).replaceAll("<.*?>", ""));
      assertEquals("0", cellsFirstRow.get(6).replaceAll("<.*?>", ""));
      assertEquals("0", cellsFirstRow.get(7).replaceAll("<.*?>", ""));
      assertEquals("0", cellsFirstRow.get(8).replaceAll("<.*?>", ""));
      assertEquals("2", cellsFirstRow.get(9).replaceAll("<.*?>", ""));
      assertEquals(" ", cellsFirstRow.get(10).replaceAll("<.*?>", "")); // Empty character
      assertEquals("Notes&#10;Notes&#10;", cellsFirstRow.get(11).replaceAll("<.*?>", ""));

   }

   private void initItems() {
      DispoConnector connector = new DispoConnector();

      Discrepancy discrepancy1 = new Discrepancy();
      discrepancy1.setId("one");
      discrepancy1.setLocation("1");
      Discrepancy discrepancy2 = new Discrepancy();
      discrepancy2.setId("two");
      discrepancy2.setLocation("3");
      Map<String, Discrepancy> discrepanciesListItem1 = new HashMap<>();
      discrepanciesListItem1.put("one", discrepancy1);
      discrepanciesListItem1.put("two", discrepancy2);
      item1.setDiscrepanciesList(discrepanciesListItem1);

      List<DispoAnnotationData> annotationsItem1 = new ArrayList<>();
      DispoAnnotationData annotation1 = new DispoAnnotationData();
      annotation1.setLocationRefs("1");
      annotation1.setResolutionType("CODE");
      annotation1.setIsResolutionValid(true);
      annotation1.setCustomerNotes("Notes");
      annotation1.setIdsOfCoveredDiscrepancies(new ArrayList<>());
      connector.connectAnnotation(annotation1, item1.getDiscrepanciesList());
      annotationsItem1.add(annotation1);

      DispoAnnotationData annotation2 = new DispoAnnotationData();
      annotation2.setLocationRefs("3");
      annotation2.setResolutionType("TEST");
      annotation2.setIsResolutionValid(true);
      annotation2.setCustomerNotes("Notes");
      annotation2.setIdsOfCoveredDiscrepancies(new ArrayList<>());
      connector.connectAnnotation(annotation2, item1.getDiscrepanciesList());
      annotationsItem1.add(annotation2);

      item1.setAnnotationsList(annotationsItem1);
      item1.setName("item1");
      item1.setTotalPoints("5");
      item1.setGuid("abc123");
      /////////////
      Discrepancy discrepancy3 = new Discrepancy();
      discrepancy3.setId("tth");
      discrepancy3.setLocation("4");
      Discrepancy discrepancy4 = new Discrepancy();
      discrepancy4.setId("fff");
      discrepancy4.setLocation("6");
      Map<String, Discrepancy> discrepanciesListItem2 = new HashMap<>();
      discrepanciesListItem2.put("tth", discrepancy3);
      discrepanciesListItem2.put("fff", discrepancy4);
      item2.setDiscrepanciesList(discrepanciesListItem2);

      List<DispoAnnotationData> annotationsItem2 = new ArrayList<>();

      DispoAnnotationData annotation3 = new DispoAnnotationData();
      annotation3.setLocationRefs("4");
      annotation3.setResolutionType("CODE");
      annotation3.setIsResolutionValid(true);
      annotation3.setCustomerNotes("Notes");
      annotation3.setIdsOfCoveredDiscrepancies(new ArrayList<>());
      connector.connectAnnotation(annotation3, item2.getDiscrepanciesList());
      annotationsItem2.add(annotation3);

      DispoAnnotationData annotation4 = new DispoAnnotationData();
      annotation4.setLocationRefs("6");
      annotation4.setResolutionType("TEST");
      annotation4.setIsResolutionValid(true);
      annotation4.setCustomerNotes("Notes");
      annotation4.setIdsOfCoveredDiscrepancies(new ArrayList<>());
      connector.connectAnnotation(annotation4, item2.getDiscrepanciesList());
      annotationsItem2.add(annotation4);

      item2.setAnnotationsList(annotationsItem2);
      item2.setName("item2");
      item2.setTotalPoints("6");
      item2.setGuid("bac123");
   }
}
