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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class DataTranslationService implements IDataTranslationService {

   private final Map<Class<?>, ITranslator<?>> translators;

   public DataTranslationService() {
      this.translators = new HashMap<Class<?>, ITranslator<?>>();
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T convert(PropertyStore propertyStore, Class<T> toMatch) throws OseeCoreException {
      Conditions.checkNotNull(toMatch, "class toMatch");
      T object = null;
      if (propertyStore != null) {
         ITranslator<?> translator = getTranslator(toMatch);
         object = (T) translator.convert(propertyStore);
      }
      return object;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> PropertyStore convert(T object) throws OseeCoreException {
      PropertyStore propertyStore = null;
      if (object == null) {
         propertyStore = new PropertyStore();
      } else {
         ITranslator<T> translator = (ITranslator<T>) getTranslator(object.getClass());
         propertyStore = translator.convert(object);
      }
      return propertyStore;
   }

   @Override
   public ITranslator<?> getTranslator(Class<?> toMatch) throws OseeCoreException {
      Conditions.checkNotNull(toMatch, "class toMatch");
      for (Entry<Class<?>, ITranslator<?>> entry : translators.entrySet()) {
         Class<?> acceptedClass = entry.getKey();
         if (acceptedClass.isAssignableFrom(toMatch)) {
            return entry.getValue();
         }
      }
      throw new OseeStateException(String.format("Unable to translate [%s]", toMatch.getName()));
   }

   @Override
   public boolean addTranslator(Class<?> clazz, ITranslator<?> translator) {
      boolean wasAdded = false;
      if (!translators.containsKey(clazz)) {
         translators.put(clazz, translator);
         wasAdded = true;
      }
      return wasAdded;
   }

   @Override
   public boolean removeTranslator(Class<?> clazz) {
      return translators.remove(clazz) != null;
   }

   @Override
   public Collection<Class<?>> getSupportedClasses() {
      return translators.keySet();
   }

   @Override
   public <T> T convert(InputStream inputStream, Class<T> toMatch) throws OseeCoreException {
      Conditions.checkNotNull(inputStream, "inputStream");
      Conditions.checkNotNull(toMatch, "class toMatch");
      PropertyStore propertyStore = new PropertyStore();
      try {
         propertyStore.load(inputStream);
         return convert(propertyStore, toMatch);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public <T> InputStream convertToStream(T object) throws OseeCoreException {
      PropertyStore propertyStore = convert(object);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      try {
         propertyStore.save(buffer);
         return new ByteArrayInputStream(buffer.toByteArray());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
