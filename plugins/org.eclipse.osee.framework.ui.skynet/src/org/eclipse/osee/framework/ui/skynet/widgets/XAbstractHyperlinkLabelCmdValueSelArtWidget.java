/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * XWidget where label is hyperlink and value is label.
 *
 * @author Donald G. Dunne
 */
public abstract class XAbstractHyperlinkLabelCmdValueSelArtWidget extends XAbstractHyperlinkLabelCmdValueSelWidget {

   public XAbstractHyperlinkLabelCmdValueSelArtWidget(WidgetId widgetId, String label, boolean supportClear, Integer truncateValueLength) {
      super(widgetId, label);
      this.supportClear = supportClear;
      this.truncateValueLength = truncateValueLength;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               XResultData rd = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(),
                  getAttributeType(), getCurrentValue());
               if (rd.isErrors()) {
                  status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
               }
               if (rd.isOK() && isRequiredEntry() && Strings.isInValid(getCurrentValue())) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     String.format("Must enter [%s]", getAttributeType().getUnqualifiedName()));
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

}
