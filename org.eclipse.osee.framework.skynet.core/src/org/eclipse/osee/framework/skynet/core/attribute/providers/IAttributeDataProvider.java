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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IAttributeDataProvider {

   public String getDisplayableString() throws OseeDataStoreException;

   public void setDisplayableString(String toDisplay) throws OseeDataStoreException;

   public void loadData(Object... objects) throws OseeDataStoreException;

   public Object[] getData() throws OseeDataStoreException;

   public void persist() throws OseeDataStoreException;

   public void purge() throws OseeDataStoreException;
}
