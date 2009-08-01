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

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.branch.BranchCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class XSelectFromMultiChoiceBranch extends XSelectFromDialog<Branch> {

   public XSelectFromMultiChoiceBranch(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);
      getStyledText().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   @Override
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      return new BranchCheckTreeDialog(getLabel(), "Select from the items below", 1, Integer.MAX_VALUE);
   }

}
