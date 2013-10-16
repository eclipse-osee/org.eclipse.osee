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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.RelationData;

/**
 * @author Roberto E. Escobar
 */
public class BufferedLoadDataHandler extends ArtifactDataCountHandler {

   private final LoadDataBuffer buffer;

   public BufferedLoadDataHandler(LoadDataHandler handler, LoadDataBuffer buffer) {
      super(handler);
      this.buffer = buffer;
   }

   protected LoadDataBuffer getBuffer() {
      return buffer;
   }

   @SuppressWarnings("unused")
   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      buffer.addData(data);
   }

   @SuppressWarnings("unused")
   @Override
   public void onData(AttributeData data) throws OseeCoreException {
      buffer.addData(data);
   }

   @SuppressWarnings("unused")
   @Override
   public void onData(RelationData data) throws OseeCoreException {
      buffer.addData(data);
   }

}