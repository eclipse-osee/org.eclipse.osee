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
package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.enums.DataType;

public enum OteEventMessageType implements DataType {
   OTE_EVENT_MESSAGE(2, 2048);

   private final int depth;
   private final int bufferSize;

   private OteEventMessageType(int depth, int bufferSize) {
      this.depth = depth;
      this.bufferSize = bufferSize;
   }

   @Override
   public int getToolingBufferSize() {
      return bufferSize;
   }

   @Override
   public int getToolingDepth() {
      return depth;
   }
}