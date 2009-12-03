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
package org.eclipse.osee.framework.messaging.event.skynet.event;

/**
 * @author Donald G. Dunne
 * @author Robert A. Fisher
 */
public class NetworkRelationLinkRationalModifiedEvent extends SkynetRelationLinkEventBase {
   private static final long serialVersionUID = 548299278567054333L;

   private final String rationale;

   public NetworkRelationLinkRationalModifiedEvent(int gammaId, int branchId, int relId, int artAId, int artATypeId, int artBId, int artBTypeId, int relTypeId, NetworkSender networkSender, String rationale) {
      super(gammaId, branchId, relId, artAId, artATypeId, artBId, artBTypeId, relTypeId, networkSender);

      this.rationale = rationale;
   }

   public NetworkRelationLinkRationalModifiedEvent(SkynetRelationLinkEventBase base, String rationale) {
      super(base);

      this.rationale = rationale;
   }

   public String getRationale() {
      return rationale;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NetworkRelationLinkRationalModifiedEvent) {
         return getRelId() == ((NetworkRelationLinkRationalModifiedEvent) obj).getRelId();
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getRelId();
   }

}
