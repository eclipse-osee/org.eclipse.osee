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
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PreviewRendererData;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */

public class OpenWithMenuListener implements MenuListener {
   private Menu parentMenu;
   private Viewer viewer;
   private IRebuildMenuListener rebuildMenuListener;

   public OpenWithMenuListener(Menu parentMenu, final Viewer viewer, IRebuildMenuListener rebuildMenuListener) {
      super();
      this.parentMenu = parentMenu;
      this.viewer = viewer;
      this.rebuildMenuListener = rebuildMenuListener;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuHidden(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuHidden(MenuEvent e) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuShown(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuShown(MenuEvent e) {
      try {
         rebuildMenuListener.rebuildMenu();

         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
         Iterator<?> iterator = selection.iterator();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selection.size());

         boolean validForPreview = true;
         //load artifacts in the list
         Artifact artifact = null;
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof Artifact) {
               artifact = (Artifact) object;
            } else if (object instanceof Match) {
               artifact = (Artifact) ((Match) object).getElement();
            }
            validForPreview &=
                  (artifact instanceof WordArtifact && !((WordArtifact) artifact).isWholeWordArtifact() || !(artifact instanceof NativeArtifact));
            artifacts.add(artifact);
         }

         List<IRenderer> commonRenders = RendererManager.getCommonSpecializedEditRenders(artifacts);

         if (validForPreview) {
            for (IRenderer previewRenderer : RendererManager.getPreviewPresentableRenders(artifact)) {
               for (PreviewRendererData data : previewRenderer.getPreviewData()) {
                  MenuItem item = new MenuItem(parentMenu, SWT.PUSH);
                  item.setText(data.getName());
                  item.setImage(previewRenderer.getImage(null));
                  item.addSelectionListener(new OpenWithSelectionListener(previewRenderer, viewer, true,
                        data.getOption()));
               }
            }
         }

         for (IRenderer renderer : commonRenders) {
            Image image = renderer.getImage(artifacts.iterator().next());
            MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
            menuItem.setText(renderer.getName());
            menuItem.setImage(image);
            menuItem.addSelectionListener(new OpenWithSelectionListener(renderer, viewer, false));
         }

      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}
