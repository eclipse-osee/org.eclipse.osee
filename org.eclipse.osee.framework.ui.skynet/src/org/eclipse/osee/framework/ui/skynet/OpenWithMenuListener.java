/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */

public class OpenWithMenuListener implements MenuListener {
   private Menu parentMenu;
   private Viewer viewer;
   private ArtifactExplorer artifactExplorer;

   public OpenWithMenuListener(Menu parentMenu, final Viewer viewer, ArtifactExplorer artifactExplorer) {
      super();
      this.parentMenu = parentMenu;
      this.viewer = viewer;
      this.artifactExplorer = artifactExplorer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuHidden(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuHidden(MenuEvent e) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuShown(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuShown(MenuEvent e) {
      try {
         artifactExplorer.setupPopupMenu();

         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         Iterator<?> iterator = selection.iterator();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selection.size());

         //load artifacts in the list
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof Artifact) {
               artifacts.add((Artifact) object);
            }
         }

         List<IRenderer> commonRenders =
               RendererManager.getApplicableRenderer(PresentationType.SPECIALIZED_EDIT, artifacts.get(0), null);

         for (Artifact artifact : artifacts) {
            List<IRenderer> applicableRenders =
                  RendererManager.getApplicableRenderer(PresentationType.SPECIALIZED_EDIT, artifact, null);

            Iterator<?> commIterator = commonRenders.iterator();

            while (commIterator.hasNext()) {
               IRenderer commRenderer = (IRenderer) commIterator.next();
               boolean found = false;
               for (IRenderer appRenderer : applicableRenders) {
                  if (appRenderer.getName().equals(commRenderer.getName())) {
                     found = true;
                     continue;
                  }
               }

               if (!found) {
                  commIterator.remove();
               }
            }

         }

         for (IRenderer renderer : commonRenders) {
            MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
            menuItem.setText(renderer.getName());
            menuItem.setImage(renderer.getImage());
            menuItem.addSelectionListener(new OpenWithSelectionListener(renderer, viewer));
         }

      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}
