/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Roberto E. Escobar
 */
public interface IdentityCollection<K, T extends Identity<K>> {

   Collection<? extends T> getAll() throws OseeCoreException;

   T getByUuid(K typeId) throws OseeCoreException;

   boolean exists(T item) throws OseeCoreException;

   boolean isEmpty() throws OseeCoreException;

   int size() throws OseeCoreException;

}