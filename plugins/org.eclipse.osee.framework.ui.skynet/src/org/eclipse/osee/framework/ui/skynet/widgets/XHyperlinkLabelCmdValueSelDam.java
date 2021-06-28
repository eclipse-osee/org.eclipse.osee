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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * XWidget where label is hyperlink and value is label.
 *
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelCmdValueSelDam extends XHyperlinkLabelCmdValueSelection implements AttributeWidget {

   private Artifact artifact;
   private AttributeTypeToken attributeType;

   public XHyperlinkLabelCmdValueSelDam(String label) {
      this(label, false);
   }

   public XHyperlinkLabelCmdValueSelDam(String label, boolean supportClear, Integer truncateValueLength) {
      super(label);
      this.supportClear = supportClear;
      this.truncateValueLength = truncateValueLength;
   }

   public XHyperlinkLabelCmdValueSelDam(String label, boolean supportClear) {
      this(label, supportClear, null);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * Hyperlink Selection is save on selection
    */
   @Override
   public void saveToArtifact() {
      // do nothing
   }

   /**
    * Hyperlink Selection is save on selection
    */
   @Override
   public void revert() {
      // do nothing
   }

   /**
    * Hyperlink Selection is save on selection
    */
   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(),
                  getCurrentValue());
               if (status.isOK() && isRequiredEntry() && Strings.isInValid(getCurrentValue())) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     String.format("Must enter [%s]", attributeType.getUnqualifiedName()));
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

}
