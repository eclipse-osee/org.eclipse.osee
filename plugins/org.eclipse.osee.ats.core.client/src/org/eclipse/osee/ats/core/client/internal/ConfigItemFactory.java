/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class ConfigItemFactory implements IAtsConfigItemFactory {

   @Override
   public IAtsConfigObject getConfigObject(Object artifact) throws OseeCoreException {
      IAtsConfigObject configObject = null;
      if (artifact instanceof IAtsConfigObject) {
         configObject = (IAtsConfigObject) artifact;
      }
      return configObject;
   }

   @Override
   public IAtsVersion getVersion(Object artifact) {
      IAtsVersion version = null;
      if (artifact instanceof IAtsVersion) {
         version = (IAtsVersion) artifact;
      }
      return version;
   }

   @Override
   public IAtsTeamDefinition getTeamDef(Object artifact) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      if (artifact instanceof IAtsTeamDefinition) {
         teamDef = (IAtsTeamDefinition) artifact;
      }
      return teamDef;
   }

   @Override
   public IAtsActionableItem getActionableItem(Object artifact) throws OseeCoreException {
      IAtsActionableItem ai = null;
      if (artifact instanceof IAtsActionableItem) {
         ai = (IAtsActionableItem) artifact;
      }
      return ai;
   }

   @Override
   public IAtsProgram getProgram(Object artifact) {
      IAtsProgram program = null;
      if (artifact instanceof IAtsProgram) {
         program = (IAtsProgram) artifact;
      }
      return program;
   }

}
