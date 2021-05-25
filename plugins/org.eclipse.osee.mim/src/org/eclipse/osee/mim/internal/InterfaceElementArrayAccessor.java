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

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.types.InterfaceStructureElementArrayToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceElementArrayAccessor extends ArtifactAccessorImpl<InterfaceStructureElementArrayToken> {

   public InterfaceElementArrayAccessor(OrcsApi orcsApi) {
      super(CoreArtifactTypes.InterfaceDataElementArray, orcsApi);
   }

}
