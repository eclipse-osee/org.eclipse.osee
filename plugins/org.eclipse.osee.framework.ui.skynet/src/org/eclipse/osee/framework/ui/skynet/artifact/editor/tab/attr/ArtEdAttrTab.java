/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
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
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrTab extends FormPage implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {
   private Composite bodyComp;
   private ScrolledForm scrolledForm;
   public final static String ID = "art.editor.attr.tab";
   private Artifact artifact;
   private ArtEdAttrXViewer xViewer;
   private ArtEdAttrToolbar toolBar;
   private Label messageLabel;
   private final ArtEdAttrTab fTab;

   public ArtEdAttrTab(FormEditor editor, Artifact artifact) {
      super(editor, ID, "Attributes (Adm)");
      this.artifact = artifact;
      fTab = this;
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      createFormContentShared(managedForm);
   }

   public void createFormContentShared(IManagedForm managedForm) {
      try {
         scrolledForm = managedForm.getForm();
         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);
         bodyComp.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               OseeEventManager.removeListener(fTab);
            }
         });

         final Composite mainComp = new Composite(bodyComp, SWT.BORDER);
         mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
         mainComp.setLayout(ALayout.getZeroMarginLayout());
         managedForm.getToolkit().paintBordersFor(mainComp);

         messageLabel = new Label(mainComp, SWT.NONE);
         messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         messageLabel.setText("");
         messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         managedForm.getToolkit().adapt(messageLabel, true, true);

         xViewer = new ArtEdAttrXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, null, artifact);
         xViewer.setContentProvider(new ArtEdAttrContentProvider());
         xViewer.setLabelProvider(new ArtEdAttrLabelProvider(xViewer));
         GridData gd2 = new GridData(GridData.FILL_BOTH);
         gd2.minimumHeight = 300;
         gd2.minimumWidth = 300;
         gd2.heightHint = 300;
         gd2.widthHint = 300;
         xViewer.getTree().setLayoutData(gd2);
         if (getSite() != null) {
            getSite().setSelectionProvider(xViewer);
         }

         xViewer.loadTable(artifact);

         toolBar = new ArtEdAttrToolbar(scrolledForm, xViewer, this);
         toolBar.build();

         scrolledForm.setText("Attributes");
         scrolledForm.setImage(ImageManager.getImage(FrameworkImage.ARTIFACT_EDITOR));

         mainComp.layout(true);
         managedForm.reflow(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      xViewer.loadTable(artifact);
   }

   @Override
   public void refreshActionHandler() {
      artifact = artifact.reloadAttributesAndRelations();
      xViewer.loadTable(artifact);
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (xViewer != null && artifactEvent.isModified(artifact)) {
         xViewer.loadTable(artifact);
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (xViewer != null && artifactTopicEvent.isModified(artifact)) {
         xViewer.loadTable(artifact);
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

   public Artifact getArtifact() {
      return artifact;
   }

   public ArtEdAttrXViewer getxViewer() {
      return xViewer;
   }

}
