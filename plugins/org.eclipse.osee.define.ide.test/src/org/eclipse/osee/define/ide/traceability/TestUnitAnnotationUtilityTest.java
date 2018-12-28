/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability;

import java.nio.CharBuffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class TestUnitAnnotationUtilityTest {

   private static final String IMPORT_STRING = "import org.eclipse.osee.framework.jdk.core.type.ObjectId;";

   private CharBuffer getClassWithImportNoAnnotation() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append(IMPORT_STRING);
      sb.append("\n");
      sb.append("import org.junit.Assert;\n");
      sb.append("public class test {\n");
      return CharBuffer.wrap(sb.toString());
   }

   private CharBuffer getClassNoImportNoAnnotation() {
      return CharBuffer.wrap("import org.junit.Assert;\n\npublic class test {\n");
   }

   private CharBuffer getClassWithImportAndAnnotation() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append(IMPORT_STRING);
      sb.append("\n");
      sb.append("import org.junit.Assert;\n");
      sb.append("@ObjectId(\"TEST\")\n");
      sb.append("public class test {\n");
      return CharBuffer.wrap(sb.toString());
   }

   @Test
   public void testGetTraceAnnotationGuid() {
      TestUnitTagger util = TestUnitTagger.getInstance();
      String guid = util.getSourceTag(getClassWithImportNoAnnotation());
      Assert.assertNull(guid);

      guid = util.getSourceTag(getClassNoImportNoAnnotation());
      Assert.assertNull(guid);

      guid = util.getSourceTag(getClassWithImportAndAnnotation());
      Assert.assertEquals("TEST", guid);
   }

   @Test
   public void testAddTraceAnnotation() {
      TestUnitTagger util = TestUnitTagger.getInstance();
      String result = util.addSourceTag(getClassNoImportNoAnnotation(), "TEST").toString();
      Assert.assertTrue(result.contains(IMPORT_STRING));
      Assert.assertTrue(result.contains("@ObjectId(\"TEST\")"));

      result = util.addSourceTag(getClassWithImportAndAnnotation(), "GUID").toString();
      Assert.assertTrue(result.contains(IMPORT_STRING));
      Assert.assertTrue(result.contains("@ObjectId(\"GUID\")"));
      Assert.assertFalse(result.contains("@ObjectId(\"TEST\")"));
   }

   @Test
   public void testRemoveTraceAnnotation() {
      TestUnitTagger util = TestUnitTagger.getInstance();
      String result = util.removeSourceTag(getClassNoImportNoAnnotation()).toString();
      Assert.assertFalse(result.contains(IMPORT_STRING));
      Assert.assertFalse(result.contains("@ObjectId(\"TEST\")"));

      result = util.removeSourceTag(getClassWithImportAndAnnotation()).toString();
      Assert.assertFalse(result.contains(IMPORT_STRING));
      Assert.assertFalse(result.contains("@ObjectId(\"TEST\")"));
   }
}
