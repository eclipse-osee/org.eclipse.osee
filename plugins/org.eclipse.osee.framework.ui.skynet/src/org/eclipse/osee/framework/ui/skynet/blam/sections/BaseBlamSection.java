/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.blam.sections;

import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class BaseBlamSection extends SectionPart {

   protected final BlamEditor editor;
   protected final AbstractBlam abstractBlam;

   public BaseBlamSection(BlamEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style);
      this.editor = editor;
      this.abstractBlam = abstractBlam;
   }

   public FormEditor getEditor() {
      return editor;
   }

   public AbstractBlam getAbstractBlam() {
      return abstractBlam;
   }

}
