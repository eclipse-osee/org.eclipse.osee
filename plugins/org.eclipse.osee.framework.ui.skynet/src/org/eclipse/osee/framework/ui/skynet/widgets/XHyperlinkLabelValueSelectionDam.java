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

import java.util.Collections;
import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * XWidget where label is hyperlink and value is label.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelValueSelectionDam extends XHyperlinkLabelValueSelection implements AttributeWidget {

   public static final String WIDGET_ID = XHyperlinkLabelValueSelectionDam.class.getSimpleName();

   protected Artifact artifact;
   protected AttributeTypeToken attributeType;
   public static String NOT_SET = "Not Set";

   public XHyperlinkLabelValueSelectionDam() {
      super("");
   }

   public XHyperlinkLabelValueSelectionDam(String label) {
      super(label);
   }

   @Override
   public boolean handleSelection() {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, Collections.singleton(artifact), true);
   }

   @Override
   public String getCurrentValue() {
      // Dates are almost always just date and not timestamp; default to that for value
      if (attributeType.isDate()) {
         Date date = artifact.getSoleAttributeValue(attributeType, null);
         return DateUtil.getMMDDYY(date);
      }
      String value = artifact.getAttributesToString(attributeType);
      if (Strings.isInValid(value)) {
         value = NOT_SET;
      }
      return value;
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
      if (Strings.isInValid(getLabel())) {
         setLabel(attributeType.getUnqualifiedName());
      }
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
               String currValue = getCurrentValue();
               if (NOT_SET.equals(currValue)) {
                  currValue = "";
               }
               if (isRequiredEntry() && Strings.isInValid(currValue)) {
                  status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                     String.format("Must select [%s]", getAttributeType().getUnqualifiedName()));
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
         if (status.isOK() && attributeType.isDate() && isValidateDate()) {
            try {
               Date date = artifact.getSoleAttributeValue(attributeType, null);
               if (date != null) {
                  Date today = new Date();
                  if (date.before(today)) {
                     status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Date is past today");
                  }
               }
            } catch (OseeCoreException ex) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
            }

         }
      }
      return status;
   }

}
