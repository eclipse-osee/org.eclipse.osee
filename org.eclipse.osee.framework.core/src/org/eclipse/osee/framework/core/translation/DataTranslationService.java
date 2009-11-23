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
import java.util.ArrayList;
import java.util.Arrays;
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

   private final Map<ClassKey, ITranslator<?>> translators;

   public DataTranslationService() {
      this.translators = new HashMap<ClassKey, ITranslator<?>>();
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T convert(PropertyStore propertyStore, Class<T>... toMatch) throws OseeCoreException {
      Conditions.checkNotNull(toMatch, "class toMatch");
      Conditions.checkDoesNotContainNulls(toMatch, "toMatch cannot contain nulls");

      T object = null;
      if (propertyStore != null && !propertyStore.keySet().isEmpty()) {
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
   public ITranslator<?> getTranslator(Class<?>... toMatch) throws OseeCoreException {
      Conditions.checkNotNull(toMatch, "classes toMatch");
      Conditions.checkExpressionFailOnTrue(toMatch.length < 1, "classes toMatch cannot be empty");
      Conditions.checkDoesNotContainNulls(toMatch, "toMatch cannot contain nulls");

      for (Entry<ClassKey, ITranslator<?>> entry : translators.entrySet()) {
         ClassKey key = entry.getKey();
         Class<?>[] classes = key.classes;
         if (matches(classes, toMatch)) {
            return entry.getValue();
         }
      }
      throw new OseeStateException(String.format("Unable to translate [%s]", Arrays.deepToString(toMatch)));
   }

   private boolean matches(Class<?>[] key, Class<?>[] toMatch) {
      boolean result = false;
      if (key.length == toMatch.length) {
         result = true;
         for (int index = 0; index < key.length; index++) {
            result &= //key[index] == toMatch[index] || 
                  key[index].isAssignableFrom(toMatch[index]);
            if (!result) {
               break;
            }
         }
      }
      return result;
   }

   @Override
   public boolean addTranslator(ITranslator<?> translator, Class<?>... classes) throws OseeCoreException {
      Conditions.checkNotNull(classes, "classes");
      Conditions.checkNotNull(translator, "translator");
      Conditions.checkExpressionFailOnTrue(classes.length < 1, "classes cannot be empty");
      Conditions.checkDoesNotContainNulls(classes, "classes cannot contain nulls");

      boolean wasAdded = false;
      ClassKey key = new ClassKey(classes);
      if (!translators.containsKey(key)) {
         translators.put(key, translator);
         wasAdded = true;
      }
      return wasAdded;
   }

   @Override
   public boolean removeTranslator(Class<?>... classes) throws OseeCoreException {
      Conditions.checkNotNull(classes, "classes");
      Conditions.checkExpressionFailOnTrue(classes.length < 1, "classes cannot be empty");
      Conditions.checkDoesNotContainNulls(classes, "classes cannot contain nulls");

      ClassKey key = new ClassKey(classes);
      return translators.remove(key) != null;
   }

   @Override
   public Collection<Class<?>[]> getSupportedClasses() {
      Collection<Class<?>[]> keys = new ArrayList<Class<?>[]>();
      for (ClassKey key : translators.keySet()) {
         keys.add(key.classes);
      }
      return keys;
   }

   @Override
   public <T> T convert(InputStream inputStream, Class<T>... toMatch) throws OseeCoreException {
      Conditions.checkNotNull(inputStream, "inputStream");
      Conditions.checkNotNull(toMatch, "class toMatch");
      Conditions.checkDoesNotContainNulls(toMatch, "toMatch cannot contain nulls");

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

   private class ClassKey {
      protected final Class<?>[] classes;

      public ClassKey(Class<?>... classes) {
         this.classes = classes;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + Arrays.hashCode(classes);
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         ClassKey other = (ClassKey) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (!Arrays.equals(classes, other.classes)) {
            return false;
         }
         return true;
      }

      private DataTranslationService getOuterType() {
         return DataTranslationService.this;
      }

   }
}
