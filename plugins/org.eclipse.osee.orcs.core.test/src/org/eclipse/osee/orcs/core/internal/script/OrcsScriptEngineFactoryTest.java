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
import static org.junit.Assert.assertNotNull;
import java.util.Arrays;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link ScriptEngines} {@link OrcsScriptEngineFactory}
 * 
 * @author Roberto E. Escobar
 */
public class OrcsScriptEngineFactoryTest {

   private static final List<String> mediaTypes = Arrays.asList("application/orcsscript", "text/orcsscript");
   private static final List<String> names = Arrays.asList("orcs", "OrcsScript", "OrcsDsl", "orcsdsl", "orcsscript");

   private ScriptEngineManager scriptManager;

   @Before
   public void setup() {
      scriptManager = ScriptEngines.newScriptEngineManager(null);
   }

   @Test
   public void testGetByMimeType() {
      for (String type : mediaTypes) {
         ScriptEngine engine = scriptManager.getEngineByMimeType(type);
         assertNotNull(engine);
         assertEquals(OrcsScriptEngine.class, engine.getClass());
      }
   }

   @Test
   public void testGetByName() {
      for (String name : names) {
         ScriptEngine engine = scriptManager.getEngineByName(name);
         assertNotNull(engine);
         assertEquals(OrcsScriptEngine.class, engine.getClass());
      }
   }

   @Test
   public void testGetByExtension() {
      ScriptEngine engine = scriptManager.getEngineByExtension("orcs");
      assertNotNull(engine);
      assertEquals(OrcsScriptEngine.class, engine.getClass());
   }

   @Test
   public void testEngineFactory() {
      ScriptEngine engine = scriptManager.getEngineByName("orcs");
      assertNotNull(engine);

      ScriptEngineFactory factory = engine.getFactory();
      assertEquals("ORCS Script Engine", factory.getEngineName());
      assertEquals("ORCS Script", factory.getLanguageName());
      assertEquals("1.0.0", factory.getEngineVersion());
      assertEquals("1.0.0", factory.getLanguageVersion());

      assertEquals("orcsscript", factory.getParameter(ScriptEngine.NAME));
      assertEquals("ORCS Script Engine", factory.getParameter(ScriptEngine.ENGINE));
      assertEquals("ORCS Script", factory.getParameter(ScriptEngine.LANGUAGE));
      assertEquals("1.0.0", factory.getParameter(ScriptEngine.ENGINE_VERSION));
      assertEquals("1.0.0", factory.getParameter(ScriptEngine.ENGINE_VERSION));
      assertEquals("STATELESS", factory.getParameter("THREADING"));

      List<String> actual = factory.getExtensions();
      assertEquals("orcs", actual.get(0));

      assertEquals(false, Compare.isDifferent(mediaTypes, factory.getMimeTypes()));
      assertEquals(false, Compare.isDifferent(names, factory.getNames()));

      String program = factory.getProgram("script-version 1.0.0", "start branch 570 find artifacts *");
      assertEquals("script-version 1.0.0;start branch 570 find artifacts *;", program);

      String outputStatement = factory.getOutputStatement("hello");
      assertEquals("print(\"hello\")", outputStatement);

      String methodCallSyntax = factory.getMethodCallSyntax("object", "method", "arg1", "arg2");
      assertEquals("object.method(arg1,arg2)", methodCallSyntax);
   }

}
