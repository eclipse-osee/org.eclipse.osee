/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken.ArtifactTokenImpl;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramArtifactToken extends ArtifactTokenImpl implements IAtsProgramArtifactToken {

   public AtsProgramArtifactToken(Long id, String name) {
      super(id, name, CoreBranches.COMMON, AtsArtifactTypes.Program);
   }

   public AtsProgramArtifactToken(Long id, String name, ArtifactTypeToken artifactType) {
      super(id, name, CoreBranches.COMMON, artifactType);
   }

   public static IAtsProgramArtifactToken valueOf(Long id, String name) {
      return new AtsProgramArtifactToken(id, name);
   }

   public static IAtsProgramArtifactToken valueOf(long id, String name, ArtifactTypeToken artifactType) {
      return new AtsProgramArtifactToken(id, name, artifactType);
   }

}
