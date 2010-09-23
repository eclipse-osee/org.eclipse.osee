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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.IWorkProductRelatable;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class RemoveWorkProductTaskAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final CoverageXViewer coverageXViewer;

   public RemoveWorkProductTaskAction(CoverageXViewer coverageXViewer, ISelectedCoverageEditorItem selectedCoverageEditorItem, ISaveable saveable) {
      super("Remove Work Product Task");
      this.coverageXViewer = coverageXViewer;
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DELETE);
   }

   @Override
   public void run() {
      if (coverageXViewer.getWorkProductTaskProvider().getWorkProductRelatedActions().isEmpty()) {
         AWorkbench.popup("No Work Product related Actions.\n\nMust related Work Product Actions first.");
         return;
      }

      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().isEmpty()) {
         AWorkbench.popup("Select Coverage Item(s) or Coverage Units(s)");
         return;
      }

      List<ICoverage> relatedCoverageItems = new ArrayList<ICoverage>();
      for (ICoverage coverage : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (coverage instanceof IWorkProductRelatable && Strings.isValid(((IWorkProductRelatable) coverage).getWorkProductGuid())) {
            relatedCoverageItems.add(coverage);
         }
      }
      if (relatedCoverageItems.isEmpty()) {
         AWorkbench.popup("No Coverage Item(s) and Coverage Units(s) related.");
         return;
      }

      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getText(),
         "Remove related Work Product Tasks?")) {
         try {
            for (ICoverage coverage : relatedCoverageItems) {
               if (coverage instanceof IWorkProductRelatable) {
                  ((IWorkProductRelatable) coverage).setWorkProductGuid("");
               }
            }
            saveable.save(relatedCoverageItems);
            coverageXViewer.getWorkProductTaskProvider().reload();
            for (ICoverage coverage : relatedCoverageItems) {
               if (coverage instanceof IWorkProductRelatable) {
                  ((IWorkProductRelatable) coverage).setWorkProductTask(null);
                  coverageXViewer.update(coverage);
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}
