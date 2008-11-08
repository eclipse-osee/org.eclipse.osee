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
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.skynet.dialogs.AuthenticationDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class SessionContributionItem extends OseeContributionItem implements IAccessControlEventListener {

   private static final String ID = "session.contribution.item";

   private static final Image ENABLED_IMAGE = SkynetGuiPlugin.getInstance().getImage("user.gif");
   private static final Image DISABLED_IMAGE =
         new OverlayImage(ENABLED_IMAGE, SkynetGuiPlugin.getInstance().getImageDescriptor("red_slash.gif")).createImage();

   private static String ENABLED_TOOLTIP = "Authenticated as: %s (%s) - session(%s)\nDouble-Click to Log Off.";
   private static String DISABLED_TOOLTIP = "Not Authenticated.\nDouble-Click to Log On.";

   private SessionContributionItem() {
      super(ID);
      init();
      updateStatus(true);
      OseeEventManager.addListener(this);
   }

   private void init() {
      setActionHandler(new Action() {

         @Override
         public void run() {
            try {
               if (ClientSessionManager.isSessionValid()) {
                  boolean result =
                        MessageDialog.openQuestion(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                              "Log Off...", "Are you sure you want to log off and exit OSEE?");
                  if (result) {
                     ClientSessionManager.releaseSession();

                     PlatformUI.getWorkbench().close();
                  }
               } else {
                  //               if (oseeAuthentication.isLoginAllowed()) {
                  AuthenticationDialog.openDialog();
                  //               } else {
                  //                  oseeAuthentication.authenticate("", "", "", false);
                  //               }
                  if (ClientSessionManager.isSessionValid()) {
                     Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                           try {
                              OseeEventManager.kickAccessControlArtifactsEvent(this,
                                    AccessControlEventType.UserAuthenticated, LoadedArtifacts.EmptyLoadedArtifacts());
                           } catch (Exception ex) {
                              OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                           }
                        }
                     });
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });

   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof SessionContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new SessionContributionItem());
      }
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IAccessControlEventListener#handleAccessControlArtifactsEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.eventx.AccessControlModType, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifactss) {
      if (accessControlEventType == AccessControlEventType.UserAuthenticated) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               updateStatus(ClientSessionManager.isSessionValid());
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledImage()
    */
   @Override
   protected Image getDisabledImage() {
      return DISABLED_IMAGE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledToolTip()
    */
   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledImage()
    */
   @Override
   protected Image getEnabledImage() {
      return ENABLED_IMAGE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledToolTip()
    */
   @Override
   protected String getEnabledToolTip() {
      if (ClientSessionManager.isSessionValid()) {
         String skynetName = "Unknown";
         String userId = "-";
         String sessionId = "-";
         try {
            skynetName = UserManager.getUser().getName();
            userId = UserManager.getUser().getUserId();
            sessionId = ClientSessionManager.getSessionId();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         return String.format(ENABLED_TOOLTIP, skynetName, userId, sessionId);
      }
      return DISABLED_TOOLTIP;
   }
}
