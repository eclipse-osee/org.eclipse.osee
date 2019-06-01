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
package org.eclipse.osee.define.rest.importing.parsers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import org.eclipse.define.api.importing.RoughArtifactCollector;
import org.eclipse.osee.define.rest.importing.parsers.DoorsTableRow.RowType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/**
 * @author David W. Miller
 */
public class DoorsTableRowCollector {
   private final DoorsArtifactExtractor extractor;
   private final ArrayList<DoorsTableRow> rawRows = Lists.newArrayList();
   private final HashMap<DoorsColumnType, Integer> columns = Maps.newHashMap();
   private final Stack<DoorsTableRow> stack = new Stack<>();
   private final static String UNKNOWN_STRING = "unknown";

   public DoorsTableRowCollector(DoorsArtifactExtractor extractor) {
      this.extractor = extractor;
   }

   public void addRawRow(Node row) {
      DoorsTableRow newRow = new DoorsTableRow();
      newRow.fill(row);
      // if it is the first row, initialize the column map
      if (rawRows.size() == 0) {
         initColumns(newRow);
      }
      rawRows.add(newRow);
   }

   public Collection<DoorsTableRow> getTableRows() {
      return rawRows;
   }

   public DoorsTableRow getTableRow(int index) {
      return rawRows.get(index);
   }

   private void initColumns(DoorsTableRow first) {
      int i = 0;
      for (Element colHeadElement : first.getRows()) {
         if (!"th".equals(colHeadElement.tagName())) {
            throw new OseeArgumentException("must be a table heading", colHeadElement);
         }
         String value = colHeadElement.ownText();
         DoorsColumnType dcte = DoorsColumnType.fromString(value);
         columns.put(dcte, i);
         ++i;
      }
   }

   public void createArtifacts(OrcsApi orcsApi, RoughArtifactCollector collector) {
      analyzeRows();
      Queue<DoorsTableRow> queue = new LinkedList<>();
      DoorsArtifactBuilder dab =
         new DoorsArtifactBuilder(orcsApi, collector.getParentRoughArtifact().getResults(), extractor, this);

      for (DoorsTableRow dr : getTableRows()) {
         switch (dr.getType()) {
            case SINGLE:
               collector.addRoughArtifact(dab.populateArtifact(dr));
               break;
            case MULTI_START:
               queue.clear();
               queue.add(dr);
               break;
            case MULTI_MID:
               queue.add(dr);
               break;
            case MULTI_END:
               queue.add(dr);
               collector.addRoughArtifact(dab.populateArtifact(queue));
               break;
            case FIRST_ROW:
               break;
            default:
               throw new OseeStateException("Unhandled enumeration", dr);
         }
      }
   }

   private DoorsDataType getDataTypeValue(DoorsTableRow dr) {
      int index = columns.get(DoorsColumnType.DATA_TYPE);
      String s = dr.getElement(index).ownText();
      return DoorsDataType.fromString(s);
   }

   public String getPreferredName(DoorsTableRow dr) {
      Conditions.checkNotNull(dr, "Doors Table Row");
      int index = getColumns().get(DoorsColumnType.PARAGRAPH_HEADING);
      String toReturn = dr.getElement(index).ownText();
      if (toReturn.isEmpty()) {
         int secondary = columns.get(DoorsColumnType.ID);
         toReturn = dr.getElement(secondary).ownText();
         if (toReturn.isEmpty()) {
            toReturn = UNKNOWN_STRING;
         }
      }
      return toReturn;
   }

   // use when the contents of the column are simple text
   public String getSimpleText(DoorsTableRow dr, DoorsColumnType dte) {
      Conditions.checkNotNull(dr, "Doors Table Row");
      Element e = dr.getElement(getColumns().get(dte));
      if (e == null) {
         throw new OseeStateException("Unknown Column type in Doors Table Row", dr, dte);
      }
      return e.ownText();
   }

   public String getHTML(DoorsTableRow dr, DoorsColumnType dte) {
      Conditions.checkNotNull(dr, "Doors Table Row");
      Element e = dr.getElement(getColumns().get(dte));
      if (e == null) {
         throw new OseeStateException("Unknown Column type in Doors Table Row", dr, dte);
      }
      return e.html();
   }

