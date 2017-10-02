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
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Donald G. Dunne
 */
public class NewActionWizard extends Wizard implements INewWizard {
   protected NewActionPage1 page1;
   protected NewActionPage2 page2;
   protected NewActionPage3 page3;
   private Collection<IAtsActionableItem> initialAias, selectableAis;
   private String initialDescription;
   private NewActionJob job = null;
   private INewActionListener newActionListener;
   private boolean openOnComplete = true;;

   @Override
   public boolean performFinish() {
      try {
         Result result = isActionValid();
         if (result.isFalse()) {
            AWorkbench.popup(result);
            return false;
         }
         job = new NewActionJob(getTitle(), getDescription(), getChangeType(), getPriority(), getNeedBy(),
            getValidation(), getSelectedIAtsActionableItems(), this, newActionListener);
         job.setUser(true);
         job.setOpenOnComplete(openOnComplete);
         job.setPriority(Job.LONG);
         job.schedule();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return false;
      }
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      // do nothing
   }

   @Override
   public void addPages() {
      page1 = new NewActionPage1(this);
      addPage(page1);
      page2 = new NewActionPage2(this);
      addPage(page2);
   }

   @Override
   public boolean canFinish() {
      return page3 == null ? page2.isPageComplete() : page3.isPageComplete();
   }

   public void createPage3IfNecessary() {
      if (page3 == null && NewActionPage3.isPage3Necesary(getSelectedIAtsActionableItems())) {
         page3 = new NewActionPage3(this);
         addPage(page3);
      }
   }

   public boolean isTTAction() {
      return getTitle().equals("tt");
   }

   public String getTitle() {
      return ((XText) page1.getXWidget(NewActionPage1.TITLE)).get();
   }

   public Set<IAtsActionableItem> getSelectedIAtsActionableItems() {
      return page1.getSelectedIAtsActionableItems();
   }

   public String getDescription() {
      return ((XText) page2.getXWidget(NewActionPage2.DESCRIPTION)).get();
   }

   public String getPriority() {
      // Must use skynet attribute name cause this widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
      return ((XCombo) page2.getXWidget(NewActionPage2.PRIORITY)).get();
   }

   public ChangeType getChangeType() {
      // Must use skynet attribute name cause this widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
      return ChangeType.getChangeType(((XCombo) page2.getXWidget(NewActionPage2.CHANGE_TYPE)).get());
   }

   public boolean getValidation() {
      return ((XCheckBox) page2.getXWidget(NewActionPage2.VALIDATION_REQUIRED)).isChecked();
   }

   public Date getNeedBy() {
      return ((XDate) page2.getXWidget(NewActionPage2.DEADLINE)).getDate();
   }

   public void notifyAtsWizardItemExtensions(ActionResult actionResult, IAtsChangeSet changes) {
      if (page3 != null) {
         page3.notifyAtsWizardItemExtensions(actionResult, changes);
      }
   }

   public XWidget getExtendedXWidget(String attrName) {
      if (page3 == null) {
         return null;
      }
      return page3.getXWidget(attrName);
   }

   public Result isActionValid() {
      if (page3 == null) {
         return Result.TrueResult;
      }
      return page3.isActionValid();
   }

   public String getInitialDescription() {
      return initialDescription;
   }

   public void setInitialDescription(String initialDescription) {
      this.initialDescription = initialDescription;
   }

   public Collection<IAtsActionableItem> getInitialAias() {
      return initialAias;
   }

   public void setInitialAias(Collection<IAtsActionableItem> initialAias) {
      this.initialAias = initialAias;
   }

   public INewActionListener getNewActionListener() {
      return newActionListener;
   }

   public void setNewActionListener(INewActionListener newActionListener) {
      this.newActionListener = newActionListener;
   }

   public void setOpenOnComplete(boolean openOnComplete) {
      this.openOnComplete = openOnComplete;
   }

   public void getSelectableAis(Collection<IAtsActionableItem> ais) {
      this.selectableAis = ais;
   }

   public Collection<IAtsActionableItem> getSelectableAis() {
      return selectableAis;
   }

}
