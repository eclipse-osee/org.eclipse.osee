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

import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Jeff C. Phillips
 */
public class ChangeVersionTranslator implements IDataTranslator<ChangeVersion> {
   private enum Entry {
      GAMMA_ID,
      MOD_TYPE,
      VALUE,
      TRANSACTION_NUMBER;
   }

   public ChangeVersionTranslator() {
      super();
   }

   @Override
   public ChangeVersion convert(PropertyStore propertyStore) throws OseeCoreException {
      String value = propertyStore.get(Entry.VALUE.name());
      ModificationType modificationType = ModificationType.getMod(Integer.parseInt(propertyStore.get(Entry.MOD_TYPE.name())));
      Long gammaId = Long.parseLong(propertyStore.get(Entry.GAMMA_ID.name()));
      Integer transactionNumber = Integer.parseInt(propertyStore.get(Entry.TRANSACTION_NUMBER.name()));
      
      return new ChangeVersion(value, gammaId, modificationType, transactionNumber);
   }

   @Override
   public PropertyStore convert(ChangeVersion changeVersion) throws OseeCoreException {
      PropertyStore store = new PropertyStore();
      
      store.put(Entry.GAMMA_ID.name(), changeVersion.getGammaId());
      store.put(Entry.MOD_TYPE.name(), changeVersion.getModType().getValue());
      store.put(Entry.VALUE.name(), changeVersion.getValue());
      store.put(Entry.TRANSACTION_NUMBER.name(), changeVersion.getTransactionNumber());
      return store;
   }

}
