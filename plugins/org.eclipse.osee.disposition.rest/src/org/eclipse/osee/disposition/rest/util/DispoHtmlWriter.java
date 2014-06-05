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
package org.eclipse.osee.disposition.rest.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.LocationRangesCompressor;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.template.engine.AppendableRule;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;
import org.eclipse.osee.template.engine.StringRule;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */

public class DispoHtmlWriter {
// @formatter:off
   private final String subTableHeadersStart = "   <tr>"+
                                            "<th class=\"spacer\" rowspan=\"";
      
   private final String subTableHeadersEnd ="\" width=\"30\"></th>"+
                                            "<th width=\"325\">Points</th>"+
                                            "<th width=\"75\">Type</th>"+
                                            "<th width=\"150\">PCR</th>"+
                                            "<th width=\"500\">Developer Notes</th>"+
                                            "<th width=\"500\">Customer Notes</th>"+
                                            "<th width=\"40\">Delete</th>" + 
                                        "</tr>";
// @formatter:on

   private final IResourceRegistry registry;

   public DispoHtmlWriter(IResourceRegistry registry) {
      this.registry = registry;
   }

   public String createDispositionPage(String title, Iterable<? extends DispoItem> items) {
      PageCreator page = PageFactory.newPageCreator(registry, "title", title);
      page.addSubstitution(new StringRule("notes", ""));
      page.addSubstitution(new TableRowRule("tableData", items));
      return page.realizePage(TemplateRegistry.DispositionHtml);
   }

   public String createMainTable(String url) {
      PageCreator page = PageFactory.newPageCreator(registry);
      page.addSubstitution(new StringRule("serverPort", url));
      return page.realizePage(TemplateRegistry.DispositionUserHtml);
   }

   public String createAdminTable(String url) {
      PageCreator page = PageFactory.newPageCreator(registry);
      page.addSubstitution(new StringRule("serverPort", url));
      return page.realizePage(TemplateRegistry.DispositionAdminHtml);
   }

   public String createAllSetsTableHTML(Iterable<DispoSet> sets) {
      int count = 0;
      StringBuilder sb = new StringBuilder();
      for (DispoSet set : sets) {
         sb.append("<tr id=\"");
         sb.append(set.getGuid());
         sb.append("\">");

         sb.append("<td class=\"setsTableData\">");
         sb.append("<input class=\"setsTableDataInput\" onclick=\"setFocused(this);\" type=\"text\" readonly value=\"");
         sb.append(set.getName());
         sb.append("\">");
         sb.append("</input>");
         sb.append("</td>");

         sb.append("<td class=\"setsTableData\">");
         sb.append("no summary available");
         sb.append("</td>");
         sb.append("</tr>");
         count++;
      }

      // We want a table of 7 rows, fill in empty rows (if any) with fillers

      for (int i = 0; i < (7 - count); i++) {
         sb.append("<tr>");
         sb.append("<td class=\"setsTableData\">");
         sb.append("</td>");
         sb.append("<td class=\"setsTableData\">");
         sb.append("</td>");
         sb.append("</tr>");
      }

      return sb.toString();
   }

   public String createSelectPrograms(Iterable<IOseeBranch> allPrograms) {
      StringBuilder sb = new StringBuilder();
      sb.append("<option selected disabled>Choose One</option>");
      for (IOseeBranch option : allPrograms) {
         sb.append("<option value=\"");
         sb.append(option.getUuid());
         sb.append("\">");
         sb.append(option.getName());
         sb.append("</option>");
      }

      return sb.toString();
   }

   public String createSelectSet(Iterable<DispoSet> allSets) {
      StringBuilder sb = new StringBuilder();
      sb.append("<option selected disabled>Choose One</option>");
      for (DispoSet option : allSets) {
         sb.append("<option value=\"");
         sb.append(option.getGuid());
         sb.append("\">");
         sb.append(option.getName());
         sb.append("</option>");
      }

      return sb.toString();
   }

