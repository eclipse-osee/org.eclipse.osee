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

import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorParameterSearchItem extends WorldSearchItem implements IWorldEditorParameterProvider, IDynamicWidgetLayoutListener, IXWidgetOptionResolver {

   private CustomizeData customizeData;
   private TableLoadOption[] tableLoadOptions;

   public WorldEditorParameterSearchItem(String name, KeyedImage oseeImage) throws OseeArgumentException {
      super(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldEditorParameterSearchItem(String name, LoadView loadView) throws OseeArgumentException {
      this(name, loadView, null);
   }

   public WorldEditorParameterSearchItem(String name, LoadView loadView, KeyedImage oseeImage) throws OseeArgumentException {
      super(name, loadView, oseeImage);
   }

   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem) throws OseeArgumentException {
      this(worldSearchItem, null);
   }

   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem, KeyedImage oseeImage) throws OseeArgumentException {
      super(worldSearchItem, oseeImage);
   }

   public abstract String getParameterXWidgetXml() throws OseeCoreException;

   public abstract Result isParameterSelectionValid() throws OseeCoreException;

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException {
   }

   @Override
   public String[] getWidgetOptions(DynamicXWidgetLayoutData widgetData) {
      return null;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

   @Override
   public void handleSaveButtonPressed() {
   }

   @Override
   public boolean isSaveButtonAvailable() {
      return false;
   }

}
