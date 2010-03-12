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
package org.eclipse.osee.framework.ui.skynet.blam.sections;

import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
public class BaseBlamSection extends SectionPart {

   private final FormEditor editor;
   private final AbstractBlam abstractBlam;

   /**
    * @param parent
    * @param toolkit
    * @param style
    */
   public BaseBlamSection(FormEditor editor, AbstractBlam abstractBlam, Composite parent, FormToolkit toolkit, int style) {
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
