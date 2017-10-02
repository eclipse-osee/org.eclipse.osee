/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.ops;

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test Case for {@link RenameFilesOperation}
 * 
 * @author Ryan D. Brooks
 */
public class RenameFilesOperationTest {
   @Rule
   public final TemporaryFolder tempFolder = new TemporaryFolder();

   @Test
   public void testDelayedParameterValues() throws IOException {
      tempFolder.newFile("abcde1");

      StringBuilder parentFolder = new StringBuilder();
      StringBuffer pathPattern = new StringBuffer();
      RenameFilesOperation renameOperation =
         new RenameFilesOperation(NullOperationLogger.getSingleton(), parentFolder, pathPattern, "");

      parentFolder.append(tempFolder.getRoot().getCanonicalPath());
      pathPattern.append(".+ab(cde)\\d");

      Operations.executeWorkAndCheckStatus(renameOperation);
      Assert.assertTrue(new File(parentFolder.toString(), "ab1").exists());
   }

   @Test
   public void testMultipleFileMatches() throws IOException {
      tempFolder.newFile("abcde12");
      tempFolder.newFile("abcde13");
      tempFolder.newFile("abcde21");

      String parentFolder = tempFolder.getRoot().getCanonicalPath();

      RenameFilesOperation renameOperation =
         new RenameFilesOperation(NullOperationLogger.getSingleton(), parentFolder, ".+ab(cde)1\\d", "_");

      Operations.executeWorkAndCheckStatus(renameOperation);
      Assert.assertTrue(new File(parentFolder, "ab_12").exists());
      Assert.assertTrue(new File(parentFolder, "ab_13").exists());
      Assert.assertTrue(new File(parentFolder, "abcde21").exists());
   }
}