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

package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
         section.setText("Un-Defined State - " + WfeWorkflowSection.getCurrentStateTitle(editor.getWorkItem(),
            stateName, false, false));
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

   public static boolean hasUndefinedStates(AbstractWorkflowArtifact awa) {
      return !getUndefinedStateNames(awa).isEmpty();
   }

   public List<String> getUndefinedStateNames() {
      return getUndefinedStateNames(editor.getWorkItem());
   }

   public static List<String> getUndefinedStateNames(AbstractWorkflowArtifact awa) {
      // Display pages that are in data store, but not in Work Definition
      Collection<String> stateNamesDefined =
         AtsApiService.get().getWorkDefinitionService().getStateNames(awa.getWorkDefinition());
      List<String> stateNamesUndefined = new ArrayList<>();
      for (String pageName : awa.getLog().getVisitedStateNames()) {
         if (!stateNamesDefined.contains(pageName)) {
            stateNamesUndefined.add(pageName);
         }
      }
      return stateNamesUndefined;
   }

   private synchronized void createSection() {
      if (sectionCreated) {
         return;
      }

      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(getSection(), SWT.WRAP);
      composite.setLayout(new GridLayout(1, true));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      toolkit.createLabel(composite, "This state is no-longer defined by the current Work Definition.", SWT.NONE);

      String infoStr = "";
      try {
         infoStr = String.format("Name: [%s]", stateName);
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
