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
import org.eclipse.osee.define.ide.traceability.CodeUnitTagger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ryan Rader
 */
public class CodeUnitAnnotationUtilityTest {

   private CharBuffer getClassNoAnnotation() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\n");
      sb.append("-- Proprietary header ends------------------------------------------------------");
      sb.append("\n");
      return CharBuffer.wrap(sb.toString());
   }

   private CharBuffer getClassWithAnnotation() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      sb.append("\n");
      sb.append("-- Proprietary header ends------------------------------------------------------");
      sb.append("\n");
      sb.append("-- ObjectId(\"TEST\")\n");
      return CharBuffer.wrap(sb.toString());
   }

   @Test
   public void testGetTraceAnnotationGuid() {
      CodeUnitTagger util = CodeUnitTagger.getInstance();
      // Check that getSourceTag returns null when there is no ObjectId
      String guid = util.getSourceTag(getClassNoAnnotation());
      Assert.assertNull(guid);

      // Check that getSourceTag returns 'TEST' when there is an ObjectId = TEST
      guid = util.getSourceTag(getClassWithAnnotation());
      Assert.assertEquals("TEST", guid);
   }

   @Test
   public void testAddTraceAnnotation() {
      CodeUnitTagger util = CodeUnitTagger.getInstance();
      // Check that addSourceTag returns ObjectId("TEST") when it adds an objectId
      String result = util.addSourceTag(getClassNoAnnotation(), "TEST").toString();
      Assert.assertTrue(result.contains("-- ObjectId(\"TEST\")"));

      // Check that addSourceTag overwrites TEST with GUID
      result = util.addSourceTag(getClassWithAnnotation(), "GUID").toString();
      Assert.assertTrue(result.contains("-- ObjectId(\"GUID\")"));
      Assert.assertFalse(result.contains("-- ObjectId(\"TEST\")"));
   }

   @Test
   public void testRemoveTraceAnnotation() {
      CodeUnitTagger util = CodeUnitTagger.getInstance();
      // Check that removeSourceTag doesn't add in ObjectId("TEST") if it wasnt there to begin with
      String result = util.removeSourceTag(getClassNoAnnotation()).toString();
      Assert.assertFalse(result.contains("-- ObjectId(\"TEST\")"));
      // Check that removeSourceTag returns ObjectId("TEST") is no longer there
      result = util.removeSourceTag(getClassWithAnnotation()).toString();
      Assert.assertFalse(result.contains("-- ObjectId(\"TEST\")"));
   }
}
