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
package org.eclipse.osee.framework.core.exchange;

import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeItemTranslator implements IDataTranslator<ChangeItem> {
   private enum Entry {
   }

   private final IDataTranslationService service;
   
   public ChangeItemTranslator(IDataTranslationService service) {
      super();
      this.service = service;
   }

   @Override
   public ChangeItem convert(PropertyStore propertyStore) throws OseeCoreException {
      return null;
   }

   @Override
   public PropertyStore convert(ChangeItem changeItem) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      return store;
   }

}
