/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.relations;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeDragAndDrop;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeRelationsTab extends WfeAbstractTab implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {
   public final static String ID = "ats.relations.tab";
   private final WorkflowEditor editor;
   private final Artifact artifact;
   private final FormToolkit toolkit;
   private RelationsComposite relationComposite;
   private ScrolledForm scrolledForm;

   public WfeRelationsTab(WorkflowEditor editor, IAtsWorkItem workItem) {
      super(editor, ID, editor.getWorkItem(), "Relations");
      this.editor = editor;
      this.artifact = (Artifact) workItem;
      this.toolkit = editor.getToolkit();
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      bodyComp = scrolledForm.getBody();
      bodyComp.setLayout(new GridLayout(1, true));
      bodyComp.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));

      Label dragDropLabel = new Label(bodyComp, SWT.BORDER);
      dragDropLabel.setText("Click here to drag this \"" + artifact.getArtifactTypeName() + "\"");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dragDropLabel.setLayoutData(gd);
      new WfeDragAndDrop(dragDropLabel, (AbstractWorkflowArtifact) workItem.getStoreObject(), WorkflowEditor.EDITOR_ID);
      toolkit.adapt(dragDropLabel, true, true);

      relationComposite = new RelationsComposite(editor, bodyComp, SWT.BORDER, artifact);
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      addListener();

      updateTitleBar(managedForm);
      createToolbar(managedForm);
      FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

   }

   private void addListener() {
      relationComposite.getTreeViewer().getTree().addMouseListener(new MouseListener() {

         private void redrawPage() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  relationComposite.layout();
                  relationComposite.getParent().layout();
                  getManagedForm().reflow(true);
               }
            });
         }

         @Override
         public void mouseDoubleClick(MouseEvent e) {
            // do nothing
         }

         @Override
         public void mouseDown(MouseEvent e) {
            redrawPage();
         }

         @Override
         public void mouseUp(MouseEvent e) {
            // do nothing
         }
      });
   }

   @Override
   public void refreshActionHandler() {
      artifact.reloadAttributesAndRelations();
      refresh();
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(relationComposite)) {
         relationComposite.refresh();
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(new BranchIdEventFilter(artifact.getBranch()));
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return Arrays.asList(new BranchIdTopicEventFilter(artifact.getBranch()));
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      // do nothing
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      // do nothing
   }

}
