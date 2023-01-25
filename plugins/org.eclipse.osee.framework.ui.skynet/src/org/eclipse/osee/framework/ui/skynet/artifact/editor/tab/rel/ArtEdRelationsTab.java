/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.rel;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
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
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdRelationsTab extends FormPage implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {
   public final static String ID = "art.editor.relations.tab";
   private final Artifact artifact;
   private final FormToolkit toolkit;
   private RelationsComposite relationComposite;
   private ScrolledForm scrolledForm;
   private Composite bodyComp;
   private final FormEditor editor;
   private ArtEdRelToolbar toolBar;
   private final ArtEdRelationsTab rTab;

   public ArtEdRelationsTab(FormEditor editor, Artifact artifact) {
      super(editor, ID, "Relations");
      this.editor = editor;
      this.artifact = artifact;
      this.toolkit = editor.getToolkit();
      this.rTab = this;
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      bodyComp = scrolledForm.getBody();
      bodyComp.setLayout(new GridLayout(1, true));
      bodyComp.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));
      bodyComp.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            OseeEventManager.removeListener(rTab);
         }
      });

      Label dragDropLabel = new Label(bodyComp, SWT.BORDER);
      dragDropLabel.setText("Click here to drag this \"" + artifact.getArtifactTypeName() + "\"");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dragDropLabel.setLayoutData(gd);
      new ArtifactDragAndDrop(dragDropLabel, artifact, ArtifactEditor.EDITOR_ID);
      toolkit.adapt(dragDropLabel, true, true);

      relationComposite = new RelationsComposite((AbstractArtifactEditor) editor, bodyComp, SWT.BORDER, artifact);
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      toolBar = new ArtEdRelToolbar(scrolledForm, artifact, this);
      toolBar.build();

      scrolledForm.setText("Relations");
      scrolledForm.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EDITOR));

      addListener();

      HelpUtil.setHelp(bodyComp, OseeHelpContext.ARTIFACT_EDITOR__RELATIONS);

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
      if (Widgets.isAccessible(relationComposite) && artifactEvent.isHasEvent(artifact)) {
         relationComposite.refresh();
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (Widgets.isAccessible(relationComposite) && artifactTopicEvent.isHasEvent(artifact)) {
         relationComposite.refresh();
      }
   }

   public void refresh() {
      if (Widgets.isAccessible(relationComposite)) {
         relationComposite.refresh();
      }
   }

}
