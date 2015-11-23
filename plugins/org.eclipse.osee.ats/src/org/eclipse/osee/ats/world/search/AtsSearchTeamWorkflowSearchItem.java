/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchTeamWorkflowSearchItem extends AbstractWorkItemSearchItem {

   public static final String NAMESPACE = "ats.search.team";
   private static final String TITLE = "Team Workflow Search";

   public AtsSearchTeamWorkflowSearchItem() {
      super(TITLE, NAMESPACE, AtsImage.TEAM_WORKFLOW);
   }

   public AtsSearchTeamWorkflowSearchItem(AbstractWorkItemSearchItem searchItem) {
      super(searchItem, TITLE, NAMESPACE, AtsImage.TEAM_WORKFLOW);
   }

   public AtsSearchTeamWorkflowSearchItem(String title, String namespace, AtsImage image) {
      super(title, namespace, image);
   }

   @Override
   public AbstractWorkItemSearchItem copy() {
      AtsSearchTeamWorkflowSearchItem item = new AtsSearchTeamWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AbstractWorkItemSearchItem copyProvider() {
      AtsSearchTeamWorkflowSearchItem item = new AtsSearchTeamWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public String getShortNamePrefix() {
      return "TWS";
   }

   @Override
   Collection<WorkItemType> getWorkItemTypes() {
      return Arrays.asList(WorkItemType.TeamWorkflow);
   }

   @Override
   protected boolean showWorkItemWidgets() {
      return false;
   }

}
