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
package org.eclipse.osee.framework.core.message.internal;

import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.internal.translation.DatastoreInitRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TableDataTranslator;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class DataTranslationServiceFactory {

   public DataTranslationServiceFactory() {
      //
   }

   public void configureService(IDataTranslationService service, AttributeTypeFactory attributeTypeFactory) throws OseeCoreException {
      service.addTranslator(new OseeImportModelRequestTranslator(), CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);
      service.addTranslator(new OseeImportModelResponseTranslator(service), CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);
      service.addTranslator(new TableDataTranslator(), CoreTranslatorId.TABLE_DATA);

      service.addTranslator(new DatastoreInitRequestTranslator(), CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);
   }
}
