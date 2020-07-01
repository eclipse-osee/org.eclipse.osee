/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.CoreTupleTypes;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactTypeJoin extends OrcsTypeJoin<ArtifactTypeJoin, ArtifactTypeToken> {

   public ArtifactTypeJoin(String name, ArtifactTypeToken... artifactTypes) {
      super(CoreTupleTypes.ArtifactTypeJoin, name, artifactTypes);
   }
}