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
package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerDecorator;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataCountHandler extends LoadDataHandlerDecorator {

   private final AtomicInteger counter;

   public ArtifactDataCountHandler(LoadDataHandler handler) {
      super(handler);
      this.counter = new AtomicInteger();
   }

   protected AtomicInteger getCounter() {
      return counter;
   }

   public int getArtifactCount() {
      return getCounter().get();
   }

   @Override
   public void onLoadStart() throws OseeCoreException {
      getCounter().set(0);
      super.onLoadStart();
   }

   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      getCounter().incrementAndGet();
      super.onData(data);
   }

}