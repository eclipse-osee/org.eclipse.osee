/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client.internal.search;

import org.eclipse.osee.orcs.rest.client.internal.Options;

/**
 * @author Roberto E. Escobar
 */
public class QueryOptions extends Options {

   private boolean includeCache = true;

   public QueryOptions() {
      super();
   }

   @Override
   public void reset() {
      super.reset();
      includeCache = true;
   }

   public boolean isCacheIncluded() {
      return includeCache;
   }

   public void setIncludeCache(boolean enabled) {
      includeCache = enabled;
   }

   @Override
   public QueryOptions clone() {
      QueryOptions clone = new QueryOptions();
      clone.setIncludeDeleted(this.areDeletedIncluded());
      clone.setFromTransaction(this.getFromTransaction());
      clone.includeCache = this.includeCache;
      return clone;
   }

   @Override
   public String toString() {
      return "QueryOptions [includeCache=" + includeCache + " [" + super.toString() + "]]";
   }
}
