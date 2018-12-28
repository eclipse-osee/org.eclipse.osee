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

import com.google.common.collect.Iterables;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import javax.script.ScriptContext;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BinaryDataProxy;
import org.eclipse.osee.orcs.core.ds.CharacterDataProxy;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.DataProxy;
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
 */
public class JsonOutputHandler extends OrcsScriptOutputHandler {

   private static String OUTPUT_SCRIPT = "output.script";
   private static String OUTPUT_DEBUG = "output.debug";

   private final ScriptContext context;

   private JsonGenerator writer;
   private List<Throwable> errors;
   private DebugInfo debugInfo;
   private boolean isDebugModeEnabled;
   private boolean isScriptOutputEnabled;
   JsonOutputMath mathOut;

   public JsonOutputHandler(ScriptContext context) {
      super();
      this.context = context;
   }

   private void initalizeData() {
      isScriptOutputEnabled = true;
      mathOut = new JsonOutputMath();
      mathOut.initialize(context);
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
         ObjectMapper mapper = new ObjectMapper();
         mapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a"));
         mapper.configure(SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true);
         mapper.configure(SerializationConfig.Feature.WRAP_EXCEPTIONS, true);
         mapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
         mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);

         SimpleModule module = new SimpleModule("DataProxy", new Version(1, 0, 0, null));
         module.addSerializer(DataProxy.class, new DataProxySerializer(DataProxy.class));
         mapper.registerModule(module);

         JsonFactory jsonFactory = mapper.getJsonFactory();
         writer = jsonFactory.createJsonGenerator(context.getWriter());
         writer.setPrettyPrinter(new DefaultPrettyPrinter());

         writer.writeStartObject();
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
            writeScriptData(model);
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
   public void onQueryEnd() {
      super.onQueryEnd();
   }

