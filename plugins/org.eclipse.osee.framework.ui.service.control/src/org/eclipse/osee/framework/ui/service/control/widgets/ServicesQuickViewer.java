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
package org.eclipse.osee.framework.ui.service.control.widgets;

import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class ServicesQuickViewer extends Composite {

   private FormattedText textArea;

   public ServicesQuickViewer(Composite parent, int style) {
      super(parent, style);
      create();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      textArea = new FormattedText(this, SWT.NONE);
      textArea.setTextAreaBackground(SWT.COLOR_WHITE);
   }

   public FormattedText getTextArea() {
      return textArea;
   }

}
