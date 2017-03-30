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

import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ReviewBlockType}
 *
 * @author Donald G. Dunne
 */
public class ReviewBlockTypeTest {

   @Test
   public void testValues() {
      Assert.assertEquals(3, ReviewBlockType.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(ReviewBlockType.Commit, ReviewBlockType.valueOf(ReviewBlockType.Commit.name()));
   }

}
