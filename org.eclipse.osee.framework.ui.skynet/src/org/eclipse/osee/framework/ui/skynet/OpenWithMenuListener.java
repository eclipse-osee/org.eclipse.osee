/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PreviewRendererData;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */

public class OpenWithMenuListener implements MenuListener {
   private Menu parentMenu;
   private Viewer viewer;
   private IRebuildMenuListener rebuildMenuListener;
   private ICommandService commandService;

   public OpenWithMenuListener(Menu parentMenu, final Viewer viewer, IRebuildMenuListener rebuildMenuListener) {
      super();
      this.parentMenu = parentMenu;
      this.viewer = viewer;
      this.rebuildMenuListener = rebuildMenuListener;
      this.commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
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
            validForPreview &= !artifact.isOfType(WordArtifact.WHOLE_WORD) && !artifact.isOfType("Native");
            artifacts.add(artifact);
         }

         List<IRenderer> commonRenders = RendererManager.getCommonSpecializedEditRenders(artifacts);

         if (validForPreview) {
            List<IRenderer> previewRenderers = RendererManager.getPreviewPresentableRenders(artifact);
            for (IRenderer previewRenderer : previewRenderers) {
               for (PreviewRendererData data : previewRenderer.getPreviewData()) {
                  MenuItem item = new MenuItem(parentMenu, SWT.PUSH);
                  item.setText(data.getName());
                  //If getting the image exceptions out we do not want to stop this process
                  try {
                     item.setImage(previewRenderer.getImage(null));
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  }
                  item.addSelectionListener(new OpenWithSelectionListener(previewRenderer, viewer, true,
                        data.getOption()));
               }
            }

            if (!previewRenderers.isEmpty()) {
               new MenuItem(parentMenu, SWT.SEPARATOR);
            }
         }

         for (IRenderer renderer : commonRenders) {
            Image image = null;
            //If getting the image exceptions out we do not want to stop this process
            try {
               image = renderer.getImage(artifacts.iterator().next());
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }

            Command command = commandService.getCommand(renderer.getCommandId());
            if (command != null && command.isEnabled()) {
               MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
               menuItem.setText(renderer.getName());
               menuItem.setImage(image);
               menuItem.addSelectionListener(new OpenWithSelectionListener(renderer, viewer, false));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}
