/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceRateAccessor extends ArtifactAccessorImpl<ArtifactAccessorResultWithoutGammas> {

   public InterfaceRateAccessor(OrcsApi orcsApi) {
      super(CoreArtifactTypes.InterfaceRate, orcsApi);
   }

}
