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
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
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
   private SortedSet<Anchor> sortedList;

   public UrlListTable(OutputStream output) {
      this.output = output;
   }

   public void initializeTable(String title, String header) throws OseeCoreException {
      document = new Document();
      sortedList = new TreeSet<Anchor>(new Comparator<Anchor>() {

         @Override
         public int compare(Anchor anchor1, Anchor anchor2) {
            String name1 = anchor1.get(0).toString();
            String name2 = anchor2.get(0).toString();
            return name1.compareTo(name2);
         }
      });
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

   public void addUrl(String name, String url) {
      Anchor anchor = new Anchor(name);
      anchor.setReference(url);
      // Save to a sorted set until complete then add to table in 'close()'
      sortedList.add(anchor);
   }

   public void close() throws OseeCoreException {
      try {
         // Create Table from sorted set
         Iterator<Anchor> treeItr = sortedList.iterator();
         while (treeItr.hasNext()) {
            table.addCell(treeItr.next());
         }
         document.add(table);
      } catch (DocumentException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      document.close();
   }
}
