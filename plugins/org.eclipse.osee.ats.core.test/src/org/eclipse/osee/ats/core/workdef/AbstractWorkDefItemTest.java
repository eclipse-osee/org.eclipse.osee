/*********************************************************************
 * Copyright (c) 2013 Boeing
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
