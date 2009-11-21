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

   public OseeCacheEnum getCacheId();

   public int size();

   public void decache(T... types) throws OseeCoreException;

   public void decache(T type) throws OseeCoreException;

   public void cache(T... types) throws OseeCoreException;

   public void cache(T type) throws OseeCoreException;

   public Collection<T> getAll() throws OseeCoreException;

   public T getById(int typeId) throws OseeCoreException;

   public Collection<T> getAllDirty() throws OseeCoreException;

   public void storeAllModified() throws OseeCoreException;

   public void ensurePopulated() throws OseeCoreException;

   public void storeItems(T... items) throws OseeCoreException;

   public void storeItems(Collection<T> toStore) throws OseeCoreException;
}
