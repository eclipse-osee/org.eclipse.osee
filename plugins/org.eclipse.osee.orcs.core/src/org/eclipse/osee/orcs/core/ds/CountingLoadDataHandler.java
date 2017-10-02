/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Roberto E. Escobar
 */
public abstract class CountingLoadDataHandler extends LoadDataHandlerDecorator {

   private final AtomicInteger counter;

   public CountingLoadDataHandler(LoadDataHandler handler) {
      super(handler);
      this.counter = new AtomicInteger();
   }

   protected AtomicInteger getCounter() {
      return counter;
   }

   public int getCount() {
      return getCounter().get();
   }

   @Override
   public void onLoadStart()  {
      getCounter().set(0);
      super.onLoadStart();
   }

   protected void incrementCount() {
      getCounter().incrementAndGet();
   }

}