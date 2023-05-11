/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - Initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import org.junit.Assert;
import org.junit.Test;

public class EmailUtilTest {

   @Test
   public void testIsEmailValid() {
      Assert.assertTrue(EmailUtil.isEmailValid("d@asdf.com"));
      Assert.assertFalse(EmailUtil.isEmailValid("d\"as.df.com"));
      Assert.assertFalse(EmailUtil.isEmailValid("d&as.df.com"));
   }

   @Test
   public void testIsEmailInValid() {
      Assert.assertFalse(EmailUtil.isEmailInValid("d@asdf.com"));
      Assert.assertTrue(EmailUtil.isEmailInValid("d\"as..df.com"));
      Assert.assertTrue(EmailUtil.isEmailInValid("d&as..df.com"));
   }

}
