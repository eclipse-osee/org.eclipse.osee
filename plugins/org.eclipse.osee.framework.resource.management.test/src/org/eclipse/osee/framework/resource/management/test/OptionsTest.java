/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.resource.management.test;

import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Cases for {@link PropertyStore}
 * 
 * @author Roberto E. Escobar
 */
public class OptionsTest {

   @Test
   public void testClear() {
      PropertyStore options = new PropertyStore();
      options.put("a", true);
      options.put("b", 0.1);
      options.put("c", new String[] {"a", "b", "c"});
      Assert.assertEquals(1, options.arrayKeySet().size());
      Assert.assertEquals(2, options.keySet().size());

      options.clear();
      Assert.assertEquals(0, options.arrayKeySet().size());
      Assert.assertEquals(0, options.keySet().size());
   }
}
