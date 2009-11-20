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
package org.eclipse.osee.framework.database;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationDatabaseManager {

   public IDatabaseInfoProvider getProvider() throws OseeDataStoreException;

   public void removeDatabaseProvider(IDatabaseInfoProvider provider);

   public void addDatabaseProvider(IDatabaseInfoProvider provider);
}
