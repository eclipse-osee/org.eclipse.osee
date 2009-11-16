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
package org.eclipse.osee.framework.database.core;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

public abstract class OseeConnection {

   protected OseeConnection() {

   }

   public abstract void close();

   public abstract boolean isClosed() throws OseeDataStoreException;

   public abstract boolean isStale();

   public abstract DatabaseMetaData getMetaData() throws OseeDataStoreException;

   protected abstract void setAutoCommit(boolean autoCommit) throws OseeDataStoreException;

   protected abstract boolean getAutoCommit() throws SQLException;

   protected abstract void commit() throws SQLException;

   protected abstract void rollback() throws OseeDataStoreException;

   protected abstract void destroy() throws OseeDataStoreException;
}