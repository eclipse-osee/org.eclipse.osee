/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.api;

import static org.eclipse.osee.orcs.OrcsIntegrationRule.integrationRule;
import static org.junit.Assert.assertEquals;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OrcsScriptTest {

   @Rule
   public TestRule osgi = integrationRule(this, "osee.demo.hsql");

   @OsgiService
   private OrcsApi orcsApi;

   private ScriptEngine engine;

   private final String script;
   private final String expected;

   public OrcsScriptTest(String script, String expected) {
      this.script = script;
      this.expected = expected;
   }

   @Before
   public void setup() {
      engine = orcsApi.getScriptEngine();
   }

   @Test
   public void testOrcsQuery() throws ScriptException {
      StringWriter writer = new StringWriter();
      engine.eval(script, newContext(writer));
      String actual = writer.toString();
      assertEquals(expected, normalize(actual));
   }

   @Parameters
   public static Iterable<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();

      add(data, "start from branch 570 collect branches {*}", //
         "{\n" + //
         "  'script' : 'start from branch 570 collect branches {*};',\n" + //
         "  'results' : [ {\n" + //
         "    'branches' : [ {\n" + //
         "      'archived' : 'UNARCHIVED',\n" + //
         "      'assoc-id' : -1,\n" + //
         "      'baseline-tx-id' : 4,\n" + //
         "      'id' : 570,\n" + //
         "      'inherits-access-control' : 0,\n" + //
         "      'name' : 'Common',\n" + //
         "      'parent-id' : 1,\n" + //
         "      'parent-tx-id' : 3,\n" + //
         "      'state' : 'MODIFIED',\n" + //
         "      'type' : 'BASELINE'\n" + //
         "    } ]\n" + //
         "  } ]\n" + //
         "}");

      add(data, "start from branch where state = modified collect branches {name};", //
         "{\n" + //
         "  'script' : 'start from branch where state = modified collect branches {name};',\n" + //
         "  'results' : [ {\n" + //
         "    'branches' : [ {\n" + //
         "      'name' : 'System Root Branch'\n" + //
         "    }, {\n" + //
         "      'name' : 'SAW_Bld_1'\n" + //
         "    }, {\n" + //
         "      'name' : 'CIS_Bld_1'\n" + //
         "    }, {\n" + //
         "      'name' : 'Common'\n" + //
         "    } ]\n" + //
         "  } ]\n" + //
         "}");

      add(
         data,
         "start from branch 570 find artifacts where art-type = 'Folder' collect artifacts {id, attributes { value } };", //
         "{\n" + //
         "  'script' : 'start from branch 570 find artifacts where art-type = 'Folder' collect artifacts {id, attributes { value } };',\n" + //
         "  'results' : [ {\n" + //
         "    'artifacts' : [ {\n" + //
         "      'id' : 7,\n" + //
         "      'attributes' : {\n" + //
         "        'Name' : {\n" + //
         "          'value' : 'User Groups'\n" + //
         "        }\n" + //
         "      }\n" + //
         "    }, {\n" + //
         "      'id' : 22,\n" + //
         "      'attributes' : {\n" + //
         "        'Name' : {\n" + //
         "          'value' : 'Document Templates'\n" + //
         "        }\n" + //
         "      }\n" + //
         "    } ]\n" + //
         "  } ]\n" + //
         "}");

      add(
         data,
         "start from branch 'SAW_Bld_1' find artifacts where attribute type = 'Name' match-order 'Read-only Robots' collect artifacts as 'Requirement' { id, attributes { value } };", //
         "{\n" + //
         "  'script' : 'start from branch 'SAW_Bld_1' find artifacts where attribute type = 'Name' match-order 'Read-only Robots' collect artifacts as 'Requirement' { id, attributes { value } };',\n" + //
         "  'results' : [ {\n" + //
         "    'Requirement' : [ {\n" + //
         "      'id' : 81,\n" + //
         "      'attributes' : {\n" + //
         "        'Name' : {\n" + //
         "          'value' : 'Read-only Robots'\n" + //
         "        },\n" + //
         "        'Word Template Content' : {\n" + //
         "          'value' : '<w:p wsp:rsidR=\\'006A3C0C\\' wsp:rsidRDefault=\\'006A3C0C\\' wsp:rsidP=\\'00E54E52\\'><w:r wsp:rsidRPr=\\'00EB2959\\'><w:t>Individual and collaborative robots can be “read-only” (i.e., provide only state information) or “read-write” (i.e., provide state inform</w:t></w:r><w:r><w:t>ation and allow state changes).</w:t></w:r></w:p>'\n" + //
         "        },\n" + //
         "        'Paragraph Number' : {\n" + //
         "          'value' : '1.1.4'\n" + //
         "        },\n" + //
         "        'Partition' : {\n" + //
         "          'value' : 'Unspecified'\n" + //
         "        },\n" + //
         "        'Subsystem' : {\n" + //
         "          'value' : 'Unspecified'\n" + //
         "        },\n" + //
         "        'Qualification Method' : {\n" + //
         "          'value' : 'Unspecified'\n" + //
         "        },\n" + //
         "        'Development Assurance Level' : {\n" + //
         "          'value' : 'E'\n" + //
         "        },\n" + //
         "        'Technical Performance Parameter' : {\n" + //
         "          'value' : 'false'\n" + //
         "        },\n" + //
         "        'CSCI' : {\n" + //
         "          'value' : 'Unspecified'\n" + //
         "        }\n" + //
         "      }\n" + //
         "    } ]\n" + //
         "  } ]\n" + //
         "}");
      return data;
   }

   private static void add(List<Object[]> data, Object... args) {
      data.add(args);
   }

   private String normalize(String value) {
      value = value.replaceAll("\r\n", "\n");
      value = value.replaceAll("\"", "'");
      return value;
   }

   private ScriptContext newContext(StringWriter writer) {
      ScriptContext context = new SimpleScriptContext();
      context.setWriter(writer);
      context.setErrorWriter(writer);
      return context;
   }

}
