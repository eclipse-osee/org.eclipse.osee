/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.config.admin.internal;

import static org.junit.Assert.assertEquals;
import java.net.URI;
import org.junit.Test;

/**
 * Test Case for {@link ConfigUtil}
 * 
 * @author Roberto E. Escobar
 */
public class ConfigUtilTest {

   @Test
   public void testAsUri1() {
      URI uri = ConfigUtil.asUri("y/x\\abcd");
      String value = uri.toASCIIString();
      assertEquals(String.format("starts with [file://] was [%s]", value), true, value.contains("file:/"));
      assertEquals(String.format("ends with [y/x/abcd] was [%s]", value), true, value.endsWith("y/x/abcd"));
   }

   @Test
   public void testAsUri2() {
      URI uri = ConfigUtil.asUri("http://x/abcd");
      String value = uri.toASCIIString();
      assertEquals("http://x/abcd", value);
   }

   @Test
   public void testAsUri3() {
      URI uri = ConfigUtil.asUri("other://x/abcd");
      String value = uri.toASCIIString();
      assertEquals("other://x/abcd", value);
   }

   @Test
   public void testAsUri4() {
      URI uri = ConfigUtil.asUri("file:/x/abcd");
      String value = uri.toASCIIString();
      assertEquals("file:/x/abcd", value);
   }
}
