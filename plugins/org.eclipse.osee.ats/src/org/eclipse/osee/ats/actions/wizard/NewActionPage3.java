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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class NewActionPage3 extends WizardPage {
   private XWidgetPage page;
   private Composite comp;
   protected final NewActionWizard wizard;
   private static Set<IAtsWizardItem> wizardExtensionItems = new HashSet<>();

   public NewActionPage3(NewActionWizard wizard) {
      this(wizard, "Create new ATS Action", "Create ATS Action");
   }

   public NewActionPage3(NewActionWizard wizard, String pageName, String title) {
      super(pageName, title, null);
      this.wizard = wizard;
      setMessage("Enter requested information and select Finish.");
      getWizardXWidgetExtensions();
   }

   public void notifyAtsWizardItemExtensions(ActionResult actionResult, IAtsChangeSet changes) {
      for (IAtsWizardItem item : wizardExtensionItems) {
         try {
            if (item.hasWizardXWidgetExtensions(wizard.getSelectedIAtsActionableItems())) {
               item.wizardCompleted(actionResult, wizard, changes);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      createPage(comp);
      getShell().layout();
   }

   public void createPage(Composite comp) {
      try {
         if (page == null) {
            StringBuffer stringBuffer = new StringBuffer(500);
            stringBuffer.append("<WorkPage>");
            IDynamicWidgetLayoutListener dynamicWidgetLayoutListener = null;
            for (IAtsWizardItem item : wizardExtensionItems) {
               boolean hasWizardXWidgetExtensions =
                  item.hasWizardXWidgetExtensions(wizard.getSelectedIAtsActionableItems());
               if (hasWizardXWidgetExtensions) {
                  stringBuffer.append(
                     "<XWidget displayName=\"--- Extra fields for " + item.getName() + " ---\" xwidgetType=\"XLabel\" horizontalLabel=\"true\" toolTip=\"These fields are available for only the team workflow specified here.\"/>");
                  try {
                     if (item instanceof IDynamicWidgetLayoutListener) {
                        dynamicWidgetLayoutListener = (IDynamicWidgetLayoutListener) item;
                     }
                     item.getWizardXWidgetExtensions(wizard.getSelectedIAtsActionableItems(), stringBuffer);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
            stringBuffer.append("</WorkPage>");
            page = new XWidgetPage(stringBuffer.toString(), ATSXWidgetOptionResolver.getInstance(),
               dynamicWidgetLayoutListener);
            page.createBody(null, comp, null, xModListener, true);

            comp.layout();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void createControl(Composite parent) {

      comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      setControl(comp);
   }

   public void update() {
      getContainer().updateButtons();
   }

   @Override
   public boolean isPageComplete() {
      if (page == null || !page.isPageComplete().isTrue()) {
         return false;
      }
      // Check wizard extension item validation
      for (IAtsWizardItem item : wizardExtensionItems) {
         try {
            if (item.hasWizardXWidgetExtensions(wizard.getSelectedIAtsActionableItems())) {
               Result result = item.isWizardXWidgetsComplete(wizard);
               if (result.isFalse()) {
                  setErrorMessage(result.getText());
                  return false;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      setErrorMessage(null);
      return true;
   }

   public XWidget getXWidget(String attrName) throws OseeCoreException {
      Conditions.checkNotNull(page, "WorkPage");
      return page.getLayoutData(attrName).getXWidget();
   }

   private static void getWizardXWidgetExtensions() {
      if (!wizardExtensionItems.isEmpty()) {
         return;
      }

      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsWizardItem");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access AtsWizardItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsWizardItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     wizardExtensionItems.add((IAtsWizardItem) obj);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading AtsWizardItem extension", ex);
                  }
               }

            }
         }
      }
   }

   public static boolean isPage3Necesary(Collection<IAtsActionableItem> aias) {
      getWizardXWidgetExtensions();
      for (IAtsWizardItem item : wizardExtensionItems) {
         try {
            if (item.hasWizardXWidgetExtensions(aias)) {
               return true;
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return false;
   }

   public Result isActionValid() {
      getWizardXWidgetExtensions();
      for (IAtsWizardItem item : wizardExtensionItems) {
         try {
            if (item.hasWizardXWidgetExtensions(wizard.getSelectedIAtsActionableItems())) {
               Result result = item.isActionValidToCreate(wizard.getSelectedIAtsActionableItems(), wizard);
               if (result.isFalse()) {
                  return result;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return Result.TrueResult;
   }

}
