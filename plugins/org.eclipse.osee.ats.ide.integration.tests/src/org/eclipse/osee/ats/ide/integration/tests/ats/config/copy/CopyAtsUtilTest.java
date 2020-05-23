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

package org.eclipse.osee.ats.ide.integration.tests.ats.config.copy;

import org.eclipse.osee.ats.ide.config.copy.ConfigData;
import org.eclipse.osee.ats.ide.config.copy.CopyAtsUtil;
import org.junit.Assert;

/**
 * Test case for {@link CopyAtsUtil}
 * 
 * @author Donald G. Dunne
 */
public class CopyAtsUtilTest {

   @org.junit.Test
   public void testGetConvertedName() throws Exception {
      ConfigData data = new ConfigData();
      data.setReplaceStr("ReplStr");
      data.setSearchStr("SrchStr");

      Assert.assertEquals("ReplStr is the one", CopyAtsUtil.getConvertedName(data, "SrchStr is the one"));
   }
}
