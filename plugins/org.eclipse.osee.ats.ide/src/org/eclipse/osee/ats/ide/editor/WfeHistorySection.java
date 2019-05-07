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
package org.eclipse.osee.ats.ide.editor;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.history.XHistoryViewer;
import org.eclipse.osee.ats.ide.editor.log.XLogViewer;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
public class WfeHistorySection extends SectionPart implements IWfeEventHandle {

   private final WorkflowEditor editor;
   private boolean sectionCreated = false;
   private final IAtsWorkItem workItem;
   private XHistoryViewer xHistoryViewer;
   private XLogViewer xLogViewer;

   public WfeHistorySection(WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
      workItem = editor.getWorkItem();
      if (workItem.getStoreObject() instanceof Artifact) {
         editor.registerEvent(this, (Artifact) workItem.getStoreObject());
      }
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
      Composite composite = toolkit.createComposite(getSection(), SWT.WRAP);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label logLabel = toolkit.createLabel(composite, "ATS Log:", SWT.NONE);
      logLabel.setFont(FontManager.getCourierNew12Bold());
      xLogViewer = new XLogViewer(awa);
      xLogViewer.createWidgets(composite, 2);

      Label historyLabel = toolkit.createLabel(composite, "Detailed History (if available):", SWT.NONE);
      historyLabel.setFont(FontManager.getCourierNew12Bold());
      xHistoryViewer = new XHistoryViewer(awa);
      xHistoryViewer.createWidgets(composite, 2);

      Label button = toolkit.createLabel(composite, "   ", SWT.NONE);
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

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);
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

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

}
