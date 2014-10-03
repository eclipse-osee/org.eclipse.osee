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

import static org.eclipse.osee.orcs.core.internal.script.OrcsScriptException.newException;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.Bindings;
import javax.script.ScriptContext;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptCompiler;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptException;
import com.google.common.io.CharStreams;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptCompilerImpl implements OrcsScriptCompiler {

   private final OrcsSession session;
   private final DataModule dataModule;
   private final OrcsTypes orcsTypes;

   public OrcsScriptCompilerImpl(OrcsSession session, DataModule dataModule, OrcsTypes orcsTypes) {
      super();
      this.session = session;
      this.dataModule = dataModule;
      this.orcsTypes = orcsTypes;
   }

   @Override
   public OrcsCompiledScript compileReader(Reader reader, String filename) throws OrcsScriptException {
      String script = parse(reader, filename);
      return new OrcsCompiledScriptImpl(script);
   }

   private String parse(Reader reader, String filename) throws OrcsScriptException {
      String script = "";
      try {
         script = CharStreams.toString(reader);
      } catch (IOException ex) {
         throw newException(ex);
      }
      return script;
   }

   private class OrcsCompiledScriptImpl implements OrcsCompiledScript {

      private final String script;

      public OrcsCompiledScriptImpl(String script) {
         super();
         this.script = script;
      }

      @Override
      public Object eval(ScriptContext context) throws OrcsScriptException {
         Map<String, Object> binding = new LinkedHashMap<String, Object>();
         List<Integer> scopes = context.getScopes();
         for (Integer scope : scopes) {
            Bindings bindings = context.getBindings(scope);
            if (bindings != null) {
               for (Entry<String, Object> entry : bindings.entrySet()) {
                  binding.put(entry.getKey(), entry.getValue());
               }
            }
         }

         JsonGenerator writer = null;
         try {
            JsonFactory jsonFactory = new JsonFactory();
            writer = jsonFactory.createJsonGenerator(context.getWriter());
            writer.writeStartObject();
            writeScriptData(writer, binding);
            writer.writeStringField("results", "Not Yet Implemented");
            writer.writeEndObject();
            return Boolean.TRUE;
         } catch (IOException ex) {
            throw newException(ex);
         } finally {
            try {
               if (writer != null) {
                  writer.flush();
               }
            } catch (IOException ex) {
               throw newException(ex);
            }
         }
      }

      private void writeScriptData(JsonGenerator writer, Map<String, Object> binding) throws IOException {
         if (binding != null && !binding.isEmpty()) {
            writer.writeFieldName("parameters");
            writer.writeStartObject();
            for (Entry<String, Object> entry : binding.entrySet()) {
               writer.writeStringField(entry.getKey(), String.valueOf(entry.getValue()));
            }
            writer.writeEndObject();
         }
         writer.writeStringField("script", script);
      }
   }

}
