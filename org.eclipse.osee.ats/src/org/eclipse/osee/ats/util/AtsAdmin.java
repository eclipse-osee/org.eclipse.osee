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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class AtsAdmin {

   private static Artifact atsAdminArtifact;
   private static boolean searched = false;

   public static boolean isAtsAdmin() {
      boolean atsAdmin = false;
      try {
         if (System.getProperty("AtsAdmin") != null) {
            atsAdmin = true;
         }
         if (!atsAdmin) {
            if (!searched) {
               atsAdminArtifact =
                     ArtifactQuery.getArtifactFromTypeAndName("Artifact", "AtsAdmin", AtsPlugin.getAtsBranch());
               searched = true;
            }
            if (atsAdminArtifact != null) {
               atsAdmin =
                     AccessControlManager.checkObjectPermission(SkynetAuthentication.getUser(), atsAdminArtifact,
                           PermissionEnum.FULLACCESS);
            }
         }
         OseeProperties.getInstance().setDeveloper(atsAdmin);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return atsAdmin;
   }
}
