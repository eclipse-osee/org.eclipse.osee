/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.rest.model.ReportFormat;
import org.junit.Test;

/**
 * Unit tests for {@link ReportFormat} enum.
 *
 * @author David W. Miller
 */
public class ReportFormatTest {

   @Test
   public void testFromStringXml() {
      assertEquals(ReportFormat.XML, ReportFormat.fromString("xml"));
      assertEquals(ReportFormat.XML, ReportFormat.fromString("XML"));
      assertEquals(ReportFormat.XML, ReportFormat.fromString("Xml"));
   }

   @Test
   public void testFromStringXlsx() {
      assertEquals(ReportFormat.XLSX, ReportFormat.fromString("xlsx"));
      assertEquals(ReportFormat.XLSX, ReportFormat.fromString("XLSX"));
   }

   @Test
   public void testFromStringHtml() {
      assertEquals(ReportFormat.HTML, ReportFormat.fromString("html"));
      assertEquals(ReportFormat.HTML, ReportFormat.fromString("HTML"));
   }

   @Test
   public void testFromStringNull() {
      assertEquals("null input should default to XML", ReportFormat.XML, ReportFormat.fromString(null));
   }

   @Test
   public void testFromStringBlank() {
      assertEquals("blank input should default to XML", ReportFormat.XML, ReportFormat.fromString(""));
      assertEquals("whitespace input should default to XML", ReportFormat.XML, ReportFormat.fromString("   "));
   }

   @Test(expected = OseeArgumentException.class)
   public void testFromStringInvalid() {
      ReportFormat.fromString("pdf");
   }

   @Test
   public void testExtensions() {
      assertEquals("xml", ReportFormat.XML.extension());
      assertEquals("xlsx", ReportFormat.XLSX.extension());
      assertEquals("html", ReportFormat.HTML.extension());
   }

   @Test
   public void testMediaTypes() {
      assertEquals("application/xml", ReportFormat.XML.mediaType());
      assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
         ReportFormat.XLSX.mediaType());
      assertEquals("text/html", ReportFormat.HTML.mediaType());
   }
}