   public String createSetTable(Iterable<DispoItem> dispoItems) throws IOException, JSONException {
      StringBuilder writer = new StringBuilder();
      for (DispoItem item : dispoItems) {
         writer.append("<tr id=\"");
         writer.append(item.getGuid());
         if (item.getStatus().equals(DispoStrings.Item_Pass)) {
            writer.append("\" class=\"itemRow\">");
         } else {
            writer.append("\" class=\"itemRow\" ondblclick=\"showAnnotations(this)\">");
         }
         addItemData(writer, item);
         writer.append("</tr>");

         writer.append("\n");
      }

      return writer.toString();

   }

   private void addItemData(Appendable appendable, DispoItem item) throws IOException, JSONException {
      addData(appendable, item.getName());
      addData(appendable, item.getStatus(), true);
      addData(appendable, String.valueOf(item.getTotalPoints())); // change to total points
      addData(appendable, String.valueOf(item.getDiscrepanciesList().length()));
      addData(appendable, String.valueOf(getFailureLocations(item.getDiscrepanciesList())));
      addData(appendable, item.getAssignee());
      addData(appendable, item.getVersion()); // Change to Version
   }

   private void addData(Appendable appendable, String data) throws IOException {
      appendable.append("<td class=\"itemData\">");
      appendable.append(data);
      appendable.append("</td>");
   }

   private void addData(Appendable appendable, String data, boolean isStatus) throws IOException {
      if (data.equals(DispoStrings.Item_Complete)) {
         appendable.append("<td class=\"itemData\">");
      } else if (data.equals(DispoStrings.Item_Pass)) {
         appendable.append("<td class=\"itemDataPass\">");
      } else {
         appendable.append("<td class=\"itemDataFail\">");
      }
      appendable.append(data);
      appendable.append("</td>");
   }

   private String getFailureLocations(JSONObject discrepanciesList) throws JSONException {
      List<Integer> locations = new ArrayList<Integer>();
      @SuppressWarnings("unchecked")
      Iterator<String> iterator = discrepanciesList.keys();
      while (iterator.hasNext()) {
         String key = iterator.next();
         JSONObject discrepancyAsJson = discrepanciesList.getJSONObject(key);
         Discrepancy discrepany = DispoUtil.jsonObjToDiscrepancy(discrepancyAsJson);

         locations.add(discrepany.getLocation());
      }

      return LocationRangesCompressor.compress(locations);
   }

   private void addSubTableData(Appendable appendable, String data, boolean isValid) throws IOException {
      appendable.append("<td class=\"annotationData\" >");
      if (isValid) {
         appendable.append("<textarea class=\"annotationInput");
      } else {
         appendable.append("<textarea class=\"annotationInputInvalid");
      }
      appendable.append("\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\">");
      appendable.append(data);
      appendable.append("</textarea></td>");
   }

   private void addSubTableDataDropDown(Appendable appendable, String data, boolean isValid) throws IOException {
      appendable.append("<td class=\"annotationData\" >");
      if (isValid) {
         appendable.append("<select class=\"annotationInput");
      } else {
         appendable.append("<select class=\"annotationInputInvalid");
      }
      appendable.append("\" onchange=\"submitAnnotationData(this);\">");

      addOptionToSelect(appendable, data, "None", true);
      addOptionToSelect(appendable, data, "Code", false);
      addOptionToSelect(appendable, data, "Test", false);
      addOptionToSelect(appendable, data, "Requirement", false);
      addOptionToSelect(appendable, data, "Other", false);
      addOptionToSelect(appendable, data, "Undetermined", false);

      appendable.append(data);
      appendable.append("</select></td>");
   }

