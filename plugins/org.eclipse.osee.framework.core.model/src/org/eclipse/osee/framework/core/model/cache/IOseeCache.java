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
public interface IOseeCache<TYPE> {

   OseeCacheEnum getCacheId();

   int size();

   void decacheAll();

   void decache(TYPE... types) ;

   void decache(TYPE type) ;

   void cache(TYPE... types) ;

   void cache(TYPE type) ;

   Collection<TYPE> getAll() ;

   TYPE getById(Number typeId) ;

   TYPE getByGuid(Long guid) ;
}