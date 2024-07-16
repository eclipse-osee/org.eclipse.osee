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
import org.eclipse.osee.mim.InterfaceElementArrayApi;
import org.eclipse.osee.mim.types.InterfaceStructureElementArrayToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementArrayApiImpl implements InterfaceElementArrayApi {

   private ArtifactAccessor<InterfaceStructureElementArrayToken> accessor;

   InterfaceElementArrayApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceElementArrayAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceStructureElementArrayToken> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceStructureElementArrayToken> accessor) {
      this.accessor = accessor;
   }

}
