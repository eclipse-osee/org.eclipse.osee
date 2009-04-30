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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetValidator {

   //            Artifact artifact = null; //aWidget.getArtifact();
   //            AttributeType attributeType = null;//AttributeTypeManager.getType(aWidget.getAttributeName());
   //            String data = null;
   //            validate(0, xWidget, artifact, attributeType, data);

   private void validate(int requiredQualityOfService, XWidget xWidget, Artifact artifact, AttributeType attributeType, Object proposedValue) {
      IStatus status =
            OseeValidator.getInstance().validate(requiredQualityOfService, artifact, attributeType, proposedValue);
      if (!status.isOK()) {
         IStatus[] itemsToReport;
         if (status.isMultiStatus()) {
            itemsToReport = status.getChildren();
         } else {
            itemsToReport = new IStatus[] {status};
         }
         for (IStatus item : itemsToReport) {
            xWidget.setControlCausedMessage(status.getPlugin(), status.getMessage(),
                  toMessageProviderLevel(item.getSeverity()));
         }
      }
   }

   private boolean isInRange(int value, int min, int max) {
      return min <= value && value < max;
   }

   private int toMessageProviderLevel(int level) {
      int toReturn = IMessageProvider.NONE;
      if (isInRange(level, Level.INFO.intValue(), Level.WARNING.intValue())) {
         toReturn = IMessageProvider.INFORMATION;
      } else if (isInRange(level, Level.WARNING.intValue(), Level.SEVERE.intValue())) {
         toReturn = IMessageProvider.WARNING;
      } else if (level > Level.SEVERE.intValue()) {
         toReturn = IMessageProvider.ERROR;
      }
      return toReturn;
   }
}
