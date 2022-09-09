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

package org.eclipse.osee.ats.ide.editor.tab.workflow.note;

import java.util.Collection;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Donald G. Dunne
 */
public class NewStateNoteWizard extends Wizard {
   public NewStateNotePage mainPage;
   private final Collection<String> stateNames;

   public NewStateNoteWizard(Collection<String> stateNames) {
      super();
      this.stateNames = stateNames;
      setWindowTitle("New State Note Wizard");
   }

   @Override
   public boolean performFinish() {
      return true;
   }

   @Override
   public void addPages() {
      mainPage = new NewStateNotePage(this);
      addPage(mainPage);
   }

   @Override
   public boolean canFinish() {
      return mainPage.isPageComplete();
   }

   public Collection<String> getStateNames() {
      return stateNames;
   }
}
