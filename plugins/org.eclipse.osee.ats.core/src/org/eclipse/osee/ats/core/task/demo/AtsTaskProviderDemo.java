/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.task.demo;

import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.ats.api.task.related.IAutoGenTaskData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.task.AbstractAtsTaskProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskProviderDemo extends AbstractAtsTaskProvider {

   @Override
   public IAutoGenTaskData getAutoGenTaskData(AutoGenVersion autoGenVer, IAtsTask task) {
      if (autoGenVer == AutoGenVersionDemo.Demo) {
         return new AutoGenTaskDataDemo(task);
      }
      return null;
   }

   @Override
   public AutoGenVersion getAutoGenTaskVersionToSet(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch) {
      if (crttwd.getDestTeamWf() != null && (crttwd.getDestTeamWf().getArtifactType().equals(
         AtsDemoOseeTypes.DemoCodeTeamWorkflow) || crttwd.getDestTeamWf().getArtifactType().equals(
            AtsDemoOseeTypes.DemoTestTeamWorkflow))) {
         return AutoGenVersionDemo.Demo;
      }
      if (crttwd.getDestTeamDef() != null) {
         IAtsTeamDefinition destTeamDef =
            atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(crttwd.getDestTeamDef().getId());
         IAtsProgram program = atsApi.getProgramService().getProgram(destTeamDef);
         if (program != null && program.equals(DemoArtifactToken.SAW_Program)) {
            return AutoGenVersionDemo.Demo;
         }
      }
      return null;
   }

}
