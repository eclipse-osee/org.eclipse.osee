/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.db.internal.health;

import org.eclipse.osee.framework.core.exception.RelTableInvalidException;
import org.eclipse.osee.jdbc.JdbcClient;

/**
 * @author Luciano T. Vaglienti
 */
public class Rel2HealthOperation {

   public Rel2HealthOperation(JdbcClient jdbcClient) {
      int count = jdbcClient.fetch(0,
         "select count(*) from (select distinct rel_type, a_art_id, rel_order, cnt\n" + "from\n" + "(select rel_type, a_art_id, b_art_id, rel_order, count('x') over (partition by rel_type, a_art_id, rel_order) cnt\n" + "from osee_relation rel where rel_order > 0) t1\n" + "where cnt > 1) t2");
      if (count > 0) {
         throw new RelTableInvalidException("Invalid relations detected. Count: %s", count);
      }
   }

}
