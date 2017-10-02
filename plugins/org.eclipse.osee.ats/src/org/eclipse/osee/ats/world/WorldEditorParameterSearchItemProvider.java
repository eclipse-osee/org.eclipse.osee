/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.search.AtsSearchWorkflowSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorParameterSearchItemProvider extends WorldEditorProvider implements IWorldEditorParameterProvider {

   private final WorldEditorParameterSearchItem worldParameterSearchItem;
   public final static String ENTER_OPTIONS_AND_SELECT_SEARCH = "Enter options and select \"Search\"";
   private boolean firstTime = true;
   private final boolean loading = false;

   public WorldEditorParameterSearchItemProvider(WorldEditorParameterSearchItem worldParameterSearchItem) {
      this(worldParameterSearchItem, null, TableLoadOption.None);
   }

   public WorldEditorParameterSearchItemProvider(WorldEditorParameterSearchItem worldParameterSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.worldParameterSearchItem = worldParameterSearchItem;
   }

   @Override
   public IWorldEditorProvider copyProvider()  {
      return new WorldEditorParameterSearchItemProvider(
         (WorldEditorParameterSearchItem) worldParameterSearchItem.copy(), customizeData, tableLoadOptions);
   }

   public WorldSearchItem getWorldSearchItem() {
      return worldParameterSearchItem;
   }

   @Override
   public String getName()  {
      return worldParameterSearchItem.getName();
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend)  {
      WorldSearchItem searchItem = getWorldSearchItem();
      if (searchItem instanceof AtsSearchWorkflowSearchItem) {
         AtsSearchWorkflowSearchItem workflowSearchItem = (AtsSearchWorkflowSearchItem) searchItem;
         AtsSearchData savedData = workflowSearchItem.getSavedData();
         if (savedData != null) {
            worldEditor.setTableTitle(ENTER_OPTIONS_AND_SELECT_SEARCH, false);
            firstTime = false;
         }
      }
      if (firstTime) {
         firstTime = false;
         worldEditor.getWorldXWidgetActionPage().getWorldComposite().getWorldXViewer().setLoading(false);
         worldEditor.setTableTitle(ENTER_OPTIONS_AND_SELECT_SEARCH, false);
         return;
      }
      if (worldParameterSearchItem.isCancelled()) {
         worldEditor.getWorldXWidgetActionPage().getWorldComposite().getWorldXViewer().setLoading(false);
         return;
      }

      Result result = worldParameterSearchItem.isParameterSelectionValid();
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return;
      }

      if (loading) {
         AWorkbench.popup("Already Loading, Please Wait");
         return;
      }
      worldParameterSearchItem.setupSearch();

      boolean pend = Arrays.asList(tableLoadOptions).contains(TableLoadOption.ForcePend) || forcePend;
      super.run(worldEditor, searchType, pend);

   }

   @Override
   public String getSelectedName(SearchType searchType)  {
      return Strings.truncate(worldParameterSearchItem.getSelectedName(searchType), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public String getParameterXWidgetXml()  {
      return worldParameterSearchItem.getParameterXWidgetXml();
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return worldParameterSearchItem;
   }

   /**
    * Available for actions needing to be done after controls are created
    */
   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      worldParameterSearchItem.createParametersSectionCompleted(managedForm, mainComp);
      WorldSearchItem searchItem = getWorldSearchItem();
      if (searchItem instanceof AtsSearchWorkflowSearchItem) {
         AtsSearchWorkflowSearchItem workflowSearchItem = (AtsSearchWorkflowSearchItem) searchItem;
         AtsSearchData savedData = workflowSearchItem.getSavedData();
         if (savedData != null) {
            workflowSearchItem.loadWidgets(savedData);
         }
      }

   }

   /**
    * Available for actions needing to be done after controls are created
    */
   @Override
   public void createToolbar(IToolBarManager toolBarManager) {
      worldParameterSearchItem.createToolbar(toolBarManager);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return worldParameterSearchItem.performSearch(searchType);
   }

}
