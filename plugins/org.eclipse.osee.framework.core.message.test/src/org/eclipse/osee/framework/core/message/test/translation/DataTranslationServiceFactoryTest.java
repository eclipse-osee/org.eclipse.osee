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
package org.eclipse.osee.framework.core.message.test.translation;

import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.internal.DataTranslationService;
import org.eclipse.osee.framework.core.message.internal.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.message.internal.translation.DatastoreInitRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TableDataTranslator;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.ITranslatorId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DataTranslationServiceFactory}
 * 
 * @author Roberto E. Escobar
 */
public class DataTranslationServiceFactoryTest {

   @Test
   public void testServiceCreation() throws OseeCoreException {
      DataTranslationService srvc = new DataTranslationService();
      srvc.start();

      checkExists(srvc, TableDataTranslator.class, CoreTranslatorId.TABLE_DATA);

      checkExists(srvc, DatastoreInitRequestTranslator.class, CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);

      srvc.stop();
   }

   private void checkExists(IDataTranslationService service, Class<? extends ITranslator<?>> expected, ITranslatorId key) throws OseeCoreException {
      ITranslator<?> actual = service.getTranslator(key);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual.getClass());
   }

}
