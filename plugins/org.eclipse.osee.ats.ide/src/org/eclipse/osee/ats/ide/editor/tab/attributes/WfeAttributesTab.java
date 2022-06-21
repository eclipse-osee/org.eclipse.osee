/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.attributes;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
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
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrTab;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrToolbar;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr.ArtEdAttrXViewer;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeAttributesTab extends WfeAbstractTab implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {
   private ScrolledForm scrolledForm;
   public final static String ID = "ats.attributes.tab";
   private final WorkflowEditor editor;
   private ArtEdAttrTab artEdAttrTab;
   private Artifact artifact;
   private ArtEdAttrXViewer xViewer;

   public WfeAttributesTab(WorkflowEditor editor, IAtsWorkItem workItem) {
      super(editor, ID, editor.getWorkItem(), "Attributes");
      this.editor = editor;
      this.artifact = (Artifact) workItem;
      OseeEventManager.addListener(this);
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      bodyComp = scrolledForm.getBody();

      artEdAttrTab = new ArtEdAttrTab(editor, editor.getWorkItem());
      artEdAttrTab.createFormContentShared(managedForm);
      getSite().setSelectionProvider(xViewer);

      artifact = artEdAttrTab.getArtifact();
      xViewer = artEdAttrTab.getxViewer();

      updateTitleBar(managedForm);
      createToolbar(managedForm);
      FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

   }

   @Override
   public void refreshActionHandler() {
      artifact = artifact.reloadAttributesAndRelations();
      refresh();
   }

   @Override
   public void refresh() {
      if (xViewer != null) {
         xViewer.loadTable(artifact);
      }
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

   @Override
   public IToolBarManager createToolbar(IManagedForm managedForm) {
      IToolBarManager toolBarMgr = scrolledForm.getToolBarManager();
      toolBarMgr.removeAll();

      ArtEdAttrToolbar artEdAttrToolbar = new ArtEdAttrToolbar(scrolledForm, xViewer, new IRefreshActionHandler() {

         @Override
         public void refreshActionHandler() {
            artEdAttrTab.refreshActionHandler();
         }
      });
      artEdAttrToolbar.addAttrToolbarActions(toolBarMgr);
      super.createToolbar(managedForm);
      return toolBarMgr;
   }

}