   @Override
   public void onLoadStart() {
      super.onLoadStart();
      try {
         writer.writeArrayFieldStart("results");
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   private DynamicData first;
   private boolean wasStarted;

   @Override
   public void onLoadDescription(LoadDescription data) {
      super.onLoadDescription(data);
      first = Iterables.getFirst(data.getObjectDescription().getDynamicData(), null);
      wasStarted = false;
      if (debugInfo != null) {
         debugInfo.addDescription(data);
      }
   }

   @Override
   public void onDynamicData(Map<String, Object> data) {
      super.onDynamicData(data);
      try {
         if (!wasStarted && first != null) {
            wasStarted = true;
            writer.writeStartObject();
            writer.writeArrayFieldStart(first.getName());
         }
         if (mathOut.isUsed()) {
            mathOut.add(data);
         }
         writer.writeObject(data);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void onLoadEnd() {
      super.onLoadEnd();
      try {
         if (wasStarted) {
            writer.writeEndArray();
            writer.writeEndObject();
         }
         if (mathOut.isUsed()) {
            mathOut.write(writer);
         }
         writer.writeEndArray();
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   @Override
   public void onExecutionEnd() {
      super.onExecutionEnd();
   }

   @Override
   public void onEvalEnd() {
      super.onEvalEnd();
      try {
         try {
            writeErrors();
            writeDebug();
         } finally {
            writer.writeEndObject();
            writer.flush();
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   private void writeScriptData(OrcsScript model) throws IOException {
      Map<String, Object> binding = OrcsScriptUtil.getBinding(model);
      if (binding != null && !binding.isEmpty()) {
         writer.writeFieldName("parameters");
         writer.writeStartObject();
         for (Entry<String, Object> entry : binding.entrySet()) {
            writer.writeStringField(entry.getKey(), String.valueOf(entry.getValue()));
         }
         writer.writeEndObject();
      }
      writer.writeFieldName("script");
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
      writer.writeString(value);
   }

   private void writeErrors() throws IOException {
      if (errors != null && !errors.isEmpty()) {
         Writer errorWriter = context.getErrorWriter();
         if (errorWriter != null && errorWriter != context.getWriter()) {
            for (Throwable th : errors) {
               errorWriter.write(Lib.exceptionToString(th));
            }
         } else {
            writer.writeArrayFieldStart("errors");
            for (Throwable th : errors) {
               writer.writeString(Lib.exceptionToString(th));
            }
            writer.writeEndArray();
         }
      }
   }

   private void writeDebug() throws JsonGenerationException, IOException {
      if (debugInfo != null) {
         writer.writeFieldName("debug");
         writer.writeStartObject();

         writer.writeStringField("script-version", debugInfo.getScriptVersion());

         List<LoadDescription> descriptions = debugInfo.getDescriptions();
         List<QueryData> queries = debugInfo.getQueries();
         for (int index = 0; index < descriptions.size(); index++) {
            writer.writeFieldName("query_" + index);
            writer.writeStartObject();

            LoadDescription description = descriptions.get(index);
            OrcsSession session = description.getSession();
            if (session != null) {
               writer.writeStringField("session", session.getGuid());
            }
            Options options = description.getOptions();
            for (String key : new TreeSet<>(options.getKeys())) {
               writer.writeStringField(key, options.get(key).replaceAll("\\s+", " "));
            }
            writeQuery(queries.get(index));
            writer.writeEndObject();
         }
         writer.writeEndObject();
      }
   }

   private void writeQuery(QueryData queryData) throws IOException {
      List<List<Criteria>> criteriaSets = queryData.getCriteriaSets();
      List<SelectSet> selectSets = queryData.getSelectSets();
      writer.writeArrayFieldStart("query");
      for (int index = 0; index < criteriaSets.size(); index++) {
         writer.writeStartObject();
         writer.writeNumberField("level", index);
         writeCriterias(criteriaSets.get(index));
         writeCollect(selectSets.get(index));
         writer.writeEndObject();
      }
      writer.writeEndArray();
   }

   private void writeCriterias(List<Criteria> criteriaSet) throws IOException {
      writer.writeArrayFieldStart("criteria");
      for (Criteria criteria : criteriaSet) {
         String value = criteria.toString();
         value = value.replaceAll("\\[", "");
         value = value.replaceAll("\\]", "");
         writer.writeObject(value);
      }
      writer.writeEndArray();
   }

   private void writeCollect(SelectSet selectSet) throws IOException {
      writer.writeFieldName("collect");
      writer.writeStartObject();
      writer.writeNumberField("limit", selectSet.getLimit());
      writeDynamicData(selectSet.getData());
      writer.writeEndObject();
   }

   private void writeDynamicData(DynamicData data) throws JsonGenerationException, IOException {
      if (data != null) {
         if (data instanceof DynamicObject) {
            DynamicObject obj = (DynamicObject) data;
            if (obj.hasChildren()) {
               writer.writeFieldName(data.getName());
               writer.writeStartObject();
               for (DynamicData child : obj.getChildren()) {
                  writeDynamicData(child);
               }
               writer.writeEndObject();
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
            writer.writeStringField(data.getName(), value.toString());
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

   private static final class DataProxySerializer extends org.codehaus.jackson.map.ser.std.SerializerBase<DataProxy> {

      protected DataProxySerializer(Class<DataProxy> t) {
         super(t);
      }

      @Override
      public void serialize(DataProxy proxy, JsonGenerator writer, SerializerProvider provider) throws IOException, JsonGenerationException {
         if (proxy instanceof CharacterDataProxy) {
            CharacterDataProxy characters = (CharacterDataProxy) proxy;
            writer.writeObject(characters.getValueAsString());
         } else if (proxy instanceof BinaryDataProxy) {
            BinaryDataProxy binary = (BinaryDataProxy) proxy;
            ByteBuffer buffer = binary.getValueAsBytes();
            InputStream inputStream = null;
            try {
               inputStream = Lib.byteBufferToInputStream(buffer);
               writer.writeBinary(Lib.inputStreamToBytes(inputStream));
            } finally {
               Lib.close(inputStream);
            }
         }
      }
   }
}