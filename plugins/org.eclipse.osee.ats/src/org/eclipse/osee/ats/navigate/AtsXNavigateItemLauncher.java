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
package org.eclipse.osee.ats.navigate;

import java.util.Collection;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorParameterSearchItem;
import org.eclipse.osee.ats.task.TaskEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class AtsXNavigateItemLauncher {

   public static void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      Collection<TableLoadOption> tableLoadOpts = Collections.getAggregate(tableLoadOptions);
      boolean dontCopyWsi = tableLoadOpts.contains(TableLoadOption.DontCopySearchItem);
      if (item instanceof SearchNavigateItem) {
         WorldSearchItem worldSearchItem = ((SearchNavigateItem) item).getWorldSearchItem();
         if (worldSearchItem.getLoadView() == LoadView.WorldEditor) {
            if (worldSearchItem instanceof WorldUISearchItem) {
               WorldEditor.open(new WorldEditorUISearchItemProvider(
                  (WorldUISearchItem) (dontCopyWsi ? worldSearchItem : worldSearchItem.copy()), null, tableLoadOptions));
            } else if (worldSearchItem instanceof WorldEditorParameterSearchItem) {
               WorldEditor.open(new WorldEditorParameterSearchItemProvider(
                  (WorldEditorParameterSearchItem) (dontCopyWsi ? worldSearchItem : worldSearchItem.copy()), null,
                  tableLoadOptions));
            } else {
               AWorkbench.popup("ERROR", "Unhandled WorldEditor navigate item");
            }
         } else if (worldSearchItem.getLoadView() == LoadView.TaskEditor) {
            if (worldSearchItem instanceof TaskEditorParameterSearchItem) {
               TaskEditor.open(new TaskEditorParameterSearchItemProvider(
                  (TaskEditorParameterSearchItem) (dontCopyWsi ? worldSearchItem : worldSearchItem.copy()), null,
                  tableLoadOptions));
            } else {
               AWorkbench.popup("ERROR", "Unhandled TaskEditor navigate item");
            }
         } else {
            AWorkbench.popup("ERROR", "Unhandled navigate item");
         }
      } else {
         try {
            item.run(tableLoadOptions);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
