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
package org.eclipse.osee.ats.rest.internal.build.report.table;

import java.io.OutputStream;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;

/**
 * @author John Misinco
 */
public class UrlListTable {

   private final OutputStream output;
   private Table table;
   private Document document;

   public UrlListTable(OutputStream output) {
      this.output = output;
   }

   public void initializeTable(String title, String header) throws OseeCoreException {
      document = new Document();
      HtmlWriter.getInstance(document, output);
      document.addTitle(title);
      document.open();
      try {
         table = new Table(1);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      Cell headerCell = new Cell(header);
      headerCell.setHeader(true);
      headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(headerCell);
      table.setWidth(10f);
      table.setAlignment(Element.ALIGN_LEFT);
   }

   public void addUrl(String name, String url) throws OseeCoreException {
      Anchor anchor = new Anchor(name);
      anchor.setReference(url);
      try {
         table.addCell(anchor);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void close() throws OseeCoreException {
      try {
         document.add(table);
      } catch (DocumentException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      document.close();
   }
}
