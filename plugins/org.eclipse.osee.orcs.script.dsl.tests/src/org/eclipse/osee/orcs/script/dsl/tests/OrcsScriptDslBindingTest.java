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

package org.eclipse.osee.orcs.script.dsl.tests;

import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslInjectorProvider;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslResource;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.formatting.OrcsScriptDslFormatter;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScript;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsExpression;
import org.eclipse.osee.orcs.script.dsl.typesystem.OsExpressionResolver;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link OrcsScriptDslFormatter}
 * 
 * @author Roberto E. Escobar
 */
@InjectWith(OrcsScriptDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class OrcsScriptDslBindingTest {

   @Inject
   private OsExpressionResolver resolver;

   @Test
   public void testBind1() throws Exception {
      String input = "var a = {{key-1}};";

      ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
      OrcsScriptDslResource resource = OrcsScriptUtil.loadModel(inputStream, "orcs:/unknown.orcs");

      OrcsScript model = resource.getModel();

      Map<String, Object> binding = new HashMap<>();
      binding.put("key-1", "hello");

      OrcsScriptUtil.bind(model, binding);

      String actual = resolver.resolveSingle(String.class, getExpression(model));
      Assert.assertEquals("hello", actual);
   }

   private OsExpression getExpression(OrcsScript model) {
      return (OsExpression) model.getStatements().get(0);
   }

}
