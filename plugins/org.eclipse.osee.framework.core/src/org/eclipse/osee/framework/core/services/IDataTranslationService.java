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
package org.eclipse.osee.framework.core.services;

import java.io.InputStream;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public interface IDataTranslationService {

   /**
    * Converts an input stream into the specified object
    * 
    * @param inputStream containing object
    * @param toMatch class to convert object into
    * @return transformed object
    * @throws OseeCoreException if there are problems during conversion
    */
   public abstract <T> T convert(InputStream inputStream, ITranslatorId toMatch) throws OseeCoreException;

   /**
    * Converts a property store into a specified object
    * 
    * @param propertyStore representing object
    * @param toMatch class to convert object into
    * @return transformed object
    * @throws OseeCoreException if there are problems during conversion
    */
   public abstract <T> T convert(PropertyStore propertyStore, ITranslatorId toMatch) throws OseeCoreException;

   /**
    * Converts an object into a property store
    * 
    * @param object to transform
    * @return property store representation of the source object
    * @throws OseeCoreException if there are problems during conversion
    */
   public abstract <T> PropertyStore convert(T object, ITranslatorId toMatch) throws OseeCoreException;

   /**
    * Converts an object into an input stream
    * 
    * @param object to transform
    * @return input stream representation of the source object
    * @throws OseeCoreException if there are problems during conversion
    */
   public abstract <T> InputStream convertToStream(T object, ITranslatorId toMatch) throws OseeCoreException;

   /**
    * Gets the translator matching the class
    * 
    * @param toMatch key to obtain translator
    * @return translator associated with that class
    * @throws OseeCoreException if there are problems getting the translator or class does not have a registered
    *            translator
    */
   public abstract ITranslator<?> getTranslator(ITranslatorId toMatch) throws OseeCoreException;

   /**
    * registers a translator for the specified class
    * 
    * @param clazz to register translator for
    * @param translator to register
    * @return <b>true</b> if translator was added successfully
    */
   public abstract boolean addTranslator(ITranslator<?> translator, ITranslatorId toMatch) throws OseeCoreException;

   /**
    * removes a translator for the specified class
    * 
    * @param clazz associated with the translator to remove
    * @return <b>true</b> if the translator associated with the class was removed
    */
   public abstract boolean removeTranslator(ITranslatorId toMatch) throws OseeCoreException;

   /**
    * Get all translator identifiers registered
    * 
    * @return translator identifiers with registered translators
    */
   public abstract Collection<ITranslatorId> getSupportedClasses();

}