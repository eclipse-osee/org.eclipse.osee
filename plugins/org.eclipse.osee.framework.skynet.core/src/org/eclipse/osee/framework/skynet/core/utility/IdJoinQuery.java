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
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Roberto E. Escobar
 */
public class IdJoinQuery extends AbstractJoinQuery {

   public IdJoinQuery(JdbcClient jdbcClient, JdbcConnection connection) {
      super(JoinItem.ID, jdbcClient, connection);
   }

   public void add(Long id) {
      addToBatch(id);
   }

   public void add(Id id) {
      addToBatch(id);
   }
}