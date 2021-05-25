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

import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureInserter extends ArtifactInserterImpl<InterfaceStructureToken> {

   public InterfaceStructureInserter(OrcsApi orcsApi, ArtifactAccessor<InterfaceStructureToken> accessor) {
      super(CoreArtifactTypes.InterfaceStructure, orcsApi, "Interface Structure", accessor,
         CoreArtifactTokens.InterfaceMessagesFolder);
   }

}
