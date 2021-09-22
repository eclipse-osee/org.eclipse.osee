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
import org.eclipse.osee.mim.InterfaceEnumerationSetApi;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationSetApiImpl implements InterfaceEnumerationSetApi {

   private ArtifactAccessor<InterfaceEnumerationSet> accessor;
   public InterfaceEnumerationSetApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceEnumerationSetAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceEnumerationSet> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceEnumerationSet> accessor) {
      this.accessor = accessor;
   }

}
