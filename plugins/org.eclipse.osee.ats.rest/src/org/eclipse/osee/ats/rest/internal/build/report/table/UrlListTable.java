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
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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
   private SortedSet<List<Anchor>> sortedList;

   public UrlListTable(OutputStream output) {
      this.output = output;
   }

   public void initializeTable(String title, String... headers) throws OseeCoreException {
      document = new Document();
      sortedList = new TreeSet<List<Anchor>>(new Comparator<List<Anchor>>() {

         @Override
         public int compare(List<Anchor> anchor1, List<Anchor> anchor2) {
            String name1 = anchor1.get(0).toString();
            String name2 = anchor2.get(0).toString();
            return name1.compareTo(name2);
         }
      });
      HtmlWriter.getInstance(document, output);
      document.addTitle(title);
      document.open();
      try {
         table = new Table(headers.length);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      for (String header : headers) {
         Cell headerCell = new Cell(header);
         headerCell.setHeader(true);
         headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
         table.addCell(headerCell);
      }
      table.setWidth(10f * headers.length);
      table.setAlignment(Element.ALIGN_LEFT);
   }

   public void addUrl(Pair<String, String>... urls) {
      List<Anchor> toAdd = new LinkedList<Anchor>();
      for (Pair<String, String> url : urls) {
         Anchor anchor = new Anchor(url.getFirst());
         anchor.setReference(url.getSecond());
         // Save to a sorted set until complete then add to table in 'close()'
         toAdd.add(anchor);
      }
      sortedList.add(toAdd);
   }

   public void close() throws OseeCoreException {
      try {
         // Create Table from sorted set
         Iterator<List<Anchor>> treeItr = sortedList.iterator();
         while (treeItr.hasNext()) {
            List<Anchor> next = treeItr.next();
            for (Anchor a : next) {
               table.addCell(a);
            }
         }
         document.add(table);
      } catch (DocumentException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      document.close();
   }
}
