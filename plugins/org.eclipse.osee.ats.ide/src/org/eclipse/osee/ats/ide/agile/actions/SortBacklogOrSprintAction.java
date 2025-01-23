/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.agile.actions;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.agile.navigate.SortAgileBacklog;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SortBacklogOrSprintAction extends AbstractAtsAction {

   private final IAgileBacklog bLog;
   private final AtsApi atsApi;
   private final IAgileSprint sprint;

   public SortBacklogOrSprintAction(IAgileSprint sprint) {
      super();
      this.sprint = sprint;
      this.bLog = null;
      setText("Sort Sprint");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.SORT));
      atsApi = AtsApiService.get();
   }

   public SortBacklogOrSprintAction(IAgileBacklog bLog) {
      super();
      this.bLog = bLog;
      this.sprint = null;
      setText("Sort Backlog");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.SORT));
      atsApi = AtsApiService.get();
   }

   @Override
   public void runWithException() {
      if (bLog != null) {
         SortAgileBacklog.sort("Sort Backlog", (GoalArtifact) bLog.getStoreObject(), false, atsApi);
      } else {
         SortAgileBacklog.sort("Sort Sprint", (GoalArtifact) sprint.getStoreObject(), false, atsApi);
      }
   }

}
