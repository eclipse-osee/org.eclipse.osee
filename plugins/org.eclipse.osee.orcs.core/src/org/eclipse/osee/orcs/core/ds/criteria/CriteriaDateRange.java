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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.sql.Timestamp;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaDateRange extends Criteria implements TxCriteria {

   private final Timestamp from, to;

   public CriteriaDateRange(Timestamp from, Timestamp to) {
      super();
      this.from = from;
      this.to = to;
   }

   @Override
   public void checkValid(Options options) {
      if (from.after(to)) {
         throw new OseeArgumentException("from date must be less than to date");
      }
   }

   public Timestamp getTo() {
      return to;
   }

   public Timestamp getFrom() {
      return from;
   }

   @Override
   public String toString() {
      return "CriteriaDateRange [from=" + from + "  to=" + to + "]";
   }
}
