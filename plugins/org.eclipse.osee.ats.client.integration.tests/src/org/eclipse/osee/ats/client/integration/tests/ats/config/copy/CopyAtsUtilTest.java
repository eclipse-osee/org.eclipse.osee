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
package org.eclipse.osee.ats.client.integration.tests.ats.config.copy;

import org.junit.Assert;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.config.copy.CopyAtsUtil;

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
