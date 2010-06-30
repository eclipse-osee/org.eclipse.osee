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
package org.eclipse.osee.framework.access.test.internal;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AccessData}
 * 
 * @author Jeff C. Phillips
 */
public class AccessDataTest {

   @Test
   public void testObjectBase() {
      AccessData accessData = new AccessData();
      Assert.assertFalse(accessData.matches(PermissionEnum.READ));

      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
      accessData.add(basicArtifact, PermissionEnum.READ);

      Assert.assertTrue(accessData.matches(PermissionEnum.READ));
      Assert.assertFalse(accessData.matches(PermissionEnum.WRITE));
   }
}
