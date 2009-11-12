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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public class DataTranslationService implements IDataTranslationService {

   private final Map<Class<?>, IDataTranslator<?>> translators;

   public DataTranslationService() {
      this.translators = new HashMap<Class<?>, IDataTranslator<?>>();
   }

   /*
    * (non-Javadoc)
    * @see
    * org.eclipse.osee.framework.core.exchange.IDataTranslationService#convert(org.eclipse.osee.framework.jdk.core.type
    * .PropertyStore, java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public <T> T convert(PropertyStore propertyStore, Class<T> toMatch) throws OseeCoreException {
      IDataTranslator<?> translator = getTranslator(toMatch);
      return (T) translator.convert(propertyStore);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.exchange.IDataTranslationService#convert(T, java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public <T> PropertyStore convert(T object, Class<T> toMatch) throws OseeCoreException {
      IDataTranslator<T> translator = (IDataTranslator<T>) getTranslator(toMatch);
      return translator.convert(object);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.exchange.IDataTranslationService#getTranslator(java.lang.Class)
    */
   public IDataTranslator<?> getTranslator(Class<?> toMatch) throws OseeCoreException {
      IDataTranslator<?> translator = translators.get(toMatch);
      if (translator == null) {
         throw new OseeStateException(String.format("Unable to translate [%s]", toMatch.getName()));
      }
      return translator;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.exchange.IDataTranslationService#addTranslator(java.lang.Class,
    * org.eclipse.osee.framework.core.exchange.IDataTranslator)
    */
   public boolean addTranslator(Class<?> clazz, IDataTranslator<?> translator) {
      boolean wasAdded = false;
      if (!translators.containsKey(clazz)) {
         translators.put(clazz, translator);
         wasAdded = true;
      }
      return wasAdded;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.exchange.IDataTranslationService#removeTranslator(java.lang.Class)
    */
   public boolean removeTranslator(Class<?> clazz) {
      return translators.remove(clazz) != null;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.exchange.IDataTranslationService#getSupportedClasses()
    */
   public Collection<Class<?>> getSupportedClasses() {
      return translators.keySet();
   }
}
