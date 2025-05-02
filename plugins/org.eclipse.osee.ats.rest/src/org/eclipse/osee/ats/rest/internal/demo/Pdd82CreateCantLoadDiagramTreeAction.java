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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.CIS_Test_AI);

      atsApi.getActionService().createAction(null, DemoArtifactToken.CantLoadDiagramTree_TeamWf.getName(),
         "Problem with the Diagram Tree", ChangeTypes.Problem, "3", false, null, aias, new Date(),
         atsApi.getUserService().getCurrentUser(), Arrays.asList(new ArtifactTokenActionListener()),
         changes);

      changes.execute();
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.CantLoadDiagramTree_TeamWf;
      }
   }

}
