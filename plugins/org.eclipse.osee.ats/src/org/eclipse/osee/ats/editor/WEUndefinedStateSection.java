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
import java.util.logging.Level;
import org.eclipse.osee.ats.core.team.SimpleTeamState;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.WorkPageType;
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
 * @author Donald G. Dunne
 */
public class WEUndefinedStateSection extends SectionPart {

   private final SMAEditor editor;
   private boolean sectionCreated = false;

   public WEUndefinedStateSection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Un-Defined States");
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

   public static boolean hasUndefinedStates(AbstractWorkflowArtifact awa) throws OseeCoreException {
      return !getUndefinedStateNames(awa).isEmpty();
   }

   public List<String> getUndefinedStateNames() throws OseeCoreException {
      return getUndefinedStateNames(editor.getAwa());
   }

   public static List<String> getUndefinedStateNames(AbstractWorkflowArtifact awa) throws OseeCoreException {
      // Display pages that are in data store, but not in Work Definition
      Collection<String> stateNamesDefined = awa.getWorkDefinition().getStateNames();
      List<String> stateNamesUndefined = new ArrayList<String>();
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

      toolkit.createLabel(composite,
         "The following are visited states that are no-longer defined by the current Work Definition.", SWT.NONE);

      try {
         for (String stateName : getUndefinedStateNames()) {
            SimpleTeamState state = new SimpleTeamState(stateName, WorkPageType.Working);
            String infoStr = "";
            try {
               infoStr =
                  String.format("Name: [%s] Assignees: [%s] Hours Spent: [%s]", state.getPageName(),
                     awa.getStateMgr().getAssigneesStr(state, 100), awa.getStateMgr().getHoursSpent(state));
            } catch (OseeCoreException ex) {
               infoStr = "Exception processing state data (see log for details) " + ex.getLocalizedMessage();
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            toolkit.createLabel(composite, infoStr, SWT.NONE);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);
      sectionCreated = true;

   }
}
