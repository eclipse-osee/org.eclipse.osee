/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Jeff C. Phillips
 *
 */
public class UpdateArtifactStatusDialog extends MessageDialog{
   private static final String TITLE = "Confirm Action";
   private List<TransferObjects> transferObjects;
   
   public UpdateArtifactStatusDialog(List<TransferObjects> transferObjects) {
      super(Display.getCurrent().getActiveShell(), TITLE, null, null, MessageDialog.NONE,
            new String[] {"Ok", "Cancel"}, 0);
      this.transferObjects = transferObjects;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      TreeViewer listViewer = new TreeViewer(container);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 250;
      gridData.widthHint = 500;
//      listViewer.getControl().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
      listViewer.getControl().setLayoutData(gridData);
      listViewer.setContentProvider(new UpdateContentProvider());
      listViewer.setLabelProvider(new UpdateDecoratingLabelProvider(new UpdateLabelProvider()));
      listViewer.setInput(transferObjects);
      
      return listViewer.getControl();
   }
}
