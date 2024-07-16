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

package org.eclipse.osee.framework.ui.skynet.skywalker.arttype;

import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.skywalker.arttype.action.ArtTypeWalkerLayoutAction;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeWalker extends GenericViewPart {
   private static final Color RED = Displays.getColor(234, 105, 135);
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.skywalker.ArtifactTypeWalkerView";
   protected GraphViewer viewer;
   private Composite viewerComp;
   private boolean singleLevel = false;
   private ArtifactTypeToken artifactType;
   private ArtifactTypeContentProvider contentProvider;

   @Override
   public void createPartControl(Composite parent) {

      viewerComp = new Composite(parent, SWT.NONE);
      viewerComp.setLayout(new FillLayout());

      viewer = new GraphViewer(viewerComp, ZestStyles.NONE);
      contentProvider = new ArtifactTypeContentProvider();
      viewer.setContentProvider(contentProvider);
      viewer.setLabelProvider(new ArtifactTypeWalkerLabelProvider(contentProvider));
      viewer.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID);
      viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
      viewer.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
      viewer.addDoubleClickListener(new IDoubleClickListener() {

         @Override
         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            while (itemsIter.hasNext()) {
               Object obj = itemsIter.next();
               if (obj instanceof ArtifactTypeToken) {
                  try {
                     explore((ArtifactTypeToken) obj);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
         }

      });
      createActions();
      refresh();

      setFocusWidget(viewer.getControl());
   }

   public void setLayout(LayoutAlgorithm algorithm) {
      viewer.setLayoutAlgorithm(algorithm);
   }

   private void explore(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
      ArtifactTypeContentProvider contentProvider = (ArtifactTypeContentProvider) viewer.getContentProvider();
      contentProvider.getParentTypes().clear();
      contentProvider.setSelectedArtType(artifactType);
      viewer.setInput(artifactType);
      GraphItem item = viewer.findGraphItem(artifactType);
      if (item != null && item instanceof GraphNode) {
         GraphNode node = (GraphNode) item;
         node.setBackgroundColor(Displays.getSystemColor(SWT.COLOR_CYAN));
         viewer.update(node, null);
      }
      try {
         for (ArtifactTypeToken childType : artifactType.getDirectDescendantTypes()) {
            GraphItem childItem = viewer.findGraphItem(childType);
            if (childItem != null && childItem instanceof GraphNode) {
               GraphNode node = (GraphNode) childItem;
               node.setBackgroundColor(Displays.getSystemColor(SWT.COLOR_GREEN));
               viewer.update(node, null);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      try {
         for (ArtifactTypeToken childType : contentProvider.getParentTypes()) {
            GraphItem childItem = viewer.findGraphItem(childType);
            if (childItem != null && childItem instanceof GraphNode) {
               GraphNode node = (GraphNode) childItem;
               node.setBackgroundColor(RED);
               viewer.update(node, null);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      setPartName("Artifact Type Walker (" + artifactType.getName() + ")");
   }

   protected void createActions() {

      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      Action openTypeAction = new Action() {
         @Override
         public void run() {
            FilteredTreeArtifactTypeDialog dialog =
               new FilteredTreeArtifactTypeDialog("Select Artifact Type", "Select Artifact Type to Explore",
                  ServiceUtil.getTokenService().getArtifactTypes(), new ArtifactTypeLabelProvider());
            dialog.setMultiSelect(false);
            try {
               if (dialog.open() == Window.OK) {
                  ArtifactTypeToken type = dialog.getSelectedFirst();
                  explore(type);
               }
            } catch (Exception ex) {
               AWorkbench.popup("Unable to create artifact type selectiong dialog.  See error log");
               throw ex;
            }
         }
      };
      openTypeAction.setText("Select Artifact Type");
      openTypeAction.setToolTipText("Select Artifact Type");
      openTypeAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.LASER));
      tbm.add(openTypeAction);

      Action singleLevelAction = new Action("Single Level", SWT.TOGGLE) {
         @Override
         public void run() {
            singleLevel = !singleLevel;
            contentProvider.setSingleLevel(singleLevel);
            explore(artifactType);
         }
      };
      singleLevelAction.setText("Show One Level");
      singleLevelAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.LINK));
      tbm.add(singleLevelAction);

      tbm.add(new ArtTypeWalkerLayoutAction(this));

      Action refreshAction = new Action() {
         @Override
         public void run() {
            refresh();
         }
      };
      refreshAction.setText("Reset");
      refreshAction.setToolTipText("Reset");
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.UNDO));
      tbm.add(refreshAction);
   }

   public void refresh() {
      try {
         explore(CoreArtifactTypes.Artifact);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refreshCurrent() {
      explore(artifactType);
   }
}
