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
package org.eclipse.osee.framework.database.test.mocks;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;
import org.eclipse.osee.framework.database.core.IJoinAccessor;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Roberto E. Escobar
 */
public class MockJoinAccessor implements IJoinAccessor {

   private OseeConnection connection;
   private JoinItem joinItem;
   private int queryId;
   private List<Object[]> dataList;

   @SuppressWarnings("unused")
   @Override
   public void store(OseeConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList) throws OseeCoreException {
      this.connection = connection;
      this.joinItem = joinItem;
      this.queryId = queryId;
      this.dataList = dataList;
   }

   @SuppressWarnings("unused")
   @Override
   public int delete(OseeConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException {
      this.connection = connection;
      this.joinItem = joinItem;
      this.queryId = queryId;
      return 0;
   }

   @SuppressWarnings("unused")
   @Override
   public Collection<Integer> getAllQueryIds(OseeConnection connection, JoinItem joinItem) throws OseeCoreException {
      this.connection = connection;
      this.joinItem = joinItem;
      return null;
   }

   public void clear() {
      connection = null;
      joinItem = null;
      dataList = null;
      queryId = -1;
   }

   public OseeConnection getConnection() {
      return connection;
   }

   public JoinItem getJoinItem() {
      return joinItem;
   }

   public int getQueryId() {
      return queryId;
   }

   public List<Object[]> getDataList() {
      return dataList;
   }

}