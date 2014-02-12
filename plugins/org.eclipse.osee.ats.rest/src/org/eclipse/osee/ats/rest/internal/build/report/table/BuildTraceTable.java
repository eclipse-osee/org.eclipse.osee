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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.rtf.table.RtfCell;

/**
 * @author Megumi Telles
 */
public class BuildTraceTable {

   private Table traceReportTable;
   private Table reportTable;
   private Table nestedHeaderTable;
   private Document document;
   private final OutputStream output;
   private final String changeReportUrlTemplate;
   private final VerifierUriProvider uriProvider;
   private SortedSet<Pair<String, Table>> sortedRpcr;

   private static Comparator<Pair<String, Table>> PairCompare = new Comparator<Pair<String, Table>>() {
      @Override
      public int compare(Pair<String, Table> pair1, Pair<String, Table> pair2) {
         String name1 = pair1.getFirst();
         String name2 = pair2.getFirst();
         return name1.compareTo(name2);
      }
   };

   public static interface VerifierUriProvider {
      List<Pair<String, String>> getBuildToUrlPairs(String verifierName);

      int getColumnCount();
   }

   public BuildTraceTable(OutputStream output, String changeReportUrlTemplate, VerifierUriProvider uriProvider) {
      this.output = output;
      this.changeReportUrlTemplate = changeReportUrlTemplate;
      this.uriProvider = uriProvider;
   }

   private void createTables() throws OseeCoreException {
      try {
         reportTable = new Table(1);
         nestedHeaderTable = new Table(2);
         traceReportTable = new Table(2);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void initializeTraceReportTable(String program, String build) throws OseeCoreException {
      String header = String.format("%s: [%s - %s]", AtsElementData.BUILD_TRACE_REPORT, program, build);
      document = new Document();
      document.addTitle(header);
      HtmlWriter.getInstance(document, output);
      document.open();
      createTables();
      Cell headerCell = new RtfCell(header);
      headerCell.setColspan(1);
      headerCell.setHeader(true);
      headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      reportTable.addCell(headerCell);
      reportTable.endHeaders();
      reportTable.setTableFitsPage(true);

      Cell rpcrCell = new RtfCell(AtsElementData.RPCR);
      rpcrCell.setHeader(true);
      rpcrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      traceReportTable.addCell(rpcrCell);

      Cell req = new Cell(AtsElementData.REQUIREMENT);
      req.setHeader(true);
      req.setHorizontalAlignment(Element.ALIGN_CENTER);
      nestedHeaderTable.addCell(req);
      Cell script = new Cell(AtsElementData.TEST_SCRIPT);
      script.setHeader(true);
      script.setHorizontalAlignment(Element.ALIGN_CENTER);
      nestedHeaderTable.addCell(script);

      traceReportTable.insertTable(nestedHeaderTable);
      sortedRpcr = new TreeSet<Pair<String, Table>>(PairCompare);

   }

   private Anchor setHyperlink(String element, String link) {
      Anchor anchor = new Anchor(element);
      anchor.setReference(link);
      return anchor;
   }

   private void addbuildTraceCells(Table buildTraceTable, String element) throws OseeCoreException {
      String url = String.format(changeReportUrlTemplate, element);
      try {
         buildTraceTable.addCell(setHyperlink(element.toString(), url));
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

   }

   private void addRequirementTraceCells(Table nestedRequirementTable, String element) throws OseeCoreException {
      try {
         nestedRequirementTable.addCell(element);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void addNewTestScriptTraceCells(Table nestedTestScriptTable, Iterable<ArtifactReadable> testScripts) throws OseeCoreException {
      if (testScripts != null) {
         // If test script has a name store in sorted set
         SortedSet<String> sortedList = new TreeSet<String>();
         for (ArtifactReadable script : testScripts) {
            if (!script.getName().isEmpty()) {
               sortedList.add(script.getName());
            }
         }
         // Create test script Table
         Iterator<String> treeItr = sortedList.iterator();
         while (treeItr.hasNext()) {
            try {
               String verifierName = treeItr.next();
               nestedTestScriptTable.addCell(verifierName);
               for (Pair<String, String> nameToUri : uriProvider.getBuildToUrlPairs(verifierName)) {
                  Anchor toAdd = setHyperlink(nameToUri.getFirst(), nameToUri.getSecond());
                  nestedTestScriptTable.addCell(toAdd);
               }
            } catch (BadElementException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
         }
      }
   }

   public void addRpcrToTable(String rpcr, Map<ArtifactReadable, Iterable<ArtifactReadable>> requirementsToTests) throws OseeCoreException {
      try {
         Table nestedRequirementTable = new Table(2);
         //         nestedRequirementTable.setAutoFillEmptyCells(true);
         SortedSet<Pair<String, Table>> nestedRpcr = new TreeSet<Pair<String, Table>>(PairCompare);

         for (Entry<ArtifactReadable, Iterable<ArtifactReadable>> entry : requirementsToTests.entrySet()) {
            ArtifactReadable changedReq = entry.getKey();
            if (Conditions.notNull(changedReq)) {
               Table nestedTestScriptTable = new Table(1 + uriProvider.getColumnCount());
               nestedTestScriptTable.setAutoFillEmptyCells(true);
               addNewTestScriptTraceCells(nestedTestScriptTable, entry.getValue());
               // Store Requirement string and TestScriptTable for sorting
               Pair<String, Table> newPair = new Pair<String, Table>(changedReq.getName(), nestedTestScriptTable);
               nestedRpcr.add(newPair);
            }
         }
         // Create sorted requirement Table
         Iterator<Pair<String, Table>> treeItr = nestedRpcr.iterator();
         while (treeItr.hasNext()) {
            Pair<String, Table> pair = treeItr.next();
            addRequirementTraceCells(nestedRequirementTable, pair.getFirst());
            nestedRequirementTable.insertTable(pair.getSecond());
         }
         // Save RPCR ID with sorted requirement string Table.
         Pair<String, Table> newPair = new Pair<String, Table>(rpcr, nestedRequirementTable);
         sortedRpcr.add(newPair);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

   }

   public void close() throws OseeCoreException {
      try {
         // Create sorted RPCR Table
         Iterator<Pair<String, Table>> treeItr = sortedRpcr.iterator();
         while (treeItr.hasNext()) {
            Pair<String, Table> pair = treeItr.next();
            addbuildTraceCells(traceReportTable, pair.getFirst());
            traceReportTable.insertTable(pair.getSecond());
         }
         reportTable.insertTable(traceReportTable);
         document.add(reportTable);
      } catch (DocumentException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      document.close();
   }

}
