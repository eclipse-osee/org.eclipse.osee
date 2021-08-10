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
package org.eclipse.osee.mim.internal;

import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceElementApi;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementApiImpl implements InterfaceElementApi {

   private ArtifactAccessor<InterfaceStructureElementToken> accessor;

   InterfaceElementApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceElementAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceStructureElementToken> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceStructureElementToken> accessor) {
      this.accessor = accessor;
   }

}
