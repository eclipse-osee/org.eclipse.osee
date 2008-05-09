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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactEditor extends FormEditor implements IDirtiableEditor {
   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#createToolkit(org.eclipse.swt.widgets.Display)
    */
   @Override
   protected XFormToolkit createToolkit(Display display) {
      // Create a toolkit that shares colors between editors.
      // the toolkit will be disposed by the super class (FormEditor)
      return new XFormToolkit(SkynetGuiPlugin.getInstance().getSharedFormColors(display));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.EditorPart#doSaveAs()
    */
   @Override
   public void doSaveAs() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
    */
   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.swt.IDirtiableEditor#onDirtied()
    */
   public void onDirtied() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#getToolkit()
    */
   @Override
   public XFormToolkit getToolkit() {
      return (XFormToolkit) super.getToolkit();
   }
}