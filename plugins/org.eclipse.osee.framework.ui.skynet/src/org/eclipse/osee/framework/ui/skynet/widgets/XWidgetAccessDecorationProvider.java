/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetDecorator.Decorator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class XWidgetAccessDecorationProvider implements XWidgetDecorator.DecorationProvider {

   private static final Image LOCK_IMAGE = ImageManager.getImage(FrameworkImage.LOCK_OVERLAY);

   private final IAccessPolicyHandlerService policyHandlerService;

   public XWidgetAccessDecorationProvider(IAccessPolicyHandlerService policyHandlerService) {
      this.policyHandlerService = policyHandlerService;
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public void onUpdate(XWidget xWidget, Decorator decorator) {
      if (xWidget instanceof IAttributeWidget) {
         IAttributeWidget attributeWidget = (IAttributeWidget) xWidget;
         IAttributeType attributeType = attributeWidget.getAttributeType();

         PermissionStatus permissionStatus = new PermissionStatus();
         try {
            Artifact artifact = attributeWidget.getArtifact();
            permissionStatus =
               policyHandlerService.hasAttributeTypePermission(Collections.asCollection(artifact), attributeType,
                  PermissionEnum.WRITE, Level.FINE);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }

         // Get Info from AccessControlService;
         boolean isLocked = !permissionStatus.matched();
         String reason = permissionStatus.getReason();

         Control control = xWidget.getControl();
         if (Widgets.isAccessible(control)) {
            xWidget.setEditable(!isLocked);
         }
         Label label = xWidget.getLabelWidget();
         if (Widgets.isAccessible(label)) {
            label.setEnabled(!isLocked);
         }

         decorator.setImage(isLocked ? LOCK_IMAGE : null);
         decorator.setDescription(isLocked ? reason : null);
         decorator.setVisible(isLocked);
      }
   }
};