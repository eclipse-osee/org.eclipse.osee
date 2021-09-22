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
import org.eclipse.osee.mim.InterfaceEnumerationApi;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumerationApiImpl implements InterfaceEnumerationApi {

   private ArtifactAccessor<InterfaceEnumeration> accessor;
   public InterfaceEnumerationApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceEnumerationAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceEnumeration> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceEnumeration> accessor) {
      this.accessor = accessor;
   }

}
