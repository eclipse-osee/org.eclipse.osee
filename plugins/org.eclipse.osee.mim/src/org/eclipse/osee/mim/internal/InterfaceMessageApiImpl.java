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
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 * @todo
 */
public class InterfaceMessageApiImpl implements InterfaceMessageApi {
   private ArtifactAccessor<InterfaceMessageToken> accessor;

   InterfaceMessageApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceMessageAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceMessageToken> getAccessor() {
      return accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<InterfaceMessageToken> accessor) {
      this.accessor = accessor;
   }

}
