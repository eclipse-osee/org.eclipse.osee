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
package org.eclipse.osee.orcs.db.mocks;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.ResourceNameResolver;

/**
 * @author Roberto E. Escobar
 */
public class MockDataProxy implements DataProxy {

   @Override
   public void setResolver(ResourceNameResolver resolver) {
      //
   }

   @Override
   public ResourceNameResolver getResolver() {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public String getDisplayableString() throws OseeCoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public void setDisplayableString(String toDisplay) throws OseeDataStoreException {
      //
   }

   @SuppressWarnings("unused")
   @Override
   public void setData(Object... objects) throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   @Override
   public Object[] getData() throws OseeDataStoreException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public void persist(long storageId) throws OseeCoreException {
      //
   }

   @SuppressWarnings("unused")
   @Override
   public void purge() throws OseeCoreException {
      //
   }

}