/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ViewSourceAction extends Action {

   private final ISelectedCoverageEditorItem selectedCoverageEditorItem;

   public ViewSourceAction(ISelectedCoverageEditorItem selectedCoverageEditorItem) {
      super("View Source");
      this.selectedCoverageEditorItem = selectedCoverageEditorItem;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().isEmpty()) {
         AWorkbench.popup("Select Coverage Item");
         return;
      }
      if (selectedCoverageEditorItem.getSelectedCoverageEditorItems().size() > 0) {
         ICoverage item = selectedCoverageEditorItem.getSelectedCoverageEditorItems().iterator().next();
         String highlightLine = null;

         try {
            if (item instanceof CoverageItem) {
               if (Strings.isValid(item.getName())) {
                  highlightLine = item.getName();
               }
               item = item.getParent();
            }
            // If order number then parent has full file contents
            // attempt to find line in file that matches
            if (Strings.isValid(item.getOrderNumber())) {
               if (item.getParent() != null) {
                  String itemLineText = item.getName();
                  String parentFileContents = item.getParent().getFileContents();
                  if (!Strings.isValid(parentFileContents)) {
                     AWorkbench.popup("No File Contents Available");
                     return;
                  }
                  String html = parentFileContents;
                  // mark text for method
                  if (Strings.isValid(itemLineText)) {
                     html = html.replaceAll(itemLineText, "HEREBEGIN" + itemLineText + "HEREEND");
                  }
                  // mark text for executable line
                  if (Strings.isValid(highlightLine)) {
                     html = html.replaceAll(highlightLine, "HEREBEGIN" + highlightLine + "HEREEND");
                  }
                  html = AHTML.textToHtml(html);
                  html = html.replaceAll(" ", "&nbsp;");
                  html = html.replaceAll("HEREBEGIN", "<FONT style=\"BACKGROUND-COLOR: yellow\">");
                  html = html.replaceAll("HEREEND", "</FONT>");
                  ResultsEditor.open("source",
                        CoverageUtil.getFullPathWithName(item.getParent()) + "[" + item.getName() + "]", html);
               } else {
                  AWorkbench.popup("No File Contents Available");
                  return;
               }
            }
            // If no order number, just open full text
            else {
               String text = item.getFileContents();
               if (!Strings.isValid(text)) {
                  AWorkbench.popup("No File Contents Available");
                  return;
               }
               String html = AHTML.textToHtml(text);
               html = html.replaceAll(" ", "&nbsp;");
               ResultsEditor.open("source", CoverageUtil.getFullPathWithName(item), html);
            }

         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }

      }
   }
}
