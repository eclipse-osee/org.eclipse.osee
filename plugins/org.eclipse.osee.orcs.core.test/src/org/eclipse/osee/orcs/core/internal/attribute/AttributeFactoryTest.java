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
package org.eclipse.osee.orcs.core.internal.attribute;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.mocks.MockLog;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link AttributeFactory}
 * 
 * @author Roberto E. Escobar
 */
public class AttributeFactoryTest {

   @Test
   @Ignore
   public void test() throws OseeCoreException {
      Log logger = new MockLog();
      AttributeTypeCache cache = null;
      AttributeClassResolver resolver = null;
      AttributeFactory factory = new AttributeFactory(logger, resolver, cache);

      AttributeContainer container = null;

      AttributeRow row = new AttributeRow();
      factory.loadAttribute(container, row);
   }
}
