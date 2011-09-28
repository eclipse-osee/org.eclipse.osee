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
package org.eclipse.osee.orcs.core.internal.attribute;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.AttributeRow;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.internal.SessionContext;

/**
 * @author Roberto E. Escobar
 */
public class AttributeRowMapper implements AttributeRowHandler {

   private final AttributeFactory factory;
   private final SessionContext context;
   private final Log logger;

   public AttributeRowMapper(Log logger, SessionContext context, AttributeFactory factory) {
      this.logger = logger;
      this.context = context;
      this.factory = factory;
   }

   private AttributeContainer<?> getContainer(AttributeRow current) {
      AttributeContainer<?> container = null;
      if (current.isHistorical()) {
         container = context.getHistorical(current.getArtifactId(), current.getStripeId());
      } else {
         container = context.getActive(current.getArtifactId(), current.getBranchId());
      }
      if (container == null) {
         logger.warn("Orphaned attribute detected - [%s]", current);
      }
      return container;
   }

   @Override
   public void onRow(List<AttributeRow> rows) throws OseeCoreException {
      AttributeRow firstRow = rows.iterator().next();
      AttributeContainer<?> container = getContainer(firstRow);
      if (container == null) {
         return; // If the artifact is null, it means the attributes are orphaned.
      }
      AttributeRow previous = new AttributeRow();
      synchronized (container) {
         if (!container.isLoaded()) {
            //            int maxTransactionId = Integer.MIN_VALUE;
            for (AttributeRow current : rows) {
               if (previous.isSameAttribute(current)) {
                  handleMultipleVersions(previous, current);
               } else {
                  factory.loadAttribute(container, current);
               }
               previous = current;
               //               maxTransactionId = Math.max(maxTransactionId, current.getTransactionId());
            }
            //            getContainer().setTransactionId(maxTransactionId);
            container.setLoaded(true);
         }
      }
   }

   private void handleMultipleVersions(AttributeRow previous, AttributeRow current) {
      // Do not warn about skipping on historical loading, because the most recent
      // transaction is used first due to sorting on the query
      if (!previous.isHistorical() && !current.isHistorical()) {
         logger.warn("Multiple attribute versions detected - \n\t[%s]\n\t[%s]", current, previous);
      }
   }
}
