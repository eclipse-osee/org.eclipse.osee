/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link DoorsTableRowCollector}
 *
 * @author David Miller
 */
public final class DoorsTableRowCollectorTest {
   private DoorsTableRowCollector dtc;

   @Mock
   private DoorsArtifactExtractor extractor;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
      dtc = new DoorsTableRowCollector(extractor);
   }

   @Test
   public void testAddRawRow() {
      loadElementsInCollector();
      Assert.assertEquals(dtc.getTableRows().size(), 2);
   }

   @Test(expected = OseeStateException.class)
   public void testCreateArtifactsEmpty() {
      RoughArtifact roughParent = new RoughArtifact(RoughArtifactKind.CONTAINER);
      RoughArtifactCollector collector = new RoughArtifactCollector(roughParent);
      dtc.createArtifacts(collector);
      // exception because there is no data
   }

   @Test
   public void testGetHTML() {
      loadElementsInCollector();
      String result = dtc.getHTML(dtc.getTableRow(1), DoorsColumnType.REQUIREMENTS);
      String shortenedResult = result.substring(0, 28);
      Assert.assertEquals(shortenedResult, "<a name=\"X3\"> </a><b>1 SCOPE");
   }

   @Test(expected = OseeStateException.class)
   public void testGetHTMLEmpty() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      DoorsTableRow dtr = new DoorsTableRow();
      dtc.getHTML(dtr, DoorsColumnType.OBJECT_TEXT);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetHTMLNullInput() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      dtc.getHTML(null, DoorsColumnType.OBJECT_TEXT);
   }

   @Test(expected = OseeStateException.class)
   public void testGetPreferredNameEmpty() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      DoorsTableRow dtr = new DoorsTableRow();
      dtc.getPreferredName(dtr);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetPreferredNameNullInput() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      dtc.getPreferredName(null);
   }

   @Test
   public void testGetPreferredName() {
      loadElementsInCollector();
      String result = dtc.getPreferredName(dtc.getTableRow(1));
      Assert.assertEquals(result, "SCOPE");
   }

   @Test
   public void testGetSimpleText() {
      loadElementsInCollector();
      String result = dtc.getHTML(dtc.getTableRow(1), DoorsColumnType.DATA_TYPE);
      Assert.assertEquals(result, "Heading");
   }

   @Test(expected = OseeStateException.class)
   public void testGetSimpleTextEmpty() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      DoorsTableRow dtr = new DoorsTableRow();
      dtc.getHTML(dtr, DoorsColumnType.EFFECTIVITY);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetSimpleTextNullInput() {
      DoorsTableRowCollector dtc = new DoorsTableRowCollector(extractor);
      dtc.getHTML(null, DoorsColumnType.ID);
   }

   @Test(expected = OseeArgumentException.class)
   public void testFirstRowNotHeaderRow() {
      Elements elements = new DoorsJsoupElementUtility().getJsoupElements();
      dtc.addRawRow(elements.get(1));
   }

   private void loadElementsInCollector() {
      Elements elements = new DoorsJsoupElementUtility().getJsoupElements();
      dtc.addRawRow(elements.get(0));
      dtc.addRawRow(elements.get(1));
   }
}
