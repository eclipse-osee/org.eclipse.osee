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
package org.eclipse.osee.orcs.core.internal.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptEngineFactory implements ScriptEngineFactory {

   private static final String THREADING = "THREADING";
   private static final String STATELESS_THREADING = "STATELESS";

   private static final String SCRIPT_LANGUAGE_NAME = "ORCS Script";
   private static final String SCRIPT_NAME = "orcsscript";
   private static final String SCRIPT_ENGINE_NAME = "ORCS Script Engine";

   private static final String SCRIPT_LANGUAGE_VERSION = "1.0.0";
   private static final String SCRIPT_ENGINE_VERSION = "1.0.0";

   private static List<String> extensions;
   private static List<String> mimeTypes;
   private static List<String> aliases;

   static {
      List<String> names = new ArrayList<>(6);
      names.add("orcs");
      names.add("OrcsScript");
      names.add(SCRIPT_NAME);
      names.add("OrcsDsl");
      names.add("orcsdsl");
      aliases = Collections.unmodifiableList(names);

      List<String> mimeTypes = new ArrayList<>(4);
      mimeTypes.add("application/orcsscript");
      mimeTypes.add("text/orcsscript");
      OrcsScriptEngineFactory.mimeTypes = Collections.unmodifiableList(mimeTypes);

      List<String> extensions = new ArrayList<>(1);
      extensions.add("orcs");
      OrcsScriptEngineFactory.extensions = Collections.unmodifiableList(extensions);
   }

   private final OrcsScriptCompiler compiler;

   public OrcsScriptEngineFactory(OrcsScriptCompiler compiler) {
      this.compiler = compiler;
   }

   @Override
   public ScriptEngine getScriptEngine() {
      OrcsScriptEngine engine = new OrcsScriptEngine(compiler);
      engine.setEngineFactory(this);
      return engine;
   }

   @Override
   public List<String> getExtensions() {
      return extensions;
   }

   @Override
   public List<String> getMimeTypes() {
      return mimeTypes;
   }

   @Override
   public List<String> getNames() {
      return aliases;
   }

   public String getName() {
      return getParameter(ScriptEngine.NAME);
   }

   @Override
   public String getEngineName() {
      return getParameter(ScriptEngine.ENGINE);
   }

   @Override
   public String getEngineVersion() {
      return getParameter(ScriptEngine.ENGINE_VERSION);
   }

   @Override
   public String getLanguageName() {
      return getParameter(ScriptEngine.LANGUAGE);
   }

   @Override
   public String getLanguageVersion() {
      return getParameter(ScriptEngine.LANGUAGE_VERSION);
   }

   @Override
   public String getParameter(String key) {
      String toReturn = null;
      if (key.equals(ScriptEngine.NAME)) {
         toReturn = SCRIPT_NAME;
      } else if (key.equals(ScriptEngine.ENGINE)) {
         toReturn = SCRIPT_ENGINE_NAME;
      } else if (key.equals(ScriptEngine.LANGUAGE)) {
         toReturn = SCRIPT_LANGUAGE_NAME;
      } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
         toReturn = SCRIPT_ENGINE_VERSION;
      } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
         toReturn = SCRIPT_LANGUAGE_VERSION;
      } else if (key.equals(THREADING)) {
         toReturn = STATELESS_THREADING;
      }
      if (toReturn == null) {
         throw new IllegalArgumentException("Invalid key");
      }
      return toReturn;
   }

   @Override
   public String getMethodCallSyntax(String obj, String method, String... args) {
      StringBuilder builder = new StringBuilder() //
         .append(obj).append(".").append(method).append("(");
      int argSize = args.length;
      if (argSize > 0) {
         for (int index = 0; index < argSize; index++) {
            builder.append(args[index]);
            if (index != argSize - 1) {
               builder.append(",");
            } else {
               builder.append(")");
            }
         }
      } else {
         builder.append(")");
      }
      return builder.toString();
   }

   @Override
   public String getOutputStatement(String toDisplay) {
      StringBuilder builder = new StringBuilder();
      builder.append("print(\"");
      for (int index = 0; index < toDisplay.length(); index++) {
         char ch = toDisplay.charAt(index);
         switch (ch) {
            case '"':
               builder.append("\\\"");
               break;
            case '\\':
               builder.append("\\\\");
               break;
            default:
               builder.append(ch);
               break;
         }
      }
      builder.append("\")");
      return builder.toString();
   }

   @Override
   public String getProgram(String... statements) {
      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < statements.length; index++) {
         builder.append(statements[index]);
         builder.append(";");
      }
      return builder.toString();
   }

}