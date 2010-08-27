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
package org.eclipse.osee.framework.search.engine.test.attribute;

import junit.framework.Assert;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.junit.Test;

/**
 * Test Case for {@link AttributeData}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeDataTest {

   @Test
   public void testInvalidUriData() {
      AttributeData actual = new AttributeData(1, 4111, 2, "Hello", "myUri", 45);

      Assert.assertEquals(1, actual.getArtId());
      Assert.assertEquals(45, actual.getAttrTypeId());
      Assert.assertEquals(4111L, actual.getGammaId());
      Assert.assertEquals(2, actual.getBranchId());
      Assert.assertEquals("Hello", actual.getStringValue());
      Assert.assertEquals("myUri", actual.getUri());
      Assert.assertEquals(false, actual.isUriValid());
      Assert.assertEquals("artId:[1] branchId:[2] gammaId:[4111] uri:[myUri] attrTypeId:[45] isValidUri:[false]",
         actual.toString());
   }

   @Test
   public void testValidUriData() {
      AttributeData actual = new AttributeData(1, 4111, 2, "Hello", "attr://myUri", 45);

      Assert.assertEquals(1, actual.getArtId());
      Assert.assertEquals(45, actual.getAttrTypeId());
      Assert.assertEquals(4111L, actual.getGammaId());
      Assert.assertEquals(2, actual.getBranchId());
      Assert.assertEquals("Hello", actual.getStringValue());
      Assert.assertEquals("attr://myUri", actual.getUri());
      Assert.assertEquals(true, actual.isUriValid());
      Assert.assertEquals("artId:[1] branchId:[2] gammaId:[4111] uri:[attr://myUri] attrTypeId:[45] isValidUri:[true]",
         actual.toString());
   }
}