   private void analyzeRows() {
      // initial marking for the raw html rows to show whether they are stand alone or not
      if (rawRows.size() < 2) {
         throw new OseeStateException("Not enough rows to analyze", rawRows);
      }
      rawRows.get(0).setRowType(RowType.FIRST_ROW);
      for (int i = 1; i < rawRows.size(); i++) {
         DoorsTableRow current = rawRows.get(i);
         DoorsDataType dte = getDataTypeValue(current);
         current.setDataType(dte);
         if (dte.isSingle()) {
            setStack(current, RowType.SINGLE);
         } else {
            setStack(current, RowType.MULTI_START);
         }
      }
      cleanupStack();
   }

   private DoorsTableRow getFirstRow() {
      DoorsTableRow toReturn = null;
      if (rawRows.size() > 0) {
         toReturn = rawRows.get(0);
      }
      return toReturn;
   }

   private void setStack(DoorsTableRow current, RowType rt) {
      if (stack.empty()) {
         stack.push(current);
         //gets it started. If rt is multi it is an error (partial requirement in file)
      }
      RowType stackTop = stack.peek().getType();
      switch (stackTop) {
         case SINGLE:
            handleSingle(current, rt);
            break;
         case MULTI_START:
         case MULTI_MID:
            handleMulti(current, rt);
            break;
         case MULTI_END:
            break;
         case FIRST_ROW:
            break;
         default:
            throw new OseeStateException("Unhandled enumeration", current);
      }
   }

   private void handleSingle(DoorsTableRow current, RowType rt) {
      switch (rt) {
         case SINGLE:
            stack.pop();
            stack.push(current);
            break;
         case MULTI_START:
            stack.peek().setRowType(RowType.MULTI_START);
            current.setRowType(RowType.MULTI_MID);
            stack.push(current);
            break;
         case MULTI_MID:
         case MULTI_END:
            break;
         case FIRST_ROW:
            break;
         default:
            throw new OseeStateException("Unhandled enumeration", current);
      }
   }

   private void handleMulti(DoorsTableRow current, RowType rt) {
      switch (rt) {
         case SINGLE:
            stack.peek().setRowType(RowType.MULTI_END);
            stack.clear();
            stack.push(current);
            break;
         case MULTI_START:
            current.setRowType(RowType.MULTI_MID);
            stack.push(current);
            break;
         case MULTI_MID:
         case MULTI_END:
            break;
         case FIRST_ROW:
            break;
         default:
            throw new OseeStateException("Unhandled enumeration", current);
      }
   }

   private void cleanupStack() {
      if (!stack.empty()) {
         RowType stackTop = stack.peek().getType();
         switch (stackTop) {
            case SINGLE:
               break;
            case MULTI_START:
               stack.peek().setRowType(RowType.SINGLE);
               break;
            case MULTI_MID:
               stack.peek().setRowType(RowType.MULTI_END);
               break;
            case MULTI_END:
               break;
            case FIRST_ROW:
               break;
            default:
               throw new OseeStateException("Unhandled enumeration", stackTop);
         }
         // all of the other items in the stack already have the right settings
         stack.clear();
      }
   }

   private HashMap<DoorsColumnType, Integer> getColumns() {
      if (columns.isEmpty()) {
         throw new OseeStateException("Doors Table Row Collector is empty", this);
      }
      return columns;
   }

   // currently used only for debugging purposes
   // this makes it simple to check the contents of the import using a diff
   @SuppressWarnings("unused")
   private void outputHTML(String file) throws FileNotFoundException, UnsupportedEncodingException {
      String outputfile = String.format("%s%s.html", file, Lib.getDateTimeString());
      PrintWriter writer = new PrintWriter(outputfile, "UTF-8");
      try {
         outputHTML(writer);
      } finally {
         writer.close();
      }

   }

   public void outputHTML(PrintWriter writer) {
      writer.println(
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      writer.println("<html>");
      writer.println("<head>");
      writer.println("<title> Testing reading requirements and outputting them </title>");
      writer.println("</head>");
      writer.println(
         "<body  bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#EE0000\" alink=\"#808080\" vlink=\"#808080\">");
      writer.println("<table border=\"\">");
      writer.println("<tbody>");

      for (DoorsTableRow dr : rawRows) {
         writer.println("<tr>");
         for (Element e : dr.getRows()) {
            writer.println(e.toString());
         }
         if (dr == getFirstRow()) {
            writer.println("<th width=\"100\" align=\"Left\">Combination</th>");
         } else {
            writer.println(String.format("<td>%s</td>", dr.getType().toString()));
         }
         writer.println("</tr>");
      }
      writer.println("</tbody>");
      writer.println("</table>");
      writer.println("</body>");
      writer.println("</html>");
   }
}
