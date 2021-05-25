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
package org.eclipse.osee.mim;

import org.eclipse.osee.mim.types.InterfaceStructureElementToken;

/**
 * @author Luciano T. Vaglienti Api for accessing interface elements
 * @todo
 */
public interface InterfaceElementApi {
   ArtifactAccessor<InterfaceStructureElementToken> getAccessor();

   ArtifactInserter<InterfaceStructureElementToken> getInserter();
}
