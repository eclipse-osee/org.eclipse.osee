/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {

   public Program(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.Program);
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }
}