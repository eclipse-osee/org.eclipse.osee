/*******************************************************************************
 * Copyright (c) 20012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import org.junit.Assert;
import org.eclipse.osee.framework.core.util.IResultDataListener;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.core.util.XResultData.Type;
import org.junit.Test;

public class XResultDataTest {

   @Test
   public void testXResultData() {
      new XResultData();
      new XResultData(true);
      new XResultData(true, new IResultDataListener() {

         @Override
         public void log(Type type, String str) {
            // do nothing
         }

      });
   }

   @Test
   public void testLogString() {
      XResultData rd = new XResultData();
      rd.log("log string");
      Assert.assertEquals("log string\n", rd.toString());
   }

   @Test
   public void testLogWithFormat() {
      XResultData rd = new XResultData();
      rd.logWithFormat("log %s string", "this");
      Assert.assertEquals("log this string", rd.toString());
   }

   @Test
   public void testLogError() {
      XResultData rd = new XResultData();
      rd.logError("log string");
      Assert.assertEquals("Error: log string\n", rd.toString());
   }

   @Test
   public void testLogErrorWithFormat() {
      XResultData rd = new XResultData();
      rd.logErrorWithFormat("log %s string", "this");
      Assert.assertEquals("Error: log this string\n", rd.toString());
   }

   @Test
   public void testLogWarning() {
      XResultData rd = new XResultData();
      rd.logWarning("log string");
      Assert.assertEquals("Warning: log string\n", rd.toString());
   }

   @Test
   public void testLogWarningWithFormat() {
      XResultData rd = new XResultData();
      rd.logWarningWithFormat("log %s string", "this");
      Assert.assertEquals("Warning: log this string\n", rd.toString());
   }

   @Test
   public void testGetNumErrors() {
      XResultData rd = new XResultData();
      Assert.assertEquals(0, rd.getNumErrors());

      rd.logError("log string");
      rd.logError("log string");
      rd.logError("log string");
      Assert.assertEquals(3, rd.getNumErrors());
   }

   @Test
   public void testGetNumErrorsViaSearch() {
      XResultData rd = new XResultData();
      Assert.assertEquals(0, rd.getNumErrors());

      rd.log("Error: log string");
      rd.log("Error: log string");
      rd.log("Error: log string");
      Assert.assertEquals(3, rd.getNumErrorsViaSearch());
   }

   @Test
   public void testGetNumWarningsViaSearch() {
      XResultData rd = new XResultData();
      Assert.assertEquals(0, rd.getNumErrors());

      rd.log("Warning: log string");
      rd.log("Warning: log string");
      rd.log("Warning: log string");
      Assert.assertEquals(3, rd.getNumWarningsViaSearch());
   }

   @Test
   public void testGetNumWarnings() {
      XResultData rd = new XResultData();
      Assert.assertEquals(0, rd.getNumWarnings());

      rd.logWarning("log string");
      rd.logWarning("log string");
      rd.logWarning("log string");
      Assert.assertEquals(3, rd.getNumWarnings());
   }

   @Test
   public void testIsErrors() {
      XResultData rd = new XResultData();
      Assert.assertFalse(rd.isErrors());

      rd.logError("log string");
      rd.log("log string");
      rd.logWarning("log string");
      Assert.assertTrue(rd.isErrors());
   }

}
