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
package org.eclipse.osee.framework.core.test.exchange;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BasicArtifactDataTranslator;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.junit.Test;

/**
 * Test Case For {@link BasicArtifactDataTranslator}
 * 
 * @author Roberto E. Escobar
 */
public abstract class BaseTranslatorTest<T> {

   private final IDataTranslator<T> translator;
   private final T data;

   protected BaseTranslatorTest(T data, IDataTranslator<T> translator) {
      this.data = data;
      this.translator = translator;
   }

   @Test
   public void testTranslation() throws OseeCoreException {
      PropertyStore propertyStore = translator.convert(data);
      T actual = translator.convert(propertyStore);

      Assert.assertNotSame(data, actual);
      checkEquals(data, actual);
   }

   protected abstract void checkEquals(T expected, T actual) throws OseeCoreException;

}
