/*********************************************************************
 * Copyright (c) 2009 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.cache;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeCache<TYPE> {

   int size();

   void decacheAll();

   void decache(TYPE... types);

   void decache(TYPE type);

   void cache(TYPE... types);

   void cache(TYPE type);

   Collection<TYPE> getAll();

   TYPE getById(Number typeId);

   TYPE getByGuid(Long guid);

   void decacheById(Id id);
}