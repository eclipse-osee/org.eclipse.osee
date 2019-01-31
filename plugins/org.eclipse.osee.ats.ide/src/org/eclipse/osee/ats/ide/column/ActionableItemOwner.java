/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.action.ActionArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.FrameworkArtifactImageProvider;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemOwner extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ActionableItemOwner instance = new ActionableItemOwner();

   public static ActionableItemOwner getInstance() {
      return instance;
   }

   protected ActionableItemOwner() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".aiOwner", "Actionable Item Owner", 80, XViewerAlign.Left, false,
         SortDataType.String, false, null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActionableItemOwner copy() {
      ActionableItemOwner newXCol = new ActionableItemOwner();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return Collections.toString("; ", getActionableItemOwners(element));
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   public static Set<User> getActionableItemOwners(Object element) {
      Set<User> users = new HashSet<>();
      if (element instanceof ActionArtifact) {
         for (TeamWorkFlowArtifact teamArt : ((ActionArtifact) element).getTeams()) {
            users.addAll(getActionableItemOwners(teamArt));
         }
      } else if (element instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) element).getParentTeamWorkflow();
         if (teamArt != null) {
            for (IAtsActionableItem aia : AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
               teamArt)) {
               for (ArtifactToken art : AtsClientService.get().getRelationResolver().getRelated(aia.getStoreObject(),
                  AtsRelationTypes.ActionableItem_User)) {
                  users.add((User) art);
               }
            }
         }
      } else if (element instanceof IAtsWorkItem) {
         return getActionableItemOwners(((IAtsWorkItem) element).getStoreObject());
      }
      return users;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         return FrameworkArtifactImageProvider.getUserImage(getActionableItemOwners(element));
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public String getDescription() {
      return "Owners (if any) of Actionable Items associated with Team Workflows.";
   }

}