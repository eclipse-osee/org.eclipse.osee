/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.branch.graph.utility;

/**
 * @author Roberto E. Escobar
 */
public class GraphOptions {

   public final static String FILTER_CONNECTIONS_PREFERENCE = "revision.graph.filter.connections";
   public final static String TRANSACTION_FILTER = "revision.graph.show.all.transactions";

   public static enum ConnectionFilter {
      NO_FILTER,
      FILTER_ALL_CONNECTIONS,
      FILTER_CHILD_BRANCH_CONNECTIONS,
      FILTER_MERGE_CONNECTIONS;
   }

   public static enum TxFilter {
      NO_FILTER,
      HIDE_ALL;
   }

   private GraphOptions() {
   }
}
