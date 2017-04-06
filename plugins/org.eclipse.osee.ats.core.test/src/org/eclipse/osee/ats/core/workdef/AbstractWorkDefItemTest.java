/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.model.AbstractWorkDefItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link AbstractWorkDefItem}
 *
 * @author Donald G. Dunne
 */
public class AbstractWorkDefItemTest {

   @Test
   public void testToString() {
      AbstractWorkDefItem item = new AbstractWorkDefItem(15L, "name");
      Assert.assertEquals("name", item.toString());
      item.setName("that");
      Assert.assertEquals("that", item.toString());
   }

}
