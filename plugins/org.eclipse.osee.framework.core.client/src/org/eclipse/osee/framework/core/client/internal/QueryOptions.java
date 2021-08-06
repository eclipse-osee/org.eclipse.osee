/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.client.internal;

/**
 * @author Roberto E. Escobar
 */
public class QueryOptions extends Options {

   public QueryOptions() {
      super();
   }

   @Override
   public void reset() {
      super.reset();
   }

   @Override
   public QueryOptions clone() {
      QueryOptions clone = new QueryOptions();
      clone.setIncludeDeleted(this.areDeletedIncluded());
      clone.setFromTransaction(this.getFromTransaction());
      return clone;
   }

   @Override
   public String toString() {
      return "QueryOptions [" + super.toString() + "]";
   }
}
