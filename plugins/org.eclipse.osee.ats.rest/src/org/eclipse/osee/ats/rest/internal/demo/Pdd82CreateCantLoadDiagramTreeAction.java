/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.CIS_Test_AI;
import static org.eclipse.osee.ats.api.demo.DemoArtifactToken.CantLoadDiagramTree_TeamWf;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd82CreateCantLoadDiagramTreeAction extends AbstractPopulateDemoDatabase {

   public Pdd82CreateCantLoadDiagramTreeAction(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), CantLoadDiagramTree_TeamWf.getName(),
            "Problem with the Situation Page") //
         .andAiAndToken(CIS_Test_AI, CantLoadDiagramTree_TeamWf) //
         .andChangeType(ChangeTypes.Problem).andPriority("3");
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return;
      }

   }

}
