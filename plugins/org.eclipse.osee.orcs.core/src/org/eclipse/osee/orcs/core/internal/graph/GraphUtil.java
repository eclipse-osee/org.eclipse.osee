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

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import com.google.common.base.Objects;

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
         public GraphData getGraph(OrcsSession session, IOseeBranch branch, int transactionId) throws OseeCoreException {
            Conditions.checkNotNull(branch, "branch", "Invalid branch - can't provide graph");
            Conditions.checkExpressionFailOnTrue(!Objects.equal(graph.getBranch(), branch),
               "Invalid branch - Graph's branch[%s] does not equals requested branch[%s]", graph.getBranch(), branch);
            Conditions.checkExpressionFailOnTrue(!Objects.equal(graph.getTransaction(), transactionId),
               "Invalid transactionId - Graph's transactionId[%s] does not equals requested transactionId[%s]",
               graph.getTransaction(), transactionId);
            return graph;
         }
      };
   }

}
