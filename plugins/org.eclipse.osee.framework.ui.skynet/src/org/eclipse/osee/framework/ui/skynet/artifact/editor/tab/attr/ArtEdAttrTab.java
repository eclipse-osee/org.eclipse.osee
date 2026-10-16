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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.header.ArtEdBaseTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.AddAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.DeleteAttributeAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.action.EditAttributeAction;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class ArtEdAttrTab extends ArtEdBaseTab implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {

   public final static String ID = "art.editor.attr.tab";
   private ArtEdAttrXViewer xViewer;
   private Artifact artifactOverride;

   public ArtEdAttrTab(ArtifactEditor editor, Artifact artifact) {
      super(editor, ID, "Attributes (Adm)");
      OseeEventManager.addListener(this);
   }

   /**
    * Constructor for embedding in non-ArtifactEditor contexts (e.g., WorkflowEditor). Pass the FormEditor and artifact
    * directly. Does not use the common header — call createFormContentShared() to create just the XViewer body.
    */
   public ArtEdAttrTab(org.eclipse.ui.forms.editor.FormEditor editor, Artifact artifact) {
      super(editor, ID, "Attributes (Adm)");
      this.artifactOverride = artifact;
      OseeEventManager.addListener(this);
   }

   @Override
   public Artifact getArtifact() {
      if (artifactOverride != null) {
         return artifactOverride;
      }
      return super.getArtifact();
   }

   /**
    * Creates just the XViewer body without the ArtEdTabHeader. For embedding in other editors (e.g., WFE).
    */
   public void createFormContentShared(IManagedForm managedForm) {
      ScrolledForm form = managedForm.getForm();
      form.setExpandHorizontal(true);
      form.setExpandVertical(true);
      Composite body = form.getBody();
      body.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));
      body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createXViewerBody(body, managedForm.getToolkit());
      body.layout(true);
   }

   @Override
   protected String getTabName() {
      return "Attributes (Admin)";
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      // Disable outer form scrolling - the XViewer handles its own
      ScrolledForm form = managedForm.getForm();
      form.setExpandHorizontal(true);
      form.setExpandVertical(true);
      super.createFormContent(managedForm);
   }

   @Override
   protected void createSectionToolbar(Composite toolbarParent) {
      ToolBarManager sectionToolBar = new ToolBarManager(SWT.FLAT);
      sectionToolBar.createControl(toolbarParent);
      sectionToolBar.getControl().setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
      sectionToolBar.getControl().setBackground(
         toolbarParent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
      sectionToolBar.add(new AddAttributeAction(xViewer));
      sectionToolBar.add(new EditAttributeAction(xViewer));
      sectionToolBar.add(new DeleteAttributeAction(xViewer));
      sectionToolBar.add(new Separator());
      sectionToolBar.add(xViewer.getCustomizeAction());
      sectionToolBar.update(true);
   }

   @Override
   protected void createTabBody(Composite parent, FormToolkit toolkit) {
      createXViewerBody(parent, toolkit);
   }

   private void createXViewerBody(Composite parent, FormToolkit toolkit) {
      Artifact artifact = getArtifact();

      Composite mainComp = new Composite(parent, SWT.NONE);
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComp.setLayout(ALayout.getZeroMarginLayout());

      Label messageLabel = new Label(mainComp, SWT.NONE);
      messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      messageLabel.setText("");
      messageLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

      xViewer = new ArtEdAttrXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, null, artifact);
      xViewer.setContentProvider(new ArtEdAttrContentProvider());
      xViewer.setLabelProvider(new ArtEdAttrLabelProvider(xViewer));
      xViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      if (getSite() != null) {
         getSite().setSelectionProvider(xViewer);
      }

      xViewer.loadTable(artifact);
   }

   @Override
   protected void refreshTabBody() {
      if (xViewer != null) {
         xViewer.loadTable(getArtifact());
      }
   }

   @Override
   public void refreshActionHandler() {
      getArtifact().reloadAttributesAndRelations();
      refreshTabBody();
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (xViewer != null && artifactEvent.isModified(getArtifact())) {
         xViewer.loadTable(getArtifact());
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (xViewer != null && artifactTopicEvent.isModified(getArtifact())) {
         xViewer.loadTable(getArtifact());
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(new BranchIdEventFilter(getArtifact().getBranch()));
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return Arrays.asList(new BranchIdTopicEventFilter(getArtifact().getBranch()));
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   public ArtEdAttrXViewer getxViewer() {
      return xViewer;
   }
}
