/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.config.wizard;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.AtsConfigManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Create new new .shape-file. Those files can be used with the ShapesEditor (see plugin.xml).
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigWizard extends Wizard implements INewWizard {

   private AtsConfigWizardPage1 page1;

   @Override
   public void addPages() {
      // add pages to this wizard
      addPage(page1);
   }

   public void init(IWorkbench workbench, IStructuredSelection selection) {
      // create pages for this wizard
      page1 = new AtsConfigWizardPage1(this);
   }

   @Override
   public boolean performFinish() {
      try {
         String namespace = page1.getNamespace();
         String teamDefName = page1.getTeamDefName();
         Collection<String> aias = page1.getActionableItems();
         Collection<String> versionNames = page1.getVersions();
         String workflowId = page1.getWorkflowId();
         AtsConfigManager.configureAtsForDefaultTeam(namespace, teamDefName, versionNames, aias, workflowId);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
      return true;
   }
}
