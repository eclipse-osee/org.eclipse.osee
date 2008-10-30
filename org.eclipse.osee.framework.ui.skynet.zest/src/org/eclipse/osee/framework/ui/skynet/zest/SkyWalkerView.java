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
package org.eclipse.osee.framework.ui.skynet.zest;

import java.util.Iterator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;

/**
 * @author Robert A. Fisher
 */
public class SkyWalkerView extends ViewPart implements IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.skyWalkerView";
   private GraphViewer viewer;

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      viewer = new GraphViewer(parent, ZestStyles.NONE);

      viewer.setContentProvider(new ArtifactGraphContentProvider(1));
      viewer.setLabelProvider(new ArtifactGraphLabelProvider());
      viewer.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID);
      viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
      RadialLayoutAlgorithm layout = new RadialLayoutAlgorithm();
      layout.setRangeToLayout((-90 * Math.PI) / 360, (90 * Math.PI) / 360);
      viewer.setLayoutAlgorithm(layout);
      //      viewer.addDoubleClickListener(new ArtifactExplorer.DoubleClickListener());
      viewer.addDoubleClickListener(new IDoubleClickListener() {

         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            while (itemsIter.hasNext()) {
               Object obj = itemsIter.next();
               if (!(obj instanceof Artifact)) continue;
               Artifact artifact = (Artifact) obj;
               //               ArtifactZestView.exploreArtifact(artifact);
               viewer.setInput(artifact);
               viewer.setSelection(selection, true);
            }
         }

      });
      try {
         viewer.setInput(ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(BranchManager.getDefaultBranch()));
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      viewer.getControl().setFocus();
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      return "";
   }

   public void explore(Artifact artifact) {
      viewer.setInput(artifact);
   }

   public static void exploreArtifact(Artifact artifact) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      SkyWalkerView view;
      try {
         view =
               (SkyWalkerView) page.showView(SkyWalkerView.VIEW_ID, new GUID().toString(), IWorkbenchPage.VIEW_ACTIVATE);
         view.explore(artifact);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }
}
