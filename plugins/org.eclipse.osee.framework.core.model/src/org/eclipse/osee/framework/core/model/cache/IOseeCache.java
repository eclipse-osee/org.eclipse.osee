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
package org.eclipse.osee.framework.core.model.cache;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCache<KEY, TYPE> {

   OseeCacheEnum getCacheId();

   int size();

   long getLastLoaded();

   boolean reloadCache() throws OseeCoreException;

   void decacheAll();

   void decache(TYPE... types) throws OseeCoreException;

   void decache(TYPE type) throws OseeCoreException;

   void cache(TYPE... types) throws OseeCoreException;

   void cache(TYPE type) throws OseeCoreException;

   Collection<TYPE> getAll() throws OseeCoreException;

   TYPE getById(Number typeId) throws OseeCoreException;

   TYPE getByGuid(KEY guid) throws OseeCoreException;

   Collection<TYPE> getAllDirty() throws OseeCoreException;

   void storeAllModified() throws OseeCoreException;

   void ensurePopulated() throws OseeCoreException;

   void storeItems(TYPE... items) throws OseeCoreException;

   void storeItems(Collection<TYPE> toStore) throws OseeCoreException;
}
