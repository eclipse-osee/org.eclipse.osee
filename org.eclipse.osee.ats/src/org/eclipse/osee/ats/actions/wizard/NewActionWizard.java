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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XList.XListItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class NewActionWizard extends Wizard implements INewWizard {
   private NewActionPage1 page1;
   private NewActionPage2 page2;
   private NewActionPage3 page3;
   private Collection<ActionableItemArtifact> initialAias;
   private String initialDescription;

   /**
    * Wizard to create a new action artifact with ONE product
    */
   public NewActionWizard() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.wizard.Wizard#performFinish()
    */
   @Override
   public boolean performFinish() {
      try {
         Result result = isActionValid();
         if (result.isFalse()) {
            result.popup();
            return false;
         }
         NewActionJob job = null;
         job =
               new NewActionJob(getTitle(), getDescription(), getChangeType(), getPriority(), getNeedBy(),
                     getValidation(), getUserCommunities(), getSelectedActionableItemArtifacts(), this);
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
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
   @Override
   public void addPages() {
      page1 = new NewActionPage1(this);
      addPage(page1);
      page2 = new NewActionPage2(this);
      addPage(page2);
   }

   @Override
   public boolean canFinish() {
      return (page3 == null) ? page2.isPageComplete() : page3.isPageComplete();
   }

   public void createPage3IfNecessary() {
      if (page3 == null && NewActionPage3.isPage3Necesary(getSelectedActionableItemArtifacts())) {
         page3 = new NewActionPage3(this);
         addPage(page3);
      }
   }

   public boolean isTTAction() {
      return getTitle().equals("tt");
   }

   public String getTitle() {
      return ((XText) page1.getXWidget("Title")).get();
   }

   public Set<ActionableItemArtifact> getSelectedActionableItemArtifacts() {
      return page1.getSelectedActionableItemArtifacts();
   }

   public String getDescription() {
      return ((XText) page2.getXWidget("Description")).get();
   }

   public Set<String> getUserCommunities() {
      Set<String> items = new HashSet<String>();
      // Must use skynet attribute name cause this widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
      for (XListItem item : ((XList) page2.getXWidget("ats.User Community")).getSelected())
         items.add(item.getName());
      return items;
   }

   public PriorityType getPriority() throws OseeCoreException {
      // Must use skynet attribute name cause this widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
      return PriorityType.getPriority(((XCombo) page2.getXWidget("ats.Priority")).get());
   }

   public ChangeType getChangeType() {
      // Must use skynet attribute name cause this widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
      return ChangeType.getChangeType(((XCombo) page2.getXWidget("ats.Change Type")).get());
   }

   public boolean getValidation() {
      return ((XCheckBox) page2.getXWidget("Validation Required")).get();
   }

   public Date getNeedBy() {
      return ((XDate) page2.getXWidget("Deadline")).getDate();
   }

   public void notifyAtsWizardItemExtensions(ActionArtifact action, SkynetTransaction transaction) {
      if (page3 != null) {
         page3.notifyAtsWizardItemExtensions(action, transaction);
      }
   }

   public XWidget getExtendedXWidget(String attrName) {
      if (page3 == null) return null;
      return page3.getXWidget(attrName);
   }

   public Result isActionValid() {
      if (page3 == null) return Result.TrueResult;
      return page3.isActionValid();
   }

   /**
    * @return the initialDescription
    */
   public String getInitialDescription() {
      return initialDescription;
   }

   /**
    * @param initialDescription the initialDescription to set
    */
   public void setInitialDescription(String initialDescription) {
      this.initialDescription = initialDescription;
   }

   /**
    * @return the initialAias
    */
   public Collection<ActionableItemArtifact> getInitialAias() {
      return initialAias;
   }

   /**
    * @param initialAias the initialAias to set
    */
   public void setInitialAias(Collection<ActionableItemArtifact> initialAias) {
      this.initialAias = initialAias;
   }
}
