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
package org.eclipse.osee.framework.ui.skynet.renderer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RenderingUtilTest {
   private static BranchTokenImpl branch;

   @BeforeClass
   public static void setUpOnce() throws OseeCoreException {
      branch = new BranchTokenImpl(1L, "Test 1");
   }

   @Test
   public void testBranchToFileName() throws Exception {
      String actual = RenderingUtil.toFileName(branch);
      Assert.assertEquals(encode(branch.getShortName()), actual);
   }

   @Test
   public void test_branchToFileName_notAllowedCharsInName() throws OseeCoreException {
      String branchShortName = "";

      branch.setName("0123455789012345578901234557890123.5");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "0123455789012345578901234557890123_",
         branchShortName);

      branch.setName("Dev>>>>>Branch");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "Dev_Branch", branchShortName);

      branch.setName("Dev/Branch");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "Dev_Branch", branchShortName);

      branch.setName("DevBranch?");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "DevBranch_", branchShortName);

      branch.setName("1234-changes:software");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "1234-changes_software", branchShortName);

      branch.setName("1234-changes:software*");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "1234-changes_software_", branchShortName);

      branch.setName("newchanges|software<<<<hardware");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "newchanges_software_hardware",
         branchShortName);

      branch.setName("someRequirementChangeCalled\"My\"Changes");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "someRequirementChangeCalled_My_Chan",
         branchShortName);

      branch.setName("aBranchName\\here");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "aBranchName_here", branchShortName);

      branch.setName("aDifferent'Name'here");
      branchShortName = RenderingUtil.toFileName(branch);
      Assert.assertEquals("Not safe character found at end of branch name.", "aDifferent_Name_here", branchShortName);

   }

   private String encode(String guid) throws UnsupportedEncodingException {
      return URLEncoder.encode(guid, "UTF-8");
   }

   public static final class BranchTokenImpl extends NamedIdBase implements IOseeBranch {
      public BranchTokenImpl(Long id, String name) {
         super(id, name);
      }
   }
}