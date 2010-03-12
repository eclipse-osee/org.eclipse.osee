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
public class ReflectArtifactStatusDialog extends MessageDialog{
   private static final String TITLE = "Confirm Inter Artifact Explorer Drop";
   private static final String OK = "Ok";
   private static final String CANCEL = "Cancel";
   private List<TransferObject> transferObjects;
   
   public ReflectArtifactStatusDialog(List<TransferObject> transferObjects) {
      super(Display.getCurrent().getActiveShell(), TITLE, null, null, MessageDialog.NONE,
            new String[] {OK, CANCEL}, 0);
      this.transferObjects = transferObjects;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      TreeViewer listViewer = new TreeViewer(container);
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 270;
      gridData.widthHint = 500;
      listViewer.getControl().setLayoutData(gridData);
      listViewer.setContentProvider(new ReflectContentProvider());
      listViewer.setLabelProvider(new ReflectDecoratingLabelProvider(new RevertLabelProvider()));
      listViewer.setInput(transferObjects);
      
      return listViewer.getControl();
   }
}
