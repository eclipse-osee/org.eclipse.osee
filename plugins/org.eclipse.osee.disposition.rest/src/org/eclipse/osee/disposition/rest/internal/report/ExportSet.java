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
package org.eclipse.osee.disposition.rest.internal.report;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class ExportSet {
   private final DispoApi dispoApi;

   public ExportSet(DispoApi dispoApi) {
      this.dispoApi = dispoApi;
   }

   public void runReport(DispoProgram program, DispoSet setPrimary, String option, OutputStream outputStream) {
      List<DispoItem> items = dispoApi.getDispoItems(program, setPrimary.getGuid());

      try {
         Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
         ExcelXmlWriter sheetWriter = new ExcelXmlWriter(writer);

         String[] headers = getHeadersDetailed();
         int columns = headers.length;
         sheetWriter.startSheet(setPrimary.getName(), headers.length);
         sheetWriter.writeRow((Object[]) headers);

         for (DispoItem item : items) {
            DispoConnector connector = new DispoConnector();
            List<Integer> allUncoveredDiscprepancies = connector.getAllUncoveredDiscprepancies(item);
            String[] row = new String[columns];
            int index = 0;

            JSONObject discrepanciesList = item.getDiscrepanciesList();

            row[index++] = String.valueOf(item.getName());
            row[index++] = String.valueOf(item.getCategory());
            row[index++] = String.valueOf(item.getStatus());
            row[index++] = String.valueOf(item.getTotalPoints());
            row[index++] = String.valueOf(item.getDiscrepanciesList().length());
            row[index++] = String.valueOf(DispoUtil.discrepanciesToString(discrepanciesList));
            row[index++] = String.valueOf(allUncoveredDiscprepancies.size());
            row[index++] = String.valueOf(LocationRangesCompressor.compress(allUncoveredDiscprepancies));
            row[index++] = String.valueOf(item.getAssignee());
            row[index++] = String.valueOf(item.getItemNotes());
            row[index++] = String.valueOf(item.getNeedsRerun());
            row[index++] = String.valueOf(item.getAborted());
            row[index++] = String.valueOf(item.getMachine());

            sheetWriter.writeRow((Object[]) row);
         }

         sheetWriter.endSheet();
         sheetWriter.endWorkbook();

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

   }

   private static String[] getHeadersDetailed() {
      String[] toReturn = {//
         "Script Name",//
            "Category",//
            "Status",//
            "Total Test Points",//
            "Failures",//
            "Failed Points",//
            "Remaining Count",//
            "Remaining Points",//
            "Assignee",//
            "Item Notes",//
            "Needs Rerun",//
            "Aborted",//
            "Station"//
         };
      return toReturn;
   }
}
