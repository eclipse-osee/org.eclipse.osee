/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.renderer;

import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.publishing.FilenameFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RenderingUtilTest {
   private static BranchToken branch;

   @BeforeClass
   public static void setUpOnce() {
      branch = BranchToken.create("Test 1");
   }

   @Test
   public void testBranchToFileName() throws Exception {
      String actual = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Test-1", actual);
   }

   @Test
   public void test_branchToFileName_notAllowedCharsInName() {
      String branchShortName = "";

      //branch.getShortName truncates after the '.'. Trailing unsafe character will be trimmed.
      branch = BranchToken.create("0123455789012345578901234557890123.5");
      branchShortName = FilenameFactory.makeNameSafer(branch.getName());
      Assert.assertEquals("Not safe character found at end of branch name.", "01234557890123455789", branchShortName);

      branch = BranchToken.create("123455789012345578.5");
      branchShortName = FilenameFactory.makeNameSafer(branch.getName());
      Assert.assertEquals("Not safe character found at in of branch name.", "123455789012345578-5", branchShortName);

      branch = BranchToken.create("Dev>>>>>Branch");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "Dev-Branch", branchShortName);

      branch = BranchToken.create("Dev/Branch");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "Dev-Branch", branchShortName);

      branch = BranchToken.create("DevBranch?");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "DevBranch", branchShortName);

      branch = BranchToken.create("123-changes:software");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "123-changes-software", branchShortName);

      branch = BranchToken.create("12-changes:software*");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "12-changes-software", branchShortName);

      branch = BranchToken.create("new|soft<<<<ware");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "new-soft-ware", branchShortName);

      branch = BranchToken.create("some\"My\"Changes");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "some-My-Changes", branchShortName);

      branch = BranchToken.create("aBranchName\\here");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "aBranchName-here", branchShortName);

      branch = BranchToken.create("aDifferent'Name'here");
      branchShortName = FilenameFactory.makeNameSafer(branch.getShortName());
      Assert.assertEquals("Not safe character found at end of branch name.", "aDifferent-Name-here", branchShortName);

      var testString = "  All the /<>(){}[].:;\"\'\\|?*+ dirty chars.  ";
      var cleanString = FilenameFactory.makeNameSafer(testString);
      Assert.assertEquals("Not safe character found in test string.", "All-the-dirty-chars", cleanString);

   }

}