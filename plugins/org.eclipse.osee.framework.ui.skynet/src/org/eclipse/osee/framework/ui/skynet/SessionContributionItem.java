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

package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.OseeStatusContributionItem;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.dialogs.AuthenticationDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Roberto E. Escobar
 */
public final class SessionContributionItem extends OseeStatusContributionItem {

   private static final String CONTRIBUTION_ITEM_ID = "session.contribution.item";

   private static final Image DISABLED_IMAGE = new OverlayImage(ImageManager.getImage(FrameworkImage.USER),
      ImageManager.getImageDescriptor(FrameworkImage.SLASH_RED_OVERLAY)).createImage();

   private static final String ENABLED_TOOLTIP = "Authenticated as: %s (%s) - session(%s)\nDouble-Click to Log Off.";
   private static final String DISABLED_TOOLTIP = "Not Authenticated.\nDouble-Click to Log On.";

   public SessionContributionItem() {
      super(CONTRIBUTION_ITEM_ID);
      init();
      updateStatus(true);
   }

   private void init() {
      setActionHandler(new Action() {

         @Override
         public void run() {
            try {
               if (ClientSessionManager.isSessionValid()) {
                  boolean result = MessageDialog.openQuestion(AWorkbench.getActiveShell(), "Log Off...",
                     "Are you sure you want to log off and exit OSEE?");
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
                     Displays.ensureInDisplayThread(new Runnable() {
                        @Override
                        public void run() {
                           try {
                              AccessTopicEventPayload payload = new AccessTopicEventPayload();
                              OseeEventManager.kickAccessTopicEvent(SessionContributionItem.class, payload,
                                 AccessTopicEvent.USER_AUTHENTICATED);
                           } catch (Exception ex) {
                              OseeLog.log(Activator.class, Level.SEVERE, ex);
                           }
                        }
                     });
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

   }

   @Override
   protected Image getDisabledImage() {
      return DISABLED_IMAGE;
   }

   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   @Override
   protected Image getEnabledImage() {
      return ImageManager.getImage(FrameworkImage.USER);
   }

   @Override
   protected String getEnabledToolTip() {
      if (ClientSessionManager.isSessionValid() && !OseeProperties.isInTest()) {
         String skynetName = "Unknown";
         String userId = "-";
         String sessionId = "-";
         try {
            // Get user without exceptioning if user not in database
            User user = UserManager.getUserOrNull();
            if (user != null) {
               skynetName = UserManager.getUser().getName();
               userId = UserManager.getUser().getUserId();
               sessionId = ClientSessionManager.getSessionId();
            }
         } catch (OseeCoreException ex) {
            skynetName = "Exception: " + ex.getLocalizedMessage();
         }
         return String.format(ENABLED_TOOLTIP, skynetName, userId, sessionId);
      }
      return DISABLED_TOOLTIP;
   }

   public static void addToAllViews() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
                  return;
               }
               for (IViewReference viewDesc : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
                  IViewPart viewPart = viewDesc.getView(false);
                  if (viewPart != null) {
                     addToViewpart((ViewPart) viewPart);
                  }
               }
            } catch (Exception ex) {
               // DO NOTHING
            }
         }
      });
   }

   public static void addToViewpart(ViewPart viewPart) {
      // Attempt to add to PackageExplorerPart
      try {
         if (viewPart != null) {
            for (IContributionItem item : viewPart.getViewSite().getActionBars().getStatusLineManager().getItems()) {
               if (item instanceof SessionContributionItem) {
                  return;
               }
            }
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(new SessionContributionItem());
         }
      } catch (Exception ex) {
         // do nothing
      }
   }
}
