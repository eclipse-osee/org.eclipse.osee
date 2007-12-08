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

import org.eclipse.osee.framework.messaging.event.skynet.ISkynetArtifactEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent;
import org.eclipse.osee.framework.messaging.event.skynet.ISkynetRelationLinkEvent;

/**
 * @author Robert A. Fisher
 */
public class BranchArtTypeFilter extends BranchFilter {
   private static final long serialVersionUID = 1951625401943195730L;

   private final int artTypeId;

   /**
    * @param branchId
    */
   public BranchArtTypeFilter(int branchId, int artTypeId) {
      super(branchId);

      this.artTypeId = artTypeId;
   }

   @Override
   public boolean accepts(ISkynetEvent event) {
      boolean accept = super.accepts(event);

      // If the event has artifact type information, then include it
      if (event instanceof ISkynetArtifactEvent) {
         accept &= ((ISkynetArtifactEvent) event).getArtTypeId() == artTypeId;
      } else if (event instanceof ISkynetRelationLinkEvent) {
         ISkynetRelationLinkEvent relEvent = (ISkynetRelationLinkEvent) event;
         accept &= relEvent.getArtATypeId() == artTypeId || relEvent.getArtBTypeId() == artTypeId;
      }

      return accept;
   }
}
