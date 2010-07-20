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
package org.eclipse.osee.coverage.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.OseeCoverageUnitStore;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditAssigneesAction extends Action {
   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final IRefreshable refreshable;

   public EditAssigneesAction(ISelectedCoverageEditorItem selectedCoverageEditorItem, IRefreshable refreshable, ISaveable saveable) {
      super("Edit Assignees");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.refreshable = refreshable;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.USER);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().isEmpty()) {
         AWorkbench.popup("Select Coverage Unit(s)");
         return;
      }
      for (ICoverage coverage : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (!(coverage instanceof CoverageUnit)) {
            AWorkbench.popup("Assignees can only be set on Coverage Units");
            return;
         }
      }
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }

      try {
         Set<User> initalUsers = new HashSet<User>();
         for (ICoverage coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
            if (coverageItem.isAssignable()) {
               if (Strings.isValid(((CoverageUnit) coverageItem).getAssignees())) {
                  initalUsers.addAll(UsersByIds.getUsers(((CoverageUnit) coverageItem).getAssignees()));
               }
            }
         }

         UserCheckTreeDialog uld = new UserCheckTreeDialog();
         uld.setInitialSelections(initalUsers);
         uld.setMessage("Select to assign.\nDeSelect to un-assign.");
         if (uld.open() == 0) {
            Collection<User> users = uld.getUsersSelected();
            Set<ICoverage> coveragesToSave = new HashSet<ICoverage>();
            for (ICoverage coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
               if (coverageItem.isAssignable()) {
                  OseeCoverageUnitStore.setAssignees(((CoverageUnit) coverageItem), users);
                  refreshable.update(coverageItem);
                  coveragesToSave.add(coverageItem);
               }
            }
            saveable.save(coveragesToSave);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
