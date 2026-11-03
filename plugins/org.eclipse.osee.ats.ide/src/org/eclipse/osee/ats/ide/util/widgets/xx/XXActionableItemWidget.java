/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.ai.ActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.EditActionableItemsAction;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.CheckBoxStateTreeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.ICheckBoxStateTreeViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXActionableItemWidget extends XXAbstractBaseXWidget<IAtsActionableItem> {

   public static final WidgetId ID = WidgetIdAts.XXActionableItemWidget;
   private static final String ACTIONABLE_ITEM_S = "Actionable Item(s)";

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private IAtsTeamDefinition teamDef;
   Collection<IAtsActionableItem> disabledAis = new ArrayList<>();

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXActionableItemWidget() {
      super(ID, ACTIONABLE_ITEM_S);
      setAttributeType(AtsAttributeTypes.ActionableItemReference);
      set(XOption.VAL_MULTI_SEL);
      setOseeImage(AtsImage.ACTIONABLE_ITEM);
      setAsProviderFor(AtsAttributeTypes.ActionableItemReference);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean handleSelection() {
      if (xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         EditActionableItemsAction.editActionableItems(teamWf);
         return false;
      }
      if (isValueMultiSelectFromSelectable()) {
         return handleAISpecializedMultiStateSelection();
      }
      return super.handleSelection();
   }

   @Override
   public IAtsActionableItem getSentinel() {
      return null;
   }

   @Override
   public Collection<IAtsActionableItem> getSelectable() {
      if (!super.getSelectable().isEmpty()) {
         return super.getSelectable();
      }
      if (teamDef != null) {
         Collection<ActionableItem> ais = teamDef.getActionableItems();
         if (!ais.isEmpty()) {
            return Collections.castAll(ais);
         }
      }
      boolean allAis = is(XOption.ACTIVE_BOTH);
      return Collections.castAll(AtsApiService.get().getActionableItemService().getTopLevelActionableItems(
         allAis ? Active.Both : Active.Active));
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof IAtsActionableItem;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public ILabelProvider getLabelProvider() {
      // Multi-select uses the checkbox-state dialog, whose AiLabelProvider paints checkbox images and supports
      // disabled/greyed items. Single-select uses the base FilteredTreeDialog (a plain tree), so return a normal
      // label provider - otherwise the painted checkbox images make a single-select list look like a checklist.
      if (isValueMultiSelectFromSelectable()) {
         return new AiLabelProvider(null, is(XOption.COLON), disabledAis);
      }
      return new AtsObjectLabelProvider();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public boolean handleAISpecializedMultiStateSelection() {
      try {
         AiLabelProvider labelProvider = new AiLabelProvider(null, is(XOption.ENABLE_ALL), disabledAis);
         // CheckBoxStateFilteredTreeDialog is a specialized dialog that allows items to be grey'd out
         // and thus disabled for selection but still show as parents or stand-alone items.
         // eg: Choose SAW_PL_Requirements but not SAW_PL parent but still have the hierarchy.
         // Eventually, this could be moved into XXAbstractBaseXWidget to allow all widgets this capability.
         CheckBoxStateFilteredTreeDialog<IAtsActionableItem> dialog =
            new CheckBoxStateFilteredTreeDialog<IAtsActionableItem>("Select Actionable Item(s)",
               "Select Actionable Item(s)", new ActionableItemTreeContentProvider(Active.Both), labelProvider,
               new ToStringViewerSorter());
         if (isValueSingleSelectFromSelectable()) {
            dialog.setSingleSelect(true);
         }
         labelProvider.setTreeViewer(dialog.getTreeViewer());
         dialog.setInput(getSelectable());
         int result = dialog.open();
         if (result == 0) {
            List<IAtsActionableItem> selectedAis = new ArrayList<>();
            for (IAtsActionableItem ai : dialog.getChecked()) {
               if (ai.isActive() && is(XOption.ENABLE_ALL)) {
                  selectedAis.add(ai);
               } else if (ai.isActive() && ai.isActionable()) {
                  selectedAis.add(ai);
               }
            }
            setSelected(selectedAis);
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public void addDisabledAis(Collection<IAtsActionableItem> disabledAis) {
      this.disabledAis = disabledAis;
   }

   public static class AiLabelProvider extends CheckBoxStateTreeLabelProvider {
      private final boolean enableAll;
      private final Collection<IAtsActionableItem> disabledAis;
      public AiLabelProvider(ICheckBoxStateTreeViewer treeViewer, boolean enableAll, Collection<IAtsActionableItem> disabledAis) {
         super(treeViewer);
         this.enableAll = enableAll;
         this.disabledAis = disabledAis;
      }

      @Override
      protected boolean isEnabled(Object element) {
         if (enableAll) {
            return true;
         }
         if (element instanceof IAtsActionableItem) {
            boolean enabled =
               ((IAtsActionableItem) element).isActive() && ((IAtsActionableItem) element).isActionable();
            if (enabled) {
               if (disabledAis.contains(element)) {
                  enabled = false;
               }
            }
            return enabled;
         }
         return true;
      }
   }

   @Override
   public IContentProvider getContentProvider() {
      boolean allAis = is(XOption.ACTIVE_BOTH);
      return new ActionableItemTreeContentProvider(allAis ? Active.Both : Active.Active);
   }

   @Override
   public void filteredCheckboxTreeCreated(FilteredCheckboxTree treeViewer) {
      if (treeViewer instanceof ICheckBoxStateTreeViewer) {
         AiLabelProvider aiLabelProvider = (AiLabelProvider) treeViewer.getViewer().getLabelProvider();
         aiLabelProvider.setTreeViewer((ICheckBoxStateTreeViewer) treeViewer);
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (xxWid.isArtifact()) {
         setSelected(xxWid.getActionableItems(), false);
      }
   }

}
