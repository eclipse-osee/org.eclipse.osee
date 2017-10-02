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
package org.eclipse.osee.orcs.core.internal.script.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.script.ScriptContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.SelectSet;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptOutputHandler;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil.OsStorageOption;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public class ExcelOutputHandler extends OrcsScriptOutputHandler {

   private static String OUTPUT_SCRIPT = "output.script";
   private static String OUTPUT_DEBUG = "output.debug";

   private final ScriptContext context;
   private ISheetWriter writer = null;
   private List<Throwable> errors;
   private DebugInfo debugInfo;
   private boolean isDebugModeEnabled;
   private boolean isScriptOutputEnabled;
   private int queriesRun = 1;
   private int numSheets;
   private int querySheetNum;
   private final HeaderCollector headers = new HeaderCollector();
   private final List<Map<String, Object>> providedData = new LinkedList<>();

   public ExcelOutputHandler(ScriptContext context) {
      super();
      Conditions.checkNotNull(context, "Context");
      this.context = context;
   }

   private void initalizeData() {
      isScriptOutputEnabled = true;
      numSheets = -1;
      querySheetNum = -1;

      Object debug = context.getAttribute(OUTPUT_DEBUG);
      isDebugModeEnabled = Boolean.parseBoolean(String.valueOf(debug));

      Object outputScript = context.getAttribute(OUTPUT_SCRIPT);
      if (outputScript != null) {
         isScriptOutputEnabled = isDebugModeEnabled || Boolean.parseBoolean(String.valueOf(outputScript));
      }

      if (isDebugModeEnabled) {
         debugInfo = new DebugInfo();
      } else {
         debugInfo = null;
      }
      errors = null;
   }

   @Override
   public void onEvalStart() {
      super.onEvalStart();
      initalizeData();
      try {
         writer = new ExcelXmlWriter(context.getWriter());
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void onError(Throwable th) {
      if (errors == null) {
         errors = new ArrayList<>();
      }
      errors.add(th);
   }

   @Override
   public void onCompileStart(OrcsScript model) {
      super.onCompileStart(model);
      if (isScriptOutputEnabled) {
         try {
            writer.startSheet("script", 4);
            ++numSheets;
            writeScriptData(model);
            writer.endSheet();
         } catch (IOException ex) {
            throw new OseeCoreException(ex);
         }
      }
   }

   @Override
   public void onExecutionStart(String version) {
      super.onExecutionStart(version);
      if (isDebugModeEnabled) {
         debugInfo.setScriptVersion(version);
      }
   }

   @Override
   public void onQueryStart(QueryData data) {
      super.onQueryStart(data);
      if (isDebugModeEnabled) {
         debugInfo.addQuery(data);
      }
   }

   @Override
   public void onLoadStart() {
      super.onLoadStart();
      providedData.clear();
      headers.clear();
   }

   @Override
   public void onLoadDescription(LoadDescription data) {
      super.onLoadDescription(data);
      if (debugInfo != null) {
         debugInfo.addDescription(data);
      }
   }

   @Override
   public void onDynamicData(Map<String, Object> data) {
      super.onDynamicData(data);
      headers.process(data);
      providedData.add(data);
   }

   @Override
   public void onLoadEnd() {
      super.onLoadEnd();
      try {
         try {
            writeData();
         } finally {
            writer.endSheet();
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   private void writeData() {
      startSheet();
      for (Map<String, Object> item : providedData) {
         try {
            try {
               writeDataMap(item, 0);
            } finally {
               writer.endRow();
            }
         } catch (IOException ex) {
            throw new OseeCoreException(ex);
         }
      }
   }

   private void startSheet() {
      try {
         try {
            writer.startSheet("Query " + Integer.toString(queriesRun++), headers.size());
            ++numSheets;
            querySheetNum = numSheets;
            for (String s : headers.getHeaders()) {
               writer.writeCell(s);
            }
         } finally {
            writer.endRow();
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void onEvalEnd() {
      super.onEvalEnd();
      if (writer != null) {
         try {
            try {
               writeErrors();
               writeDebug();
               if (querySheetNum >= 0) {
                  writer.setActiveSheet(querySheetNum);
               }
            } finally {
               writer.endWorkbook();
            }
         } catch (IOException ex) {
            throw new OseeCoreException(ex);
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void writeDataMap(Map<String, Object> data, int level) {
      Set<String> keys = data.keySet();
      try {
         for (String key : keys) {
            Object entry = data.get(key);
            if (entry instanceof Map<?, ?>) {
               writeMapEntry((Map<String, Object>) entry, key, level);
            } else if (entry instanceof Set<?>) {
               int columnIndex = headers.getColumnIndex(level, key);
               Object setTypeCheck = ((Set<?>) entry).iterator().next();
               if (setTypeCheck instanceof Map<?, ?>) {
                  writeSetEntry((Iterable<Map<String, Object>>) entry, columnIndex);
               } else {
                  throw new OseeCoreException("unknown data type in Set output", setTypeCheck);
               }
            } else {
               int columnIndex = headers.getColumnIndex(level, key);
               writer.writeCell(entry.toString(), columnIndex);
            }
         }
         if (level == headers.columnGroups.size()) {
            writer.endRow();
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }

   }

   private String getValidContent(Object data) {
      StringBuilder toReturn = new StringBuilder();
      if (data instanceof BinaryDataProxy) {
         toReturn.append("binary data");
      } else if (data instanceof CharacterDataProxy) {
         toReturn.append(convertCharacterProxyData((CharacterDataProxy) data));
      } else {
         toReturn.append(AXml.textToXml(data.toString()));
      }
      return toReturn.toString();
   }

   private String convertCharacterProxyData(CharacterDataProxy dp) {
      String toWrite = dp.getValueAsString();
      return AXml.textToXml(toWrite);
   }

   private void writeMapEntry(Map<String, Object> data, String key, int level) throws IOException {
      if (data.size() > 1) {
         writeDataMap(data, level + 1);
      } else {
         Object value = data.values().iterator().next();
         if (value instanceof Map<?, ?>) {
            writeDataMap(data, level + 1);
         } else {
            int columnIndex = headers.getColumnIndex(level, key);
            String toWrite = getValidContent(value);
            writer.writeCell(toWrite, columnIndex);
         }
      }
   }

   private void writeSetEntry(Iterable<Map<String, Object>> data, int column) throws IOException {
      // multiple values in a single attribute - combine as one string
      // assume Set of Maps - concatenate the data
      StringBuilder combined = new StringBuilder();
      boolean first = true;
      for (Map<String, Object> content : data) {
         for (Object value : content.values()) {
            if (!first) {
               combined.append(", ");
            }
            first = false;
            combined.append(getValidContent(value));
         }
      }
      writer.writeCell(combined.toString(), column);
   }

   private void writeScriptData(OrcsScript model) throws IOException {
      Map<String, Object> binding = OrcsScriptUtil.getBinding(model);
      if (binding != null && !binding.isEmpty()) {
         writer.writeCell("parameters:");
         writer.endRow();

         for (Entry<String, Object> entry : binding.entrySet()) {
            writer.writeCell(entry.getKey());
            writer.writeCell(String.valueOf(entry.getValue()));
            writer.endRow();
         }
      }
      writer.writeCell("script:");
      writer.endRow();

      String value = "N/A";
      try {
         if (model != null && model.eAllContents().hasNext()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OrcsScriptUtil.saveModel(model, "orcs:/unknown.orcs", outputStream, OsStorageOption.NO_VALIDATION_ON_SAVE);
            value = outputStream.toString("UTF-8");
         }
      } catch (Exception ex) {
         value = ex.getMessage();
      }
      writer.writeCell(value);
      writer.endRow();
   }

   private void writeErrors() throws IOException {
      if (errors != null && !errors.isEmpty()) {
         Writer errorWriter = context.getErrorWriter();
         if (errorWriter != null && errorWriter != context.getWriter()) {
            for (Throwable th : errors) {
               errorWriter.write(Lib.exceptionToString(th));
            }
         } else {
            writer.startSheet("errors", 1);
            ++numSheets;
            writer.writeCell("errors:");
            writer.endRow();
            for (Throwable th : errors) {
               writer.writeCell(Lib.exceptionToString(th));
               writer.endRow();
            }
            writer.endSheet();
         }
      }
   }

   private void writeDebug() throws IOException {
      if (debugInfo != null) {
         List<LoadDescription> descriptions = debugInfo.getDescriptions();
         writer.startSheet("Debug", 2);
         ++numSheets;
         writer.writeCell("debug:");
         writer.endRow();

         writer.writeCell("script-version: " + debugInfo.getScriptVersion());
         writer.endRow();

         List<QueryData> queries = debugInfo.getQueries();
         for (int index = 0; index < descriptions.size(); index++) {
            writer.writeCell("query_" + index);
            writer.endRow();

            LoadDescription description = descriptions.get(index);
            OrcsSession session = description.getSession();
            if (session != null) {
               writer.writeCell("session");
               writer.writeCell(session.getGuid());
               writer.endRow();
            }
            Options options = description.getOptions();
            for (String key : new TreeSet<String>(options.getKeys())) {
               writer.writeCell(key);
               writer.writeCell(options.get(key).replaceAll("\\s+", " "));
               writer.endRow();
            }
            writeQuery(queries.get(index));
         }
         writer.endSheet();
      }
   }

   private void writeQuery(QueryData queryData) throws IOException {
      List<CriteriaSet> criteriaSets = queryData.getCriteriaSets();
      List<SelectSet> selectSets = queryData.getSelectSets();
      writer.writeCell("query:");
      writer.endRow();
      for (int index = 0; index < criteriaSets.size(); index++) {
         writer.writeCell("level");
         writer.writeCell(index);
         writer.endRow();
         writeCriterias(criteriaSets.get(index));
         writeCollect(selectSets.get(index));
      }
      writer.endRow();
   }

   private void writeCriterias(CriteriaSet criteriaSet) throws IOException {
      writer.writeCell("criteria:");
      if (criteriaSet.getCriterias().size() == 0) {
         writer.writeCell("none");
         writer.endRow();
      } else {
         writer.endRow();
         for (Criteria criteria : criteriaSet.getCriterias()) {
            String value = criteria.toString();
            writer.writeCell(value);
            writer.endRow();
         }
      }
   }

   private void writeCollect(SelectSet selectSet) throws IOException {
      writer.writeCell("collect:");
      writer.endRow();
      writer.writeCell("limit:");
      writer.writeCell(selectSet.getLimit());
      writer.endRow();
      writeDynamicData(selectSet.getData());
   }

   private void writeDynamicData(DynamicData data) throws IOException {
      if (data != null) {
         if (data instanceof DynamicObject) {
            DynamicObject obj = (DynamicObject) data;
            if (obj.hasChildren()) {
               writer.writeCell(data.getName());
               writer.endRow();
               for (DynamicData child : obj.getChildren()) {
                  writeDynamicData(child);
               }
            }
         } else {
            StringBuilder value = new StringBuilder();
            if (data.isHidden()) {
               value.append("hidden->");
            }
            value.append(data.getGuid());
            if (data.isPrimaryKey()) {
               value.append("*");
            }
            writer.writeCell(data.getName() + ":");
            writer.writeCell(value.toString());
            writer.endRow();
         }

      }
   }

   private static final class DebugInfo {
      public String scriptVersion;
      public final List<LoadDescription> descriptions = new ArrayList<>();
      private final List<QueryData> queries = new ArrayList<>();

      public void setScriptVersion(String version) {
         this.scriptVersion = version;
      }

      public void addDescription(LoadDescription data) {
         descriptions.add(data);
      }

      public void addQuery(QueryData queryData) {
         queries.add(queryData);
      }

      public String getScriptVersion() {
         return scriptVersion;
      }

      public List<LoadDescription> getDescriptions() {
         return descriptions;
      }

      public List<QueryData> getQueries() {
         return queries;
      }
   }

   private static final class HeaderCollector {

      private final List<List<String>> columnGroups = new ArrayList<>();
      private volatile int totalSize = -1;

      public void clear() {
         columnGroups.clear();
         totalSize = -1;
      }

      public void process(Map<String, Object> data) {
         processHelper(data, 0);
         totalSize = -1;
      }

      @SuppressWarnings("unchecked")
      private void processHelper(Map<String, Object> data, int index) {
         List<String> columns;
         if (index >= columnGroups.size()) {
            columns = new ArrayList<>(data.keySet());
            columnGroups.add(columns);
         } else {
            columns = columnGroups.get(index);

            for (String attributes : data.keySet()) {
               if (!columns.contains(attributes)) {
                  columns.add(attributes);
               }
            }
         }

         for (Object item : data.values()) {
            if (item instanceof Map<?, ?>) {
               Map<String, Object> child = (Map<String, Object>) item;
               if (child.size() > 1) {
                  processHelper(child, index + 1);
               } else if (child.values().iterator().next() instanceof Map<?, ?>) {
                  processHelper(child, index + 1);
               }
            }
         }
      }

      public int size() {
         if (totalSize < 0) {
            totalSize = 0;
            for (Collection<String> attributes : columnGroups) {
               totalSize += attributes.size();
            }
         }
         return totalSize;
      }

      public Iterable<String> getHeaders() {
         List<String> headings = new ArrayList<>();
         for (Collection<String> attributes : columnGroups) {
            for (String value : attributes) {
               headings.add(value);
            }
         }
         return headings;
      }

      public int getColumnIndex(int index, String key) {
         int position = 0;
         try {
            for (int i = 0; i < index; ++i) {
               position += columnGroups.get(i).size();
            }
            List<String> columns = columnGroups.get(index);
            position += columns.indexOf(key);
         } catch (Exception ex) {
            throw new OseeCoreException("Invalid index: %d getting column heading position for %s", index, key);
         }
         return position;
      }
   }
}
