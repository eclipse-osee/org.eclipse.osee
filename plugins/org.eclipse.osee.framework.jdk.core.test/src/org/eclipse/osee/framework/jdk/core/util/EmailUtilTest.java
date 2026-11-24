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

import java.util.Arrays;
import java.util.List;
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
   public void testSplitAddresses() {
      List<String> expected = Arrays.asList("a@x.com", "b@x.com");
      Assert.assertEquals(Arrays.asList("a@x.com"), EmailUtil.splitAddresses("a@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com,b@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com;b@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com b@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com, b@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com ; b@x.com"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("a@x.com\nb@x.com\t"));
      Assert.assertEquals(expected, EmailUtil.splitAddresses("  ,a@x.com,, b@x.com;  "));
   }

   @Test
   public void testSplitAddressesDropsDuplicatesKeepingOrder() {
      Assert.assertEquals(Arrays.asList("b@x.com", "a@x.com"), EmailUtil.splitAddresses("b@x.com,a@x.com,b@x.com"));
   }

   @Test
   public void testSplitAddressesEmptyAndNull() {
      Assert.assertTrue(EmailUtil.splitAddresses(null).isEmpty());
      Assert.assertTrue(EmailUtil.splitAddresses("").isEmpty());
      Assert.assertTrue(EmailUtil.splitAddresses(" , ; ").isEmpty());
   }

   @Test
   public void testSplitAddressesResultsAreValidated() {
      for (String address : EmailUtil.splitAddresses("a@x.com, b@y.org")) {
         Assert.assertTrue(address, EmailUtil.isEmailValid(address));
      }
      Assert.assertTrue(EmailUtil.isEmailInValid(EmailUtil.splitAddresses("notanemail").get(0)));
   }

   @Test
   public void testIsEmailInValid() {
      Assert.assertFalse(EmailUtil.isEmailInValid("d@asdf.com"));
      Assert.assertTrue(EmailUtil.isEmailInValid("d\"as..df.com"));
      Assert.assertTrue(EmailUtil.isEmailInValid("d&as..df.com"));
   }

}
