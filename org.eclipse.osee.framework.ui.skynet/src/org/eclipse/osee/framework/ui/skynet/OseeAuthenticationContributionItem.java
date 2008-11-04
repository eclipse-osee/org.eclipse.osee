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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.security.AuthenticationDialog;
import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class OseeAuthenticationContributionItem extends OseeContributionItem implements IAccessControlEventListener {

   private static final String ID = "skynet.authentication";

   private static final Image ENABLED_IMAGE = SkynetGuiPlugin.getInstance().getImage("user.gif");
   private static final Image DISABLED_IMAGE =
         new OverlayImage(ENABLED_IMAGE, SkynetGuiPlugin.getInstance().getImageDescriptor("red_slash.gif")).createImage();

   private static String ENABLED_TOOLTIP = "Authenticated as: %s (%s)\nDouble-Click to Log Off.";
   private static String DISABLED_TOOLTIP = "Not Authenticated.\nDouble-Click to Log On.";

   final OseeAuthenticationContributionItem contributionItem;

   public OseeAuthenticationContributionItem() {
      super(ID);
      init();
      contributionItem = this;
   }

   private void init() {
      setActionHandler(new Action() {

         @Override
         public void run() {
            OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();
            if (oseeAuthentication.isAuthenticated()) {
               boolean result =
                     MessageDialog.openQuestion(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Log Off...",
                           "Are you sure you want to log off and exit OSEE?");
               if (result) {
                  oseeAuthentication.logOff();
                  PlatformUI.getWorkbench().close();
               }
            } else {
               if (oseeAuthentication.isLoginAllowed()) {
                  AuthenticationDialog.openDialog();
               } else {
                  oseeAuthentication.authenticate("", "", "", false);
               }
            }
            SkynetAuthentication.notifyListeners();
         }
      });
      OseeEventManager.addListener(this);
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof OseeAuthenticationContributionItem) return;
      manager.add(new OseeAuthenticationContributionItem());
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
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

               updateStatus(OseeAuthentication.getInstance().isAuthenticated());
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
      if (OseeAuthentication.getInstance().isAuthenticated()) {
         User skynetName = SkynetAuthentication.getUser();
         return String.format(ENABLED_TOOLTIP, (skynetName != null ? skynetName.getName() : skynetName),
               OseeAuthentication.getInstance().getCredentials().getField(UserCredentialEnum.Id));
      }
      return DISABLED_TOOLTIP;
   }
}
