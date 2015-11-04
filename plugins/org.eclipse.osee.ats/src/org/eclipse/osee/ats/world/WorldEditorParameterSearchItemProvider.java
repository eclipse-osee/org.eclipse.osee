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

import java.util.Collection;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
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
   public IWorldEditorProvider copyProvider() throws OseeCoreException {
      return new WorldEditorParameterSearchItemProvider(
         (WorldEditorParameterSearchItem) worldParameterSearchItem.copy(), customizeData, tableLoadOptions);
   }

   public WorldSearchItem getWorldSearchItem() {
      return worldParameterSearchItem;
   }

   @Override
   public String getName() throws OseeCoreException {
      return worldParameterSearchItem.getName();
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException {
      if (firstTime) {
         firstTime = false;
         worldEditor.setTableTitle(ENTER_OPTIONS_AND_SELECT_SEARCH, false);
         return;
      }
      if (worldParameterSearchItem.isCancelled()) {
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

      boolean pend = Collections.getAggregate(tableLoadOptions).contains(TableLoadOption.ForcePend) || forcePend;
      super.run(worldEditor, searchType, pend);

   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return Strings.truncate(worldParameterSearchItem.getSelectedName(searchType), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return worldParameterSearchItem.getParameterXWidgetXml();
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return worldParameterSearchItem;
   }

   @Override
   public String[] getWidgetOptions(XWidgetRendererItem widgetData) {
      return null;
   }

   @Override
   public void handleSaveButtonPressed() {
      if (isSaveButtonAvailable()) {
         worldParameterSearchItem.handleSaveButtonPressed();
      }
   }

   @Override
   public boolean isSaveButtonAvailable() {
      return worldParameterSearchItem.isSaveButtonAvailable();
   }

   /**
    * Available for actions needing to be done after controls are created
    */
   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      worldParameterSearchItem.createParametersSectionCompleted(managedForm, mainComp);
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
