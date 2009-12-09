/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.cache;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCache<T> {

   OseeCacheEnum getCacheId();

   int size();

   void reloadCache() throws OseeCoreException;

   void decacheAll();

   void decache(T... types) throws OseeCoreException;

   void decache(T type) throws OseeCoreException;

   void cache(T... types) throws OseeCoreException;

   void cache(T type) throws OseeCoreException;

   Collection<T> getAll() throws OseeCoreException;

   T getById(int typeId) throws OseeCoreException;

   Collection<T> getAllDirty() throws OseeCoreException;

   void storeAllModified() throws OseeCoreException;

   void ensurePopulated() throws OseeCoreException;

   void storeItems(T... items) throws OseeCoreException;

   void storeItems(Collection<T> toStore) throws OseeCoreException;
}
