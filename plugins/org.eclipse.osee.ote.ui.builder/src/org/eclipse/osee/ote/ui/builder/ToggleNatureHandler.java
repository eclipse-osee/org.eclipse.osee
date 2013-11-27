package org.eclipse.osee.ote.ui.builder;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ToggleNatureHandler extends AbstractHandler {

   @Override
   public void addHandlerListener(IHandlerListener handlerListener) {
      // TODO Auto-generated method stub

   }

   @Override
   public void dispose() {
      // TODO Auto-generated method stub

   }

   @SuppressWarnings("rawtypes")
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      IStructuredSelection selection = getCurrentSelection();
      if (selection != null && selection instanceof IStructuredSelection) {
         for (Iterator it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
            Object element = it.next();
            IProject project = null;
            if (element instanceof IProject) {
               project = (IProject) element;
            } else if (element instanceof IAdaptable) {
               project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
            }
            if (project != null) {
               toggleNature(project);
            }
         }
      }
      return null;
   }
   
   private void toggleNature(IProject project) {
      try {
         IProjectDescription description = project.getDescription();
         String[] natures = description.getNatureIds();

         for (int i = 0; i < natures.length; ++i) {
            if (OTEPackagingNature.NATURE_ID.equals(natures[i])) {
               // Remove the nature
               String[] newNatures = new String[natures.length - 1];
               System.arraycopy(natures, 0, newNatures, 0, i);
               System.arraycopy(natures, i + 1, newNatures, i,
                     natures.length - i - 1);
               description.setNatureIds(newNatures);
               project.setDescription(description, null);
               return;
            }
         }

         // Add the nature
         String[] newNatures = new String[natures.length + 1];
         System.arraycopy(natures, 0, newNatures, 0, natures.length);
         newNatures[natures.length] = OTEPackagingNature.NATURE_ID;
         description.setNatureIds(newNatures);
         project.setDescription(description, null);
      } catch (CoreException e) {
      }
   }
   
   private IStructuredSelection getCurrentSelection() {
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isStarting() && !workbench.isClosing()) {
         IWorkbenchPage page = getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  ISelectionProvider selectionProvider = site.getSelectionProvider();
                  if(selectionProvider != null){
                     ISelection selection = selectionProvider.getSelection();
                     if (selection instanceof IStructuredSelection) {
                        return (IStructuredSelection) selection;
                     }
                  }
               }
            }
         }
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public boolean isHandled() {
      return true;
   }

   @Override
   public void removeHandlerListener(IHandlerListener handlerListener) {
      // TODO Auto-generated method stub

   }
   
   private IWorkbenchPage getActivePage() {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      return workbenchWindow != null ? workbenchWindow.getActivePage() : null;
   }

}
