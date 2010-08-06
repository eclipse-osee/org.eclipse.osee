/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.util;

import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.test.mocks.MockTrackingHandler;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.junit.Test;

/**
 * Test Case for {@link AbstractTrackingHandler}
 * 
 * @author Roberto E. Escobar
 */
public class AbstractTrackingHandlerTest {

   @Test
   public void testDefaultGetConfiguredDependencies() {
      MockTrackingHandler handler = new MockTrackingHandler(null, String.class, Integer.class);
      Map<Class<?>, ServiceBindType> actualDeps = handler.getConfiguredDependencies();
      Assert.assertEquals(2, actualDeps.size());
      Assert.assertEquals(ServiceBindType.SINGLETON, actualDeps.get(String.class));
      Assert.assertEquals(ServiceBindType.SINGLETON, actualDeps.get(Integer.class));
   }
}
