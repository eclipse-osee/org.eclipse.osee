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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Shows state information for any state that is no-longer defined by Work Definition.
 * 
 * @author Donald G. Dunne
 */
public class WfeUndefinedStateSection extends SectionPart {

   private final WorkflowEditor editor;
   private boolean sectionCreated = false;
   private final String stateName;

   public WfeUndefinedStateSection(String stateName, WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.stateName = stateName;
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      try {
         section.setText("Un-Defined State - " + WfeWorkflowSection.getCurrentStateTitle(editor.getAwa(), stateName,
            false, false, false));
      } catch (OseeCoreException ex) {
         section.setText(stateName + " - Exception:" + ex.getLocalizedMessage());
      }
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection();
         }
      });
   }

   public static boolean hasUndefinedStates(AbstractWorkflowArtifact awa)  {
      return !getUndefinedStateNames(awa).isEmpty();
   }

   public List<String> getUndefinedStateNames()  {
      return getUndefinedStateNames(editor.getAwa());
   }

   public static List<String> getUndefinedStateNames(AbstractWorkflowArtifact awa)  {
      // Display pages that are in data store, but not in Work Definition
      Collection<String> stateNamesDefined =
         AtsClientService.get().getWorkDefinitionService().getStateNames(awa.getWorkDefinition());
      List<String> stateNamesUndefined = new ArrayList<>();
      for (String pageName : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
         String justPage = pageName.replaceFirst(";.*$", "");
         if (!stateNamesDefined.contains(justPage)) {
            stateNamesUndefined.add(justPage);
         }
      }
      return stateNamesUndefined;
   }

   private synchronized void createSection() {
      if (sectionCreated) {
         return;
      }

      AbstractWorkflowArtifact awa = editor.getAwa();
      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(getSection(), SWT.WRAP);
      composite.setLayout(new GridLayout(1, true));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      toolkit.createLabel(composite, "This state is no-longer defined by the current Work Definition.", SWT.NONE);

      String infoStr = "";
      try {
         infoStr = String.format("Name: [%s] Assignees: [%s] Hours Spent: [%s]", stateName,
            awa.getStateMgr().getAssigneesStr(stateName, 100), awa.getStateMgr().getHoursSpent(stateName));
      } catch (OseeCoreException ex) {
         infoStr = "Exception processing state data (see log for details) " + ex.getLocalizedMessage();
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      toolkit.createLabel(composite, infoStr, SWT.NONE);

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);
      sectionCreated = true;

   }
}
