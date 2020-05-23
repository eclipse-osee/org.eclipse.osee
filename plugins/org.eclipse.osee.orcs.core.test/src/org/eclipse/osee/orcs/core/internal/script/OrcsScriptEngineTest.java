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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.Reader;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.eclipse.osee.orcs.core.internal.script.OrcsScriptCompiler.OrcsCompiledScript;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ScriptEngines} {@link OrcsScriptEngine}
 * 
 * @author Roberto E. Escobar
 */
public class OrcsScriptEngineTest {

   private static final String UNKNOWN_FILENAME = "<Unknown Source>";

   //@formatter:off
   @Mock private OrcsScriptCompiler compiler;
   @Mock private OrcsCompiledScript compiledScript;
  
   @Mock private Reader reader;
   @Mock private Object result;
   @Captor private ArgumentCaptor<ScriptContext> captor;
   @Captor private ArgumentCaptor<Reader> readerCaptor;
   //@formatter:on

   private ScriptEngine engine;

   @Before
   public void setup() throws OrcsScriptException {
      MockitoAnnotations.initMocks(this);
      ScriptEngineManager manager = ScriptEngines.newScriptEngineManager(compiler);
      engine = manager.getEngineByExtension("orcs");

      when(compiler.compileReader(any(Reader.class), anyString())).thenReturn(compiledScript);
      when(compiledScript.eval(any(ScriptContext.class))).thenReturn(result);
   }

   @Test
   public void testEvalReader() throws ScriptException {
      Object actual = engine.eval(reader);

      assertEquals(result, actual);
      verify(compiler).compileReader(reader, UNKNOWN_FILENAME);
   }

   @Test
   public void testEvalReaderWithBindings() throws ScriptException {
      Bindings data = engine.createBindings();
      data.put("a", "1");

      Object actual = engine.eval(reader, data);

      assertEquals(result, actual);
      verify(compiledScript).eval(captor.capture());
      verify(compiler).compileReader(reader, UNKNOWN_FILENAME);

      ScriptContext context = captor.getValue();
      assertEquals("1", context.getAttribute("a"));
   }

   @Test
   public void testEvalReaderWithContext() throws ScriptException {
      ScriptContext data = new SimpleScriptContext();
      data.setAttribute("a", "1", ScriptContext.ENGINE_SCOPE);

      Object actual = engine.eval(reader, data);

      assertEquals(result, actual);
      verify(compiledScript).eval(captor.capture());
      verify(compiler).compileReader(reader, UNKNOWN_FILENAME);

      ScriptContext context = captor.getValue();
      assertEquals("1", context.getAttribute("a"));
   }

   @Test
   public void testEvalWithEngineContext() throws ScriptException {
      ScriptContext data = new SimpleScriptContext();
      data.setAttribute("a", "1", ScriptContext.ENGINE_SCOPE);
      data.setAttribute(ScriptEngine.FILENAME, "filename-1", ScriptContext.ENGINE_SCOPE);

      engine.setContext(data);

      Object actual = engine.eval(reader);

      assertEquals(result, actual);
      verify(compiledScript).eval(captor.capture());
      verify(compiler).compileReader(reader, "filename-1");

      ScriptContext context = captor.getValue();
      assertEquals("1", context.getAttribute("a"));
      assertEquals("filename-1", context.getAttribute(ScriptEngine.FILENAME));
   }

   @Test
   public void testEvalString() throws ScriptException, IOException {
      ScriptContext data = new SimpleScriptContext();
      data.setAttribute("a", "1", ScriptContext.ENGINE_SCOPE);
      data.setAttribute(ScriptEngine.FILENAME, "filename-1", ScriptContext.ENGINE_SCOPE);

      String script = "abcde;";

      Object actual = engine.eval(script, data);

      assertEquals(result, actual);
      verify(compiledScript).eval(captor.capture());
      verify(compiler).compileReader(readerCaptor.capture(), eq("filename-1"));

      ScriptContext context = captor.getValue();
      assertEquals("1", context.getAttribute("a"));
      assertEquals("filename-1", context.getAttribute(ScriptEngine.FILENAME));

      Reader reader2 = readerCaptor.getValue();
      assertEquals(script, CharStreams.toString(reader2));
   }

   @Test
   public void testCompileable() throws ScriptException, IOException {
      Compilable compilable = (Compilable) engine;

      String script = "abcde;";
      CompiledScript compiled = compilable.compile(script);

      assertEquals(engine, compiled.getEngine());

      verify(compiler).compileReader(readerCaptor.capture(), eq(UNKNOWN_FILENAME));
      Reader reader2 = readerCaptor.getValue();
      assertEquals(script, CharStreams.toString(reader2));
   }

   @Test
   public void testCompileable2() throws ScriptException, IOException {
      engine.getContext().setAttribute(ScriptEngine.FILENAME, "filename-1", ScriptContext.ENGINE_SCOPE);

      Compilable compilable = (Compilable) engine;

      String script = "abcde;";
      CompiledScript compiled = compilable.compile(script);

      assertEquals(engine, compiled.getEngine());

      verify(compiler).compileReader(readerCaptor.capture(), eq("filename-1"));
      Reader reader2 = readerCaptor.getValue();
      assertEquals(script, CharStreams.toString(reader2));

      ScriptContext context = new SimpleScriptContext();
      context.setAttribute("a", "1", ScriptContext.ENGINE_SCOPE);
      Object actual = compiled.eval(context);

      assertEquals(result, actual);
      verify(compiledScript).eval(captor.capture());

      assertEquals("1", captor.getValue().getAttribute("a"));
   }
}
