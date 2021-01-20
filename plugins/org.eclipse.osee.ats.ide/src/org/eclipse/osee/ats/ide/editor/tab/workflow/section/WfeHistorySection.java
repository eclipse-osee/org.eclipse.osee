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

import java.util.logging.Level;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.history.XHistoryViewer;
import org.eclipse.osee.ats.ide.editor.tab.workflow.log.XLogViewer;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WfeHistorySection extends SectionPart {

   private final WorkflowEditor editor;
   private boolean sectionCreated = false;
   private XHistoryViewer xHistoryViewer;
   private XLogViewer xLogViewer;
   private Composite mainComp;

   public WfeHistorySection(WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Log / History");
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

   private synchronized void createSection() {
      if (sectionCreated) {
         return;
      }

      AbstractWorkflowArtifact awa = editor.getWorkItem();
      final FormToolkit toolkit = getManagedForm().getToolkit();
      mainComp = toolkit.createComposite(getSection(), SWT.WRAP);
      mainComp.setLayout(new GridLayout(1, false));
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label logLabel = toolkit.createLabel(mainComp, "ATS Log:", SWT.NONE);
      logLabel.setFont(FontManager.getCourierNew12Bold());
      xLogViewer = new XLogViewer(awa);
      xLogViewer.createWidgets(mainComp, 2);

      Label historyLabel = toolkit.createLabel(mainComp, "Detailed History (if available):", SWT.NONE);
      historyLabel.setFont(FontManager.getCourierNew12Bold());
      xHistoryViewer = new XHistoryViewer(awa);
      xHistoryViewer.createWidgets(mainComp, 2);

      Label button = toolkit.createLabel(mainComp, "   ", SWT.NONE);
      button.setText("    ");
      final AbstractWorkflowArtifact fSma = awa;
      button.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            try {
               RendererManager.open(fSma, PresentationType.GENERALIZED_EDIT);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

      getSection().setClient(mainComp);
      toolkit.paintBordersFor(mainComp);
      sectionCreated = true;

   }

   @Override
   public void refresh() {
      if (xHistoryViewer != null) {
         xHistoryViewer.refresh();
      }
      if (xLogViewer != null) {
         xLogViewer.refresh();
      }
   }

   public boolean isDisposed() {
      return mainComp == null || mainComp.isDisposed();
   }

}
