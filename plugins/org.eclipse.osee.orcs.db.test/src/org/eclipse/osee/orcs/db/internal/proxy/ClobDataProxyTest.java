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
import org.eclipse.osee.orcs.db.mock.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockDataHandler;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link ClobDataProxy}
 * 
 * @author Roberto E. Escobar
 */
public class ClobDataProxyTest {

   @Test
   @Ignore
   public void testSetDisplay() {

   }

   @Test
   @Ignore
   public void test() throws OseeCoreException {
      Assert.assertEquals(1, 1);

      DataHandler handler = new MockDataHandler();

      ClobDataProxy proxy = new ClobDataProxy();
      proxy.setLogger(new MockLog());
      proxy.setStorage(new Storage(handler));

      String toDisplay = "Hello";
      proxy.setDisplayableString(toDisplay);
      String todisplay = proxy.getDisplayableString();
      Assert.assertEquals("Hello", todisplay);
   }

   // getData()
   // setData(Object...)
   //
   // getValueAsString()
   // persist(int)
   // purge()
   // 
   // setValue(String)

}
