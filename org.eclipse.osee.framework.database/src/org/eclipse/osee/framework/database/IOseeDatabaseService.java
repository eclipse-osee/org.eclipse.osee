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

import java.util.List;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.IOseeSequence;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeDatabaseService {

   IOseeSequence getSequence();

   IOseeStatement getStatement() throws OseeDataStoreException;

   IOseeStatement getStatement(OseeConnection connection) throws OseeDataStoreException;

   IOseeStatement getStatement(OseeConnection connection, boolean autoClose) throws OseeDataStoreException;

   IOseeStatement getStatement(int resultSetType, int resultSetConcurrency) throws OseeDataStoreException;

   OseeConnection getConnection() throws OseeDataStoreException;

   OseeConnection getConnection(IDatabaseInfo info) throws OseeDataStoreException;

   <O extends Object> int runBatchUpdate(String query, List<O[]> dataList) throws OseeDataStoreException;

   <O extends Object> int runPreparedUpdate(String query, O... data) throws OseeDataStoreException;

   <O extends Object> int runBatchUpdate(OseeConnection connection, String query, List<O[]> dataList) throws OseeDataStoreException;

   <O extends Object> int runPreparedUpdate(OseeConnection connection, String query, O... data) throws OseeDataStoreException;

   <T, O extends Object> T runPreparedQueryFetchObject(T defaultValue, String query, O... data) throws OseeDataStoreException;

   <T, O extends Object> T runPreparedQueryFetchObject(OseeConnection connection, T defaultValue, String query, O... data) throws OseeDataStoreException;

}
