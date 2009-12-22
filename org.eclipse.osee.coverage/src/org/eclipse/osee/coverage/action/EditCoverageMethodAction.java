/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.coverage.util.dialog.CoverageMethodListDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class EditCoverageMethodAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;
   private final ISaveable saveable;
   private final IRefreshable refreshable;
   private final CoverageXViewer coverageXViewer;

   public EditCoverageMethodAction(CoverageXViewer coverageXViewer, ISelectedCoverageEditorItem selectedCoverageEditorItem, IRefreshable refreshable, ISaveable saveable) {
      super("Edit Coverage Method");
      this.coverageXViewer = coverageXViewer;
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
      this.refreshable = refreshable;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() == 0) {
         AWorkbench.popup("Select Coverage Item(s)");
         return;
      }
      for (ICoverage coverage : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
         if (!(coverage instanceof CoverageItem)) {
            AWorkbench.popup("Coverage Method can only be set on Coverage Items");
            return;
         }
      }

      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }

      CoverageMethodListDialog dialog =
            new CoverageMethodListDialog(coverageXViewer.getCoverageOptionManager().getEnabled());
      if (dialog.open() == 0) {
         Set<ICoverage> coveragesToSave = new HashSet<ICoverage>();
         for (ICoverage coverageItem : selectedCoverageEditorItem.getSelectedCoverageEditorItems()) {
            if (coverageItem instanceof CoverageItem) {
               ((CoverageItem) coverageItem).setCoverageMethod((CoverageOption) dialog.getFirstResult());
               refreshable.update(coverageItem);
               coveragesToSave.add(coverageItem);
            }
         }
         try {
            saveable.save(coveragesToSave);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            return;
         }
      }
   }
}
