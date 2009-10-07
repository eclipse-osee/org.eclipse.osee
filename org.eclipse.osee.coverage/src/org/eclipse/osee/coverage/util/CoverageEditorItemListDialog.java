/*
 * Created on Oct 6, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorItemListDialog extends org.eclipse.ui.dialogs.ListDialog {

   public CoverageEditorItemListDialog(String title, String message) {
      super(Display.getCurrent().getActiveShell());
      setTitle(title);
      setMessage(message);
      setContentProvider(new IStructuredContentProvider() {
         @SuppressWarnings("unchecked")
         public Object[] getElements(Object arg0) {
            return ((Collection) arg0).toArray();
         }

         public void dispose() {
         }

         public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
         }
      });
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof ICoverageEditorItem) {
               return ((ICoverageEditorItem) element).getName();
            }
            return "Unknown";
         }
      });

   }
}
