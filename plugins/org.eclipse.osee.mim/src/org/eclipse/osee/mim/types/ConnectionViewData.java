/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class ConnectionViewData extends PLGenericDBObject {
   private ConnectionViewType TransportType = ConnectionViewType.ETHERNET; //will need logic for both of these or data stored in DB
   private boolean isDashed = true;

   public ConnectionViewData(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ConnectionViewData(ArtifactReadable art) {
      super(art);
   }

   public ConnectionViewData(Long id, String name) {
      super(id, name);
   }

   public ConnectionViewData() {
   }

   /**
    * @return the type
    */
   public ConnectionViewType getTransportType() {
      return TransportType;
   }

   /**
    * @param type the type to set
    */
   public void setTransportType(ConnectionViewType type) {
      this.TransportType = type;
   }

   /**
    * @return the isDashed
    */
   public boolean isDashed() {
      return isDashed;
   }

   /**
    * @param isDashed the isDashed to set
    */
   public void setDashed(boolean isDashed) {
      this.isDashed = isDashed;
   }

}
