/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.resource.management.test;

import org.eclipse.osee.framework.jdk.core.test.type.PropertyStoreTest;
import org.eclipse.osee.framework.resource.management.Options;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Cases for {@link Options}
 * 
 * @author Roberto E. Escobar
 */
public class OptionsTest extends PropertyStoreTest {

   @Test
   public void testClear() {
      Options options = new Options();
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
