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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Jeff C. Phillips
 */
public abstract class AbstractArtifactSelectionHandler extends AbstractHandler {
   private static final AccessControlManager accessManager = AccessControlManager.getInstance();
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private List<Artifact> artifacts = null;
   private final ISelectionChangedListener myISelectionChangedListener = null;
   private List<Artifact> mySelectedArtifactList = null;
   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);

   public AbstractArtifactSelectionHandler() {
      IWorkbenchPart myIWorkbenchPart = AWorkbench.getActivePage().getActivePart();
      IWorkbenchPartSite myIWorkbenchPartSite = myIWorkbenchPart.getSite();
      ISelectionProvider myISelectionProvider = myIWorkbenchPartSite.getSelectionProvider();
      myISelectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            mySelectedArtifactList =
                  Handlers.getArtifactsFromStructuredSelection((IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection());
            fireHandlerChanged(enabledChangedEvent);

         }
      });
      artifacts =
            Handlers.getArtifactsFromStructuredSelection((IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection());
   }

   /**
    * @return Returns a list of artifacts acquired from an IStructuredSelection from the active page.
    */
   protected List<Artifact> getArtifacts() {
      return artifacts;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
    */
   @Override
   public boolean isEnabled() {
      System.out.println("AbstractArtifactSelectionHandler isEnabled " + this.toString());
      return permissionLevel() != null ? hasPermission(artifacts) : super.isEnabled();
   }

   private boolean hasPermission(List<Artifact> artifacts) {
      boolean permitted = true;

      for (Artifact artifact : artifacts) {
         permitted &=
               accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), artifact, permissionLevel());
      }
      return permitted;
   }

   /**
    * This method should be overridden by a subclass that requires access control.
    * 
    * @return Returns the permission level to be used for access control
    */
   protected PermissionEnum permissionLevel() {
      return null;
   }
   // abstract
}
