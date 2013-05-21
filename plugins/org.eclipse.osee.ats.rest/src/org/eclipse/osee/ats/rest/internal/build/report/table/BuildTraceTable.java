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
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.rest.internal.build.report.model.AtsElementData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Conditions;
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

   public BuildTraceTable(OutputStream output) {
      this.output = output;
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
   }

   private Anchor setHyperlink(String element, String link) {
      Anchor anchor = new Anchor(element);
      anchor.setReference(link);
      return anchor;
   }

   public void addbuildTraceCells(Table buildTraceTable, String element, UriInfo uriInfo) throws OseeCoreException {
      String url = String.format(AtsElementData.CHANGE_REPORT_URL_TEMPLATE, uriInfo.getBaseUri(), element);
      try {
         buildTraceTable.addCell(setHyperlink(element.toString(), url));
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

   }

   public void addRequirementTraceCells(Table nestedRequirementTable, ArtifactReadable element) throws OseeCoreException {

      try {
         nestedRequirementTable.addCell(element.getName());
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public void addNewTestScriptTraceCells(Table nestedTestScriptTable, List<ArtifactReadable> testScripts) throws OseeCoreException {
      if (testScripts != null) {
         for (ArtifactReadable script : testScripts) {
            try {
               if (!script.getName().isEmpty()) {
                  nestedTestScriptTable.addCell(script.getName());
               }
            } catch (BadElementException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
         }
      }
   }

   public void addRpcrToTable(String rpcr, Map<ArtifactReadable, List<ArtifactReadable>> requirementsToTests, UriInfo uriInfo) throws OseeCoreException {

      try {
         addbuildTraceCells(traceReportTable, rpcr, uriInfo);
         Table nestedRequirementTable = new Table(2);
         nestedRequirementTable.setAutoFillEmptyCells(true);
         for (ArtifactReadable changedReq : requirementsToTests.keySet()) {
            if (Conditions.notNull(changedReq)) {
               addRequirementTraceCells(nestedRequirementTable, changedReq);
               Table nestedTestScriptTable = new Table(1);
               nestedRequirementTable.setAutoFillEmptyCells(true);
               addNewTestScriptTraceCells(nestedTestScriptTable, requirementsToTests.get(changedReq));
               nestedRequirementTable.insertTable(nestedTestScriptTable);
            }
         }
         traceReportTable.insertTable(nestedRequirementTable);
      } catch (BadElementException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

   }

   public void close() throws OseeCoreException {
      reportTable.insertTable(traceReportTable);

      try {
         document.add(reportTable);
      } catch (DocumentException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      document.close();
   }

}
