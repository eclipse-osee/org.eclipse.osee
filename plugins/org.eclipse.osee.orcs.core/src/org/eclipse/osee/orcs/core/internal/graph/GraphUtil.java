/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.graph;

import com.google.common.base.Objects;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public final class GraphUtil {

   private GraphUtil() {
      // Utility class
   }

   public static GraphProvider asProvider(final GraphData graph) {
      return new GraphProvider() {

         @Override
         public GraphData getGraph(OrcsSession session, Long branchId, TransactionId transactionId) throws OseeCoreException {
            Conditions.checkExpressionFailOnTrue(!Objects.equal(graph.getBranchId(), branchId),
               "Invalid branch - Graph's branch[%s] does not equals requested branch[%s]", graph.getBranchId(),
               branchId);
            Conditions.checkExpressionFailOnTrue(!Objects.equal(graph.getTransaction(), transactionId),
               "Invalid transactionId - Graph's transactionId[%s] does not equals requested transactionId[%s]",
               graph.getTransaction(), transactionId);
            return graph;
         }
      };
   }

}
