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
import com.google.common.io.CharStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.Bindings;
import javax.script.ScriptContext;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DataModule;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptAssembler;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptCompiler;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptException;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptExecutor;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptInterpreter;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptOutputHandler;
import org.eclipse.osee.orcs.script.dsl.IExpressionResolver;
import org.eclipse.osee.orcs.script.dsl.IFieldResolver;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslResource;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptCompilerImpl implements OrcsScriptCompiler {
   private final OrcsSession session;
   private final DataModule dataModule;
   private final OrcsTypes orcsTypes;

   private OrcsScriptInterpreter interpreter;

   public OrcsScriptCompilerImpl(OrcsSession session, DataModule dataModule, OrcsTypes orcsTypes) {
      super();
      this.session = session;
      this.dataModule = dataModule;
      this.orcsTypes = orcsTypes;
   }

   private OrcsScriptInterpreter getInterpreter() {
      if (interpreter == null) {
         IExpressionResolver resolver = OrcsScriptUtil.getExpressionResolver();
         IFieldResolver fieldResolver = OrcsScriptUtil.getFieldResolver();
         interpreter = new OrcsScriptInterpreterImpl(orcsTypes, resolver, fieldResolver);
      }
      return interpreter;
   }

   private OrcsScriptOutputHandler getOutputHandler(ScriptContext context) {
      OrcsScriptOutputHandler handler = null;
      String output = (String) context.getAttribute("output.mime-type");
      if (Strings.isValid(output) && "excel-xml".equalsIgnoreCase(output)) {
         handler = new ExcelOutputHandler(context);
      } else {
         handler = new JsonOutputHandler(context);
      }
      return handler;
   }

   private OrcsScriptAssembler getAssembler(OrcsScriptOutputHandler output) {
      return new OrcsScriptAssemblerImpl(dataModule, orcsTypes, output);
   }

   private OrcsScriptExecutor getExecutor(OrcsScriptAssembler assembler) {
      return (OrcsScriptExecutor) assembler;
   }

   @Override
   public OrcsCompiledScript compileReader(Reader reader, String filename) throws OrcsScriptException {
      OrcsScript model = parse(reader, filename);
      return new OrcsCompiledScriptImpl(model);
   }

   private OrcsScript parse(Reader reader, String filename) throws OrcsScriptException {
      OrcsScriptDslResource resource = null;
      InputStream inputStream = null;
      try {
         String uri = "orcs:/dummy.orcs";
         inputStream = new ByteArrayInputStream(read(reader).getBytes(Strings.UTF_8));
         resource = OrcsScriptUtil.loadModelSafely(inputStream, uri);
      } catch (IOException ex) {
         throw newException(ex);
      } finally {
         Lib.close(inputStream);
      }
      if (resource.hasErrors()) {
         throw newException(filename, resource.getErrors());
      }
      return resource.getModel();
   }

   private String read(Reader reader) throws IOException {
      String script = CharStreams.toString(reader);
      if (Strings.isValid(script)) {
         script = script.trim();
         if (!script.endsWith(";")) {
            script = script + ';';
         }
      }
      return script;
   }

   private final class OrcsCompiledScriptImpl implements OrcsCompiledScript {

      private final OrcsScript model;

      public OrcsCompiledScriptImpl(OrcsScript model) {
         super();
         this.model = model;
      }

      @Override
      public Object eval(ScriptContext context) throws OrcsScriptException {
         OrcsScriptOutputHandler output = getOutputHandler(context);
         try {
            output.onEvalStart();
            Map<String, Object> parameters = asMap(context);

            OrcsScriptAssembler assembler = getAssembler(output);
            synchronized (model) {
               try {
                  OrcsScriptUtil.bind(model, parameters);
                  OrcsScriptInterpreter interpreter = getInterpreter();
                  interpreter.interpret(model, assembler);
               } finally {
                  OrcsScriptUtil.unbind(model);
               }
            }

            OrcsScriptExecutor executor = getExecutor(assembler);
            return executor.execute(session, parameters);
         } catch (Exception ex) {
            throw newException(ex);
         } finally {
            output.onEvalEnd();
         }
      }

      private Map<String, Object> asMap(ScriptContext context) {
         Map<String, Object> data = new LinkedHashMap<>();
         for (Integer scope : context.getScopes()) {
            Bindings bindings = context.getBindings(scope);
            if (bindings != null) {
               for (Entry<String, Object> entry : bindings.entrySet()) {
                  data.put(entry.getKey(), entry.getValue());
               }
            }
         }
         return data;
      }
   }

}
