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
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class OseeImportModelRequestTranslator implements ITranslator<OseeImportModelRequest> {

   private enum Fields {
      PERSIST,
      GENERATE_EMF_COMPARE,
      GENERATE_DIRTY_REPORT,
      MODEL_NAME,
      MODEL;
   }

   @Override
   public OseeImportModelRequest convert(PropertyStore store) throws OseeCoreException {
      String model = store.get(Fields.MODEL.name());
      String modelName = store.get(Fields.MODEL_NAME.name());
      boolean createTypeChangeReport = store.getBoolean(Fields.GENERATE_DIRTY_REPORT.name());
      boolean createCompareReport = store.getBoolean(Fields.GENERATE_EMF_COMPARE.name());
      boolean isPersistAllowed = store.getBoolean(Fields.PERSIST.name());

      return new OseeImportModelRequest(modelName, model, createTypeChangeReport, createCompareReport, isPersistAllowed);
   }

   @Override
   public PropertyStore convert(OseeImportModelRequest object) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      store.put(Fields.MODEL_NAME.name(), object.getModelName());
      store.put(Fields.MODEL.name(), object.getModel());
      store.put(Fields.GENERATE_DIRTY_REPORT.name(), object.isCreateTypeChangeReport());
      store.put(Fields.GENERATE_EMF_COMPARE.name(), object.isCreateCompareReport());
      store.put(Fields.PERSIST.name(), object.isPersistAllowed());
      return store;
   }
}
