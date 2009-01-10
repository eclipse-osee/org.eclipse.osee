/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.List;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.ui.PlatformUI;

/**
 * This abstract class provides the basic functionality for opening renderer editors.
 * 
 * @author Jeff C. Phillips
 */
public abstract class AbstractEditorHandler extends CommandHandler {
   private static final AccessControlManager accessControlManager = AccessControlManager.getInstance();
   protected List<Artifact> artifacts;
   ISelectionProvider selectionProvider;

   protected PermissionEnum getPermissionLevel() {
      return PermissionEnum.READ;
   }

   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      boolean isEnabled = false;

      selectionProvider = AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         isEnabled = accessControlManager.checkObjectListPermission(artifacts, getPermissionLevel());
      }
      return isEnabled;
   }

}
