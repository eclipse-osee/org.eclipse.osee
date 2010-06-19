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
package org.eclipse.osee.framework.core.message;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportRequest {
   private final int srcTx;
   private final int destTx;

   public ChangeReportRequest(int srcTx, int destTx) {
      super();
      this.srcTx = srcTx;
      this.destTx = destTx;
   }

   public int getSourceTx() {
      return srcTx;
   }

   public int getDestinationTx() {
      return destTx;
   }
}
