/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Morgan E. Cook
 */
public class Id4JoinQuery extends AbstractJoinQuery {

   Id4JoinQuery(JdbcClient jdbcClient, JdbcConnection connection) {
      super(JoinItem.ID4, jdbcClient, connection);
   }

   public void add(Id id_1, Id id_2, Id id_3, Id id_4) {
      addToBatch(id_1, id_2, id_3, id_4);
   }

   public void add(Id id_1, Id id_2, Id id_3) {
      addToBatch(id_1, id_2, id_3, ArtifactId.SENTINEL);
   }

   public void add(Id id_1, Id id_2) {
      addToBatch(id_1, id_2, TransactionId.SENTINEL, ArtifactId.SENTINEL);
   }

   public void add(Long id_1, Long id_2, Long id_3) {
      addToBatch(id_1, id_2, id_3, ArtifactId.SENTINEL);
   }
}