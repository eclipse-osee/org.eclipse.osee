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
package org.eclipse.osee.orcs.db.internal.proxy;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.db.mocks.MockResourceLocatorManager;
import org.eclipse.osee.orcs.db.mocks.MockResourceManager;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link ResourceHandler}
 * 
 * @author Roberto E. Escobar
 */
public class ResourceHandlerTest {

   @Test
   @Ignore
   public void testAcquire() throws OseeCoreException {
      Assert.assertEquals(1, 1);

      DataResource resource = new DataResource();

      MockResourceManager resMgr = new MockResourceManager();
      MockResourceLocatorManager locMgr = new MockResourceLocatorManager();
      ResourceHandler handler = new ResourceHandler(resMgr, locMgr);

      byte[] data = handler.acquire(resource);
   }

   @Test
   @Ignore
   public void testSave() {
      Assert.assertEquals(1, 1);

      //      save(int, DataResource, byte[])
   }

   @Test
   @Ignore
   public void testDelete() {
      Assert.assertEquals(1, 1);

      //      save(int, DataResource, byte[])
   }

}
