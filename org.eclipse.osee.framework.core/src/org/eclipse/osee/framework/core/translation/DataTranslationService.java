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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class DataTranslationService implements IDataTranslationService {

   private final Map<ITranslatorId, ITranslator<?>> translators;

   public DataTranslationService() {
      this.translators = new HashMap<ITranslatorId, ITranslator<?>>();
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T convert(PropertyStore propertyStore, ITranslatorId txId) throws OseeCoreException {
      Conditions.checkNotNull(txId, "translator Id");

      T object = null;
      if ((propertyStore != null) && !propertyStore.isEmpty()) {
         ITranslator<?> translator = getTranslator(txId);
         object = (T) translator.convert(propertyStore);
      }
      return object;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> PropertyStore convert(T object, ITranslatorId txId) throws OseeCoreException {
      PropertyStore propertyStore = null;
      if (object == null) {
         propertyStore = new PropertyStore();
      } else {
         ITranslator<T> translator = (ITranslator<T>) getTranslator(txId);
         propertyStore = translator.convert(object);
      }
      return propertyStore;
   }

   @Override
   public ITranslator<?> getTranslator(ITranslatorId txId) throws OseeCoreException {
      Conditions.checkNotNull(txId, "translator Id");
      ITranslator<?> toReturn = translators.get(txId);
      if (toReturn == null) {
         throw new OseeStateException(String.format("Unable to find a match for translator id [%s]", txId));
      }
      return toReturn;

   }

   @Override
   public boolean addTranslator(ITranslator<?> translator, ITranslatorId txId) throws OseeCoreException {
      Conditions.checkNotNull(txId, "translator Id");
      Conditions.checkNotNull(translator, "translator");
      boolean wasAdded = false;
      if (!translators.containsKey(txId)) {
         translators.put(txId, translator);
         wasAdded = true;
      }
      return wasAdded;
   }

   @Override
   public boolean removeTranslator(ITranslatorId txId) throws OseeCoreException {
      Conditions.checkNotNull(txId, "translator Id");
      return translators.remove(txId) != null;
   }

   @Override
   public Collection<ITranslatorId> getSupportedClasses() {
      return new HashSet<ITranslatorId>(translators.keySet());
   }

   @Override
   public <T> T convert(InputStream inputStream, ITranslatorId txId) throws OseeCoreException {
      Conditions.checkNotNull(inputStream, "inputStream");
      Conditions.checkNotNull(txId, "translator Id");

      PropertyStore propertyStore = new PropertyStore();
      try {
         propertyStore.load(inputStream);
         return convert(propertyStore, txId);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public <T> InputStream convertToStream(T object, ITranslatorId txId) throws OseeCoreException {
      PropertyStore propertyStore = convert(object, txId);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      try {
         propertyStore.save(buffer);
         return new ByteArrayInputStream(buffer.toByteArray());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
