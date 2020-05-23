/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.script;

import java.io.Reader;
import java.io.StringReader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptCompiler.OrcsCompiledScript;

/**
 * @author Roberto E. Escobar
 */
public class OrcsScriptEngine extends AbstractScriptEngine implements Compilable {

   private final OrcsScriptCompiler compiler;
   private ScriptEngineFactory factory;

   public OrcsScriptEngine(OrcsScriptCompiler compiler) {
      this.compiler = compiler;
   }

   public void setEngineFactory(ScriptEngineFactory factory) {
      this.factory = factory;
   }

   private OrcsScriptCompiler getCompiler() {
      return compiler;
   }

   @Override
   public ScriptEngineFactory getFactory() {
      return factory != null ? factory : new OrcsScriptEngineFactory(compiler);
   }

   @Override
   public Bindings createBindings() {
      return new SimpleBindings();
   }

   @Override
   public Object eval(String script, ScriptContext context) throws ScriptException {
      if (script == null) {
         throw new NullPointerException("script was null");
      }
      return eval(new StringReader(script), context);
   }

   @Override
   public CompiledScript compile(String script) throws ScriptException {
      return compile(new StringReader(script));
   }

   @Override
   public Object eval(Reader reader, ScriptContext context) throws ScriptException {
      String fileName = (String) context.getAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE);
      if (!Strings.isValid(fileName)) {
         fileName = "<Unknown Source>";
      }
      OrcsScriptCompiler runtime = getCompiler();
      OrcsCompiledScript script = runtime.compileReader(reader, fileName);
      return script.eval(context);
   }

   @Override
   public CompiledScript compile(Reader reader) throws ScriptException {
      String fileName = (String) get(ScriptEngine.FILENAME);
      if (!Strings.isValid(fileName)) {
         fileName = "<Unknown Source>";
      }
      OrcsScriptCompiler runtime = getCompiler();
      OrcsCompiledScript script = runtime.compileReader(reader, fileName);
      return newCompiledScript(this, script);
   }

   @Override
   protected ScriptContext getScriptContext(Bindings nn) {
      return super.getScriptContext(nn);
   }

   private static CompiledScript newCompiledScript(final ScriptEngine engine, final OrcsCompiledScript script) {
      return new CompiledScript() {

         @Override
         public Object eval(ScriptContext context) throws ScriptException {
            return script.eval(context);
         }

         @Override
         public ScriptEngine getEngine() {
            return engine;
         }
      };
   }

}
