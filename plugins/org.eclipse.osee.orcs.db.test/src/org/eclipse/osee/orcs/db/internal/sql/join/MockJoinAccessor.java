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
package org.eclipse.osee.orcs.db.internal.sql.join;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Roberto E. Escobar
 */
public class MockJoinAccessor implements IJoinAccessor {

   private JdbcConnection connection;
   private JoinItem joinItem;
   private int queryId;
   private List<Object[]> dataList;

   @Override
   public void store(JdbcConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList, Long issuedAt, Long expiresIn) throws OseeCoreException {
      this.connection = connection;
      this.joinItem = joinItem;
      this.queryId = queryId;
      this.dataList = dataList;
   }

   @Override
   public int delete(JdbcConnection connection, JoinItem joinItem, int queryId) {
      this.connection = connection;
      this.joinItem = joinItem;
      this.queryId = queryId;
      return 0;
   }

   @Override
   public Collection<Integer> getAllQueryIds(JdbcConnection connection, JoinItem joinItem) {
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

   public JdbcConnection getConnection() {
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