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
package org.eclipse.osee.framework.core;

import java.io.InputStream;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Roberto E. Escobar
 */
public interface IDataTranslationService {

   public abstract <T> T convert(InputStream inputStream, Class<T> toMatch) throws OseeCoreException;

   public abstract <T> T convert(PropertyStore propertyStore, Class<T> toMatch) throws OseeCoreException;

   public abstract <T> PropertyStore convert(T object) throws OseeCoreException;

   public abstract <T> InputStream convertToStream(T object) throws OseeCoreException;

   public abstract IDataTranslator<?> getTranslator(Class<?> toMatch) throws OseeCoreException;

   public abstract boolean addTranslator(Class<?> clazz, IDataTranslator<?> translator);

   public abstract boolean removeTranslator(Class<?> clazz);

   public abstract Collection<Class<?>> getSupportedClasses();

}