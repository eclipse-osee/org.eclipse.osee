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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSelectWizard extends Wizard {

   private ArtifactSelectWizardPage wizardPage;

   public ArtifactSelectWizard() {
   }

   public void addPages() {
      wizardPage = new ArtifactSelectWizardPage();
      addPage(wizardPage);
   }

   public boolean performFinish() {
      return true;
   }

   @Override
   public boolean canFinish() {
      return wizardPage.isPageComplete();
   }

   public Artifact getSelectedArtifact() {
      return wizardPage.getSelectedArtifact();
   }

}
