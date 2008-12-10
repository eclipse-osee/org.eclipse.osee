/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
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

         Map<String, IRenderer> commonRenders = new HashMap<String, IRenderer>();
         for (IRenderer renderer : RendererManager.getApplicableRenderer(PresentationType.EDIT, artifacts.get(0), null)) {
            commonRenders.put(renderer.getName(), renderer);
         }

         for (Artifact artifact : artifacts) {
            List<IRenderer> applicableRenders =
                  RendererManager.getApplicableRenderer(PresentationType.EDIT, artifact, null);

            for (IRenderer renderer : applicableRenders) {
               if (!commonRenders.containsKey(renderer.getName())) {
                  commonRenders.remove(renderer);
               }
            }
         }

         for (IRenderer renderer : commonRenders.values()) {
            MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
            menuItem.setText(renderer.getName());
            menuItem.addSelectionListener(new OpenWithSelectionListener(renderer, viewer));
         }

      } catch (Exception ex) {

      }
   }
}
