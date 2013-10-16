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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface DataProxy {

   public void setResolver(ResourceNameResolver resolver);

   public ResourceNameResolver getResolver();

   public String getDisplayableString() throws OseeCoreException;

   public void setDisplayableString(String toDisplay) throws OseeDataStoreException;

   public void setData(Object... objects) throws OseeCoreException;

   public Object[] getData() throws OseeCoreException;

   public void persist(long storageId) throws OseeCoreException;

   public void purge() throws OseeCoreException;

   boolean isInMemory();
}
