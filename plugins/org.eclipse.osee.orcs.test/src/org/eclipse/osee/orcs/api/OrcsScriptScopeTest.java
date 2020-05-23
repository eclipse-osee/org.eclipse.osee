/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertTrue;
import java.io.StringWriter;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author David W. Miller
 */
public class OrcsScriptScopeTest {
   @Rule
   public TestRule osgi = integrationRule(this);

   @OsgiService
   private OrcsApi orcsApi;

   private ScriptEngine engine;

   private final String script =
      "start from branch 5 find artifacts where art-id = [278] collect artifacts { id } follow relation type = \"Requirement Trace\" to side-B collect artifacts { id, attributes { value } };";

   private final String expected = "AND txs4.branch_id = br1.branch_id";

   @Before
   public void setup() {
      engine = orcsApi.getScriptEngine();
   }

   @Test
   public void testScriptScope() throws ScriptException {
      StringWriter writer = new StringWriter();
      engine.eval(script, newContext(writer));
      String result = writer.toString();
      assertTrue(result.contains(expected));
   }

   private ScriptContext newContext(StringWriter writer) {
      ScriptContext context = new SimpleScriptContext();
      context.setWriter(writer);
      context.setErrorWriter(writer);
      context.setAttribute("output.debug", true, ScriptContext.ENGINE_SCOPE);
      return context;
   }

}