   private void addOptionToSelect(Appendable appendable, String data, String optionValue, boolean isDefault) throws IOException {
      appendable.append("<option value=\"");
      appendable.append(optionValue);
      appendable.append("\"");
      if (data.equalsIgnoreCase(optionValue)) {
         appendable.append(" selected");
      }
      if (isDefault) {
         appendable.append(" disabled");
      }
      appendable.append(">");
      appendable.append(optionValue);
      appendable.append("</option>");
   }

// @formatter:off
   public String createSubTable(List<DispoAnnotationData> annotations) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append("<td colspan=\"9\">");
      sb.append("<table class=\"table subTable\">");
      sb.append(createHeadersForSubTable(annotations.size()));
      for(DispoAnnotationData annotation :annotations) {
         sb.append("<tr id=\"");
         sb.append(annotation.getId());
         sb.append("\">");
         boolean isResolutionValid = true;
         if(!annotation.getResolution().isEmpty() && !annotation.getIsResolutionValid()){
            isResolutionValid = false;
         }
         boolean isResolutionTypeValid = true;
         if(!annotation.isResolutionTypeValid()){
            isResolutionTypeValid = false;
         }
         addSubTableData(sb, annotation.getLocationRefs(), annotation.getIsConnected());
         addSubTableDataDropDown(sb, annotation.getResolutionType(), isResolutionTypeValid);
         addSubTableData(sb, annotation.getResolution(), isResolutionValid);
         addSubTableData(sb, annotation.getDeveloperNotes(), true);
         addSubTableData(sb, annotation.getCustomerNotes(), true);
         addDeleteButton(sb);
         sb.append("</tr>");
      }
      // add on empty row
      sb.append("<tr>");
      sb.append("<td class=\"annotationData\"><textarea class=\"annotationInput\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\"></textarea></d>");
      sb.append("<td class=\"annotationData\"><select class=\"annotationInputDisabled\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\" disabled>");
      sb.append("<option value=\"None\" selected>None</option>");
      sb.append("<option value=\"Code\">Code</option><option value=\"Test\">Test</option>");
      sb.append("<option value=\"Requirement\">Requirement</option><option value=\"Other\">Other</option>");
      sb.append("<option value=\"Undetermined\">Undetermined</option>");
      sb.append("</select></td>");
      sb.append("<td class=\"annotationData\"><textarea class=\"annotationInputDisabled\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\" readonly=\"true\"></textarea></td>");
      sb.append("<td class=\"annotationData\"><textarea class=\"annotationInputDisabled\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\" readonly=\"true\"></textarea></td>");
      sb.append("<td class=\"annotationData\"><textarea class=\"annotationInputDisabled\" onchange=\"submitAnnotationData(this);\" ondblclick=\"annotationDblClick(this);\" readonly=\"true\"></textarea></td>");
      sb.append("<td><button class=\"annotationDelete\" onclick=\"deleteAnnotation(this);\" disabled=\"true\">X</button></td>");
      sb.append("</tr>");
      sb.append("</table>");
      sb.append("</td>");
      return sb.toString();
   }

   private void addDeleteButton(Appendable appendable) throws IOException {
      appendable.append("<td><button class=\"annotationDelete\" onclick=\"deleteAnnotation(this);\">X</button></td>");
   }
   
   private String createHeadersForSubTable(int numberOfAnnotations) {
      return subTableHeadersStart + (numberOfAnnotations+2) + subTableHeadersEnd;
   }
   private static final class TableRowRule extends AppendableRule<Object> {
      private final Iterable<? extends DispoItem> items;

      public TableRowRule(String ruleName, Iterable<? extends DispoItem> items) {
         super(ruleName);
         this.items = items;
      }

      @Override
      public void applyTo(Appendable appendable) throws IOException {
         for (DispoItem item : items) {
            appendable.append("<tr>");
            addItemData(appendable, item);

            appendable.append("<?subTable?>");
         }
      }

      private void addItemData(Appendable appendable, DispoItem item) throws IOException {
         addData(appendable, item.getName());
         addData(appendable, item.getStatus());
         addData(appendable, String.valueOf(item.getDiscrepanciesList().length())); // Change to TOtal Points
         addData(appendable, String.valueOf(item.getDiscrepanciesList().length()));
         addData(appendable, item.getAssignee());
         addData(appendable, item.getGuid()); // Change to Version
      }

      private void addData(Appendable appendable, String data) throws IOException {
         appendable.append("<td>");
         appendable.append(data);
         appendable.append("</td>");
      }
   }
}
