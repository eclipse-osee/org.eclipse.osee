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
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class NewNoteWizard extends Wizard {
   public NewNotePage mainPage;
   private final Collection<String> artifactNames;

   /**
    * 
    */
   public NewNoteWizard(Collection<String> artifactNames) {
      super();
      this.artifactNames = artifactNames;
      setWindowTitle("New Note Wizard");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
    *      org.eclipse.jface.viewers.IStructuredSelection)
    */
   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }

   /**
    * (non-Javadoc) Method declared on Wizard.
    */
   public void addPages() {
      mainPage = new NewNotePage(this);
      addPage(mainPage);
   }

   @Override
   public boolean canFinish() {
      return mainPage.isPageComplete();
   }

   public Collection<String> getArtifactNames() {
      return artifactNames;
   }
}
