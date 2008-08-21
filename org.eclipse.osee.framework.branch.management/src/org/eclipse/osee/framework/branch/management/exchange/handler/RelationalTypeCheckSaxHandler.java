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
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class RelationalTypeCheckSaxHandler extends RelationalSaxHandler {

   public static RelationalTypeCheckSaxHandler newCacheAllDataRelationalTypeCheckSaxHandler() {
      return new RelationalTypeCheckSaxHandler(true, 0);
   }

   public static RelationalTypeCheckSaxHandler newLimitedCacheRelationalTypeCheckSaxHandler(int cacheLimit) {
      return new RelationalTypeCheckSaxHandler(false, cacheLimit);
   }

   private RelationalTypeCheckSaxHandler(boolean isCacheAll, int cacheLimit) {
      super(isCacheAll, cacheLimit);
   }

   @Override
   protected void processData(Map<String, String> fieldMap) {
      System.out.println(String.format("Table: [%s] Data: %s ", getMetaData(), fieldMap));
   }
}
