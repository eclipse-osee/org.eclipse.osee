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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.INewActionPageAttributeFactory;
import org.eclipse.osee.ats.api.workflow.INewActionPageAttributeFactoryProvider;
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.PromptChangeUtil;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author Jeremy A. Midvdy
 */
public class PriorityColumnUI extends XViewerAtsAttributeValueColumn {

   public static PriorityColumnUI instance = null;
   private final AtsAttributeValueColumn colToken;
   private final AttributeTypeEnum<?> attrToken;

   public static PriorityColumnUI getInstance() {
      if (instance == null) {
         for (INewActionPageAttributeFactoryProvider provider : ActionFactory.getProviders()) {
            for (INewActionPageAttributeFactory factory : provider.getNewActionAttributeFactory()) {
               if (factory.useFactory() && factory.getPrioirtyColumnToken() != null) {
                  instance = new PriorityColumnUI(factory.getPrioirtyColumnToken(), factory.getPrioirtyAttrToken());
                  return instance;
               }
            }
         }
         instance = new PriorityColumnUI(AtsColumnToken.PriorityColumn, AtsAttributeTypes.Priority);
      }
      return instance;
   }

   private PriorityColumnUI(AtsAttributeValueColumn priColToken, AttributeTypeEnum<?> priAttrToken) {
      super(priColToken);
      this.colToken = priColToken;
      this.attrToken = priAttrToken;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PriorityColumnUI copy() {
      PriorityColumnUI newXCol = new PriorityColumnUI(this.colToken, this.attrToken);
      super.copy(this, newXCol);
      return newXCol;
   }

   public static boolean promptChangePriority(final Collection<? extends TeamWorkFlowArtifact> teams, AttributeTypeToken attrTypeToken, boolean persist) {

      try {
         for (TeamWorkFlowArtifact team : teams) {
            if (AtsClientService.get().getVersionService().isReleased(
               team) || AtsClientService.get().getVersionService().isVersionLocked(team)) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
               return false;
            }
         }
         PromptChangeUtil.promptChangeAttribute(teams, attrTypeToken, persist, false);
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't change priority", ex);
         return false;
      }
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         // Only prompt change for sole attribute types
         if (AttributeTypeManager.getMaxOccurrences(getAttributeType()) != 1) {
            return false;
         }
         if (treeItem.getData() instanceof Artifact) {
            Artifact useArt = AtsClientService.get().getQueryServiceClient().getArtifact(treeItem);
            if (useArt.isOfType(AtsArtifactTypes.Action)) {
               if (AtsClientService.get().getWorkItemService().getTeams(useArt).size() == 1) {
                  useArt = AtsClientService.get().getQueryServiceClient().getArtifact(
                     AtsClientService.get().getWorkItemService().getFirstTeam(useArt));
               } else {
                  return false;
               }
            }
            if (!useArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               return false;
            }
            TeamWorkFlowArtifact team = (TeamWorkFlowArtifact) useArt;
            if (AtsClientService.get().getVersionService().isReleased(
               team) || AtsClientService.get().getVersionService().isVersionLocked(team)) {
               AWorkbench.popup("ERROR",
                  "Team Workflow\n \"" + team.getName() + "\"\n version is locked or already released.");
               return false;
            }

            boolean modified =
               promptChangePriority(Arrays.asList((TeamWorkFlowArtifact) useArt), this.attrToken, isPersistViewer());
            XViewer xViewer = (XViewer) ((XViewerColumn) treeColumn.getData()).getXViewer();
            if (modified && isPersistViewer(xViewer)) {
               useArt.persist("persist priority via alt-left-click");
            }
            if (modified) {
               xViewer.update(useArt, null);
               return true;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<TeamWorkFlowArtifact> awas = new HashSet<>();
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof Artifact) {
            Artifact art = AtsClientService.get().getQueryServiceClient().getArtifact(item);
            if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               awas.add((TeamWorkFlowArtifact) art);
            }
         }
      }
      if (awas.isEmpty()) {
         AWorkbench.popup("Must select Team Workflow(s)");
         return;
      }
      promptChangePriority(awas, this.attrToken, true);
      return;
   }

}
