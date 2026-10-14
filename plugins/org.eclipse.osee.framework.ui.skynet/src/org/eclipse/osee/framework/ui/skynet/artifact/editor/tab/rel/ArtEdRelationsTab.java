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
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.header.ArtEdBaseTab;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ArtEdRelationsTab extends ArtEdBaseTab implements IRefreshActionHandler, IArtifactEventListener, IArtifactTopicEventListener {

   public final static String ID = "art.editor.relations.tab";
   private RelationsComposite relationComposite;

   public ArtEdRelationsTab(ArtifactEditor editor, Artifact artifact) {
      super(editor, ID, "Relations");
      OseeEventManager.addListener(this);
   }

   @Override
   protected String getTabName() {
      return "Relations";
   }

   @Override
   protected void createSectionToolbar(Composite toolbarParent) {
      Artifact artifact = getArtifact();

      // Drag box on the far right of the label row
      Label dragDropLabel = new Label(toolbarParent, SWT.BORDER | SWT.CENTER);
      dragDropLabel.setText("  Drag \"" + artifact.getArtifactTypeName() + "\"  ");
      GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
      gd.heightHint = 20;
      dragDropLabel.setLayoutData(gd);
      new ArtifactDragAndDrop(dragDropLabel, artifact, ArtifactEditor.EDITOR_ID);
   }

   @Override
   protected void createTabBody(Composite parent, FormToolkit toolkit) {
      Artifact artifact = getArtifact();

      relationComposite = new RelationsComposite((AbstractArtifactEditor) getEditor(), parent, SWT.NONE, artifact);
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      relationComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      addListener();
      HelpUtil.setHelp(parent, OseeHelpContext.ARTIFACT_EDITOR__RELATIONS);
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
   protected void refreshTabBody() {
      if (Widgets.isAccessible(relationComposite)) {
         relationComposite.refresh();
      }
   }

   @Override
   public void refreshActionHandler() {
      getArtifact().reloadAttributesAndRelations();
      refreshTabBody();
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
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (Widgets.isAccessible(relationComposite) && artifactEvent.isHasEvent(getArtifact())) {
         relationComposite.refresh();
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (Widgets.isAccessible(relationComposite) && artifactTopicEvent.isHasEvent(getArtifact())) {
         relationComposite.refresh();
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }
}
