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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchViewImageHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Roberto E. Escobar
 */
public class XSelectFromMultiChoiceBranch extends XSelectFromDialog<Branch> {

   public XSelectFromMultiChoiceBranch(String displayLabel) {
      super(displayLabel);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog#createControls(org.eclipse.swt.widgets.Composite, int, boolean)
    */
   @Override
   public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);
      getStyledText().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog#createDialog()
    */
   @Override
   public CheckedTreeSelectionDialog createDialog() {
      CheckedTreeSelectionDialog dialog =
            new CheckedTreeSelectionDialog(Display.getCurrent().getActiveShell(), new BranchLabelProvider(),
                  new ArrayTreeContentProvider());
      dialog.setTitle(getLabel());
      dialog.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
      dialog.setMessage("Select from the items below");
      return dialog;
   }

   private final class BranchLabelProvider extends LabelProvider {
      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
       */
      @Override
      public Image getImage(Object element) {
         if (element instanceof Branch) {
            return BranchViewImageHandler.getImage(element, 0);
         }
         return null;
      }
   }

}
