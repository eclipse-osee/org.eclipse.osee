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
package org.eclipse.osee.orcs.script.dsl.tests;

import static org.junit.Assert.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslConstants;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslInjectorProvider;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptDslResource;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil.OsStorageOption;
import org.eclipse.osee.orcs.script.dsl.formatting.OrcsScriptDslFormatter;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Case for {@link OrcsScriptDslFormatter}
 * 
 * @author Roberto E. Escobar
 */
@InjectWith(OrcsScriptDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class OrcsScriptDslFormatterTest {

   private static final String INDENT = OrcsScriptDslConstants.FORMATTING__INDENT_STRING;
   private static final String LN = System.getProperty("line.separator");

   private static final String INPUT_1 = //
      "script-version 0.12.0   ; // a comment" + //
         LN + //
         "start from tx where date in  ( '12/24/2014 12:12:12 AM' .. '12/24/2014 12:12:12 AM' )" + //
         "find artifacts where art-type instance-of [23]" + //
         "                  follow    relation type = 324     to   side-A  ;" + //
         "/**" + LN + //
         " * multi-line comment" + LN + //
         " */ start    from     branch    570      " + //
         "follow relation type =    324 to    side-B" + //
         "           find artifacts where art-type = 1231" + //
         " collect artifacts as 'alias-2' {   id as 'id-1'   ,   guid as 'id-2'  ,  attributes { uri, txs {*} }, type }" + //
         "       limit    34;" + //
         "             start from branch  570 " + //
         "follow relation type = 324    to side-B ;" + //
         "              start    from tx * ;";

   private static final String FORMATTED_INPUT_1 = //
      "script-version 0.12.0; // a comment" + LN + //
         LN + //
         "start from tx where date in ('12/24/2014 12:12:12 AM'..'12/24/2014 12:12:12 AM')" + LN + //
         "find artifacts where art-type instance-of [23]" + LN + //
         "follow relation type = 324 to side-A;" + LN + //
         LN + //
         "/**" + LN + //
         " * multi-line comment" + LN + //
         " */" + LN + //
         "start from branch 570" + LN + //
         "follow relation type = 324 to side-B" + LN + //
         "find artifacts where art-type = 1231" + LN + //
         "collect artifacts as 'alias-2' {" + LN + //
         INDENT + "id as 'id-1', guid as 'id-2', attributes {" + LN + //
         INDENT + INDENT + "uri, txs { * }" + LN + //
         INDENT + "}, type" + LN + //
         "} limit 34;" + LN + //
         LN + //
         "start from branch 570" + LN + //
         "follow relation type = 324 to side-B;" + LN + //
         LN + //
         "start from tx *;";

   @Test
   public void testLoadSaveAndWithFormatting() throws Exception {
      String input = INPUT_1;
      String expected = FORMATTED_INPUT_1;

      ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
      OrcsScriptDslResource resource = OrcsScriptUtil.loadModel(inputStream, "orcs:/unknown.orcs");

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      OrcsScriptUtil.saveModel(resource, outputStream, OsStorageOption.FORMAT_ON_SAVE);
      String actual = outputStream.toString("UTF-8");

      assertEquals(expected, actual);
   }

   @Test
   public void testLoadSaveAndNoFormatting() throws Exception {
      String input = INPUT_1;
      String expected = INPUT_1;

      ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
      OrcsScriptDslResource resource = OrcsScriptUtil.loadModel(inputStream, "orcs:/unknown.orcs");

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      OrcsScriptUtil.saveModel(resource, outputStream);
      String actual = outputStream.toString("UTF-8");

      assertEquals(expected, actual);
   }
}
