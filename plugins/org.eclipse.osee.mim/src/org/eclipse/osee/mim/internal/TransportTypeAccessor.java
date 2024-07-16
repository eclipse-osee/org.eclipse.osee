/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import org.eclipse.osee.accessor.internal.ArtifactAccessorImpl;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.types.TransportType;
import org.eclipse.osee.orcs.OrcsApi;

public class TransportTypeAccessor extends ArtifactAccessorImpl<TransportType> {

   public TransportTypeAccessor(OrcsApi orcsApi) {
      super(CoreArtifactTypes.TransportType, orcsApi);
   }

}
