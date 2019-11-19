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

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetDecorator.Decorator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetAccessDecorationProvider implements XWidgetDecorator.DecorationProvider {

   private static final Image LOCK_IMAGE = ImageManager.getImage(FrameworkImage.LOCK_OVERLAY);

   private final AccessPolicy policyHandlerService;

   public XWidgetAccessDecorationProvider(AccessPolicy policyHandlerService) {
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
         AttributeTypeId attributeType = attributeWidget.getAttributeType();

         PermissionStatus permissionStatus = new PermissionStatus();
         Artifact artifact = attributeWidget.getArtifact();
         try {
            permissionStatus = policyHandlerService.hasAttributeTypePermission(Collections.singletonList(artifact),
               attributeType, PermissionEnum.WRITE, Level.FINE);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }

         // Get Info from AccessControlServiceImpl and take in to account if widget was editable before;
         boolean isWriteable = permissionStatus.matched();
         boolean isLocked = !isWriteable || artifact.isReadOnly();
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
