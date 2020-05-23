/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class LibTest {

   @Test
   public void testGetExtension() {
      assertEquals("", Lib.getExtension(null));
      assertEquals("", Lib.getExtension(""));
      assertEquals("", Lib.getExtension("hello"));
      assertEquals("", Lib.getExtension("my/path"));
      assertEquals("", Lib.getExtension("my\\path"));
      assertEquals("txt", Lib.getExtension("my.txt"));
      assertEquals("txt", Lib.getExtension("my/path.txt"));
      assertEquals("txt", Lib.getExtension("my\\path\\is\\here.txt"));
      assertEquals("txt", Lib.getExtension("my/path\\is/here.txt"));
      assertEquals("txt", Lib.getExtension("my/path\\is/here.txt"));
      assertEquals("lib", Lib.getExtension("my/path.txt\\is/here.lib"));
      assertEquals("a", Lib.getExtension("my/path.blah\\is/here.a"));
   }
}
