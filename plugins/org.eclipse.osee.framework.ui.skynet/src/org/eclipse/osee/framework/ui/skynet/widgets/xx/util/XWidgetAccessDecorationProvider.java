/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx.util;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetDecorator.Decorator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetAccessDecorationProvider implements XWidgetDecorator.DecorationProvider {

   private static final Image LOCK_IMAGE = ImageManager.getImage(CoreImage.LOCK_OVERLAY);
   private static final Image UNLOCK_IMAGE = ImageManager.getImage(CoreImage.LOCK_UNLOCKED);

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public void onUpdate(XWidget xWidget, Decorator decorator) {
      AttributeTypeToken attributeType = xWidget.getAttributeType();
      Artifact artifact = xWidget.getArtifact();
      if (!artifact.isValid() || !attributeType.isValid()) {
         return;
      }
      final XResultData rd = AccessControlArtifactUtil.getXResultAccessHeader("XWidget / Attribute / Access Report",
         Collections.singleton(artifact), attributeType);
      rd.logf("\nAccess Control Details: \n\n");
      try {
         ServiceUtil.accessControlService().hasAttributeTypePermission(Collections.singleton(artifact), attributeType,
            PermissionEnum.WRITE, rd);
      } catch (OseeCoreException ex) {
         rd.errorf("Error computing access permissions %s", Lib.exceptionToString(ex));
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      // Get Info from AccessControlServiceImpl and take in to account if widget was editable before;
      boolean isWriteable = rd.isSuccess();
      String reason = rd.toString();

      rd.logf("\n\nXWidget Data:\n\n%s\n", xWidget.toString());

      addReportAndMouseListener(xWidget, rd, isWriteable);

      decorator.setImage(isWriteable ? UNLOCK_IMAGE : LOCK_IMAGE);
      decorator.setDescription(reason);
      decorator.setVisible(!isWriteable);
   }

   private void addReportAndMouseListener(XWidget xWidget, final XResultData rd, boolean isWriteable) {
      // Only add label listener once
      if (xWidget.getWidgetReport().isEmpty()) {
         Control control = xWidget.getControl();
         if (Widgets.isAccessible(control)) {
            xWidget.setEditable(isWriteable);
         }
         MouseAdapter mListener = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  XResultDataUI.report(rd, "Widget Details");
               }
            }

         };
         Label label = xWidget.getLabelWidget();
         if (Widgets.isAccessible(label)) {
            label.setEnabled(isWriteable);
            label.addMouseListener(mListener);
         }
         Hyperlink hLabel = xWidget.getLabelLinkWidget();
         if (Widgets.isAccessible(hLabel)) {
            hLabel.setEnabled(isWriteable);
            hLabel.addMouseListener(mListener);
         }
      }
      xWidget.setWidgetReport(rd);
   }
};
