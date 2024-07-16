/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.ide.config.editor.AtsConfigContentProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigCheckTreeDialog<T> extends FilteredCheckboxTreeDialog<T> {

   private List<T> initialConfigObjects;
   private final boolean requiredSelection;
   private final Collection<T> selectableConfigObjects;

   public AtsConfigCheckTreeDialog(String title, String message, Collection<T> selectableTeamDefs, boolean requiredSelection) {
      super(title, message, new AtsConfigContentProvider(), new AtsObjectLabelProvider(), new ToStringViewerSorter());
      this.selectableConfigObjects = selectableTeamDefs;
      this.requiredSelection = requiredSelection;
   }

   @Override
   public Collection<T> getChecked() {
      return super.getTreeViewer().getChecked();
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(selectableConfigObjects);
         if (getInitialConfigObjects() != null) {
            getTreeViewer().setInitalChecked(getInitialConfigObjects());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      super.isComplete();
      Result result = Result.TrueResult;
      try {
         if (requiredSelection && getChecked().isEmpty()) {
            result = new Result("Must select Item(s)");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return result;
   }

   public List<T> getInitialConfigObjects() {
      return initialConfigObjects;
   }

   public void setInitialConfigObjects(List<T> initialConfigObjects) {
      this.initialConfigObjects = initialConfigObjects;
   }

}
