/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.ui.skynet.access;

import java.util.Collections;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AccessControlDetails extends Action {

   private final Artifact artifact;
   public static final String NAME = "Access Control - Details";

   public AccessControlDetails(Artifact artifact) {
      super(NAME);
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.LOCK_DETAILS));
   }

   @Override
   public void run() {
      XResultData rd = AccessControlArtifactUtil.getXResultAccessHeader(getText(), artifact);

      // Show Name first
      logAttrType(rd, CoreAttributeTypes.Name);
      for (AttributeTypeToken attrType : artifact.getAttributeTypes()) {
         if (attrType.notEqual(CoreAttributeTypes.Name)) {
            logAttrType(rd, attrType);
         }
      }

      XResultDataUI.report(rd, getText());
   }

   private void logAttrType(XResultData rd, AttributeTypeToken attrType) {
      rd.logf("\n\n==========================================================\n " //
         + "== Attribute Type %s Access: \n" //
         + "==========================================================\n", attrType.toStringWithId());

      // Need new XResultData for each or Errors will collide with each other
      rd.logf("Read Access: \n\n");
      rd.addRaw(OseeApiService.get().getAccessControlService().hasAttributeTypePermission(
         Collections.singleton(artifact), attrType, PermissionEnum.READ, new XResultData()).toString());

      rd.logf("\n\nWrite Access: \n\n");
      rd.addRaw(OseeApiService.get().getAccessControlService().hasAttributeTypePermission(
         Collections.singleton(artifact), attrType, PermissionEnum.WRITE, new XResultData()).toString());

      rd.logf("\n\nFull Access: \n\n");
      rd.addRaw(OseeApiService.get().getAccessControlService().hasAttributeTypePermission(
         Collections.singleton(artifact), attrType, PermissionEnum.FULLACCESS, new XResultData()).toString());
   }

}
