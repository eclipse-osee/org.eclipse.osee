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
package org.eclipse.osee.framework.messaging.event.skynet.filter;

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;

/**
 * Filter Skynet events based on the branch they are associated with.
 * 
 * @author Robert A. Fisher
 */
public class BranchFilter implements IEventFilter {
   private static final long serialVersionUID = 6810368802224101971L;

   private final int branchId;

   /**
    * @param branchId
    */
   public BranchFilter(int branchId) {
      this.branchId = branchId;
   }

   public boolean accepts(ISkynetEvent event) {
      return event.getBranchId() == branchId;
   }

}
