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

package org.eclipse.osee.ats.ide.blam;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.ActionableItemReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.TeamDefinitionReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.core.column.ActionableItemsColumn;
import org.eclipse.osee.ats.core.column.AtsIdColumn;
import org.eclipse.osee.ats.core.column.TeamColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXActionableItemWidget;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListArtifactDropWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.ats.api.data.AtsUserGroups;

/**
 * @author Vaibhav Y. Patel
 */
@Component(service = AbstractBlam.class, immediate = true)
public class RealignActionableItemsBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Realign Actionable Items for actions";
   private final static String ACTIONABLE_ITEMS_FROM = "Actionable Item(s) From";
   private final static String ACTIONABLE_ITEM_TO = "Actionable Item To";
   private final static String PERSIST = "Persist? (Read Description before checking the box)";
   private final static String DELETE = "Delete? (Read Description before checking the box)";

   private final AtsApi atsApi = AtsApiService.get();
   private XXListArtifactDropWidget xListDropViewer;
   private XXCheckBoxWidget xPersistCheckBox;
   private XXCheckBoxWidget xDeleteCheckBox;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public RealignActionableItemsBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wba.andListDropWidget(ACTIONABLE_ITEMS_FROM);
      wba.andActionableItemWidget(ACTIONABLE_ITEM_TO);
      wba.andCheckBoxWidget(PERSIST);
      wba.andCheckBoxWidget(DELETE);
      return wba.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(ACTIONABLE_ITEMS_FROM)) {
         xListDropViewer = (XXListArtifactDropWidget) xWidget;
      } else if (xWidget.getLabel().equals(PERSIST)) {
         xPersistCheckBox = (XXCheckBoxWidget) xWidget;
         xPersistCheckBox.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (xPersistCheckBox.isSelected()) {
                  xDeleteCheckBox.setEditable(true);
               } else {
                  xDeleteCheckBox.setEditable(false);
               }
            }
         });

      } else if (xWidget.getLabel().equals(DELETE)) {
         xDeleteCheckBox = (XXCheckBoxWidget) xWidget;
         xDeleteCheckBox.setEditable(false);
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask(BLAM_NAME, IProgressMonitor.UNKNOWN);
      Collection<ArtifactToken> actionableItemsFrom = getActionableItemsFrom();
      IAtsActionableItem actionableItemTo = getSelectedActionableItemTo();
      boolean isPersistAllowed = variableMap.getBoolean(PERSIST);
      boolean isDeleteAllowed = variableMap.getBoolean(DELETE);

      if (actionableItemTo.isValid() && actionableItemTo.getTeamDefinition() != null) {
         realignAiAndTdForActionsIfAllowed(actionableItemsFrom, actionableItemTo, isPersistAllowed, isDeleteAllowed);
      } else {
         StringBuilder err = new StringBuilder();
         err.append("Selected Actionable Item To: " + actionableItemTo.toString());
         err.append(" Or Related Team Definition: " + actionableItemTo.getTeamDefinition());
         err.append(" NOT VALID");
         log(err.toString());
      }

      monitor.done();
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private IAtsActionableItem getSelectedActionableItemTo() {
      XXActionableItemWidget widget = (XXActionableItemWidget) getXWidget(WidgetIdAts.XXActionableItemWidget);
      return widget.getSelectedFirst();
   }

   private Collection<ArtifactToken> getActionableItemsFrom() {
      Collection<ArtifactToken> actionableItemsFrom = Collections.castAll(xListDropViewer.getSelectedArtifacts());
      Conditions.checkNotNullOrEmpty(actionableItemsFrom, ACTIONABLE_ITEMS_FROM);
      return actionableItemsFrom;
   }

   private void realignAiAndTdForActionsIfAllowed(Collection<ArtifactToken> actionableItemsFrom,
      IAtsActionableItem actionableItemTo, boolean isPersistAllowed, boolean isDeleteAllowed) {
      IAtsChangeSet changes = atsApi.createChangeSet("Realign AI/TD References for actions");
      List<ArtifactToken> teamWfs = atsApi.getQueryService().getArtifactsFromAttributeValues(ActionableItemReference,
         actionableItemsFrom, CoreBranches.COMMON);
      List<Long> aiIdsFrom = actionableItemsFrom.stream().map(ArtifactToken::getId).collect(Collectors.toList());

      final XResultData resultData = new XResultData();
      resultData.addRaw(getName());
      resultData.addRaw("\n Related actions counts: " + teamWfs.size() + "\n");
      resultData.addRaw(AHTML.beginMultiColumnTable(0, 1));
      String[] titleRow = {"Action Title", "Id", "AI From", "AI To", "TD From", "TD To"};
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(titleRow));

      for (ArtifactToken teamWf : teamWfs) {
         new ActionableItemsColumn(atsApi);
         Collection<IAtsActionableItem> currentlyAssignedAIs =
            ActionableItemsColumn.getActionableItems((IAtsObject) teamWf);
         String currentlyAssignedTD = new TeamColumn(atsApi).getColumnText((IAtsObject) teamWf);
         String currentlyAssignedAtsId = new AtsIdColumn(atsApi).getColumnText((IAtsObject) teamWf);

         resultData.addRaw(AHTML.addRowMultiColumnTable(teamWf.getName(), currentlyAssignedAtsId,
            Collections.toString(", ", currentlyAssignedAIs), actionableItemTo.getName(), currentlyAssignedTD,
            actionableItemTo.getTeamDefinition().getName()));

         if (isPersistAllowed && !resultData.isErrors()) {
            try {
               List<Object> assignAIsTo = new ArrayList<Object>();
               assignAIsTo.add(actionableItemTo);
               for (IAtsActionableItem currentlyAssignedAI : currentlyAssignedAIs) {
                  if (!aiIdsFrom.contains(currentlyAssignedAI.getId())) {
                     assignAIsTo.add(currentlyAssignedAI);
                  }
               }
               changes.setAttributeValues(teamWf, ActionableItemReference, assignAIsTo);
               if (atsApi.getAttributeResolver().getAttributeCount(teamWf, TeamDefinitionReference) > 0) {
                  changes.setSoleAttributeValue(teamWf, TeamDefinitionReference, actionableItemTo.getTeamDefinition());
               }
            } catch (Exception ex) {
               resultData.errorf(ex.toString());
               resultData.errorf("%s - %s has unknown problem.\n", currentlyAssignedAtsId, teamWf.getName());
            }
         }
      }

      if (isDeleteAllowed && isPersistAllowed && !resultData.isErrors()) {
         for (ArtifactToken actionableItemFrom : actionableItemsFrom) {
            changes.deleteArtifact(actionableItemFrom);
         }
         changes.executeIfNeeded();
      } else if (isPersistAllowed && !resultData.isErrors()) {
         changes.executeIfNeeded();
      }

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XResultDataUI.report(resultData, "Results for " + getName());
         }
      });

   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      StringBuilder description = new StringBuilder();
      description.append("This BLAM will realign items that are associated with the dragged/dropped ");
      description.append("\"Actionable Item(s) From\" to the selected \"Actionable Item To\". ");
      description.append(
         "Team Definition will get realign to the related Team Definition of the selected \"Actionable Item To\". ");
      description.append(
         "It will re-assign team workflows to the selected \"Actionable Item To\" and related Team Definition. ");
      description.append(
         "\n Note: Run BLAM without checking the persist and delete checkboxes first. It will generate report. ");
      description.append(
         "If report data looks correct, then check the persist and delete checkboxes and run the BLAM to execute changes. ");
      description.append(
         "\n Warning: When delete checkbox is checked, it will delete \"Actionable Item(s) From\" from OSEE. ");
      return description.toString();
   }

}
