/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class Program extends AtsConfigObject implements IAtsProgram {

   public Program(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact);
   }

   @Override
   public String getTypeName() {
      return "Program";
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }
}