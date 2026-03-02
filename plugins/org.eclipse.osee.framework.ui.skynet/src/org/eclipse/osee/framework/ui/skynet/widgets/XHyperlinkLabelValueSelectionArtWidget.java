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
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.data.conditions.RequiredIfInRelationCondition;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * XWidget where label is hyperlink and value is label.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkLabelValueSelectionArtWidget extends XAbstractHyperlinkLabelValueSelWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkLabelValueSelectionArtWidget;

   public XHyperlinkLabelValueSelectionArtWidget() {
      this("");
   }

   public XHyperlinkLabelValueSelectionArtWidget(String label) {
      super(ID, label);
   }

   @Override
   public boolean handleSelection() {
      return ArtifactPromptChange.promptChangeAttribute(getAttributeType(), Collections.singleton(getArtifact()), true,
         getClass().getSimpleName());
   }

   @Override
   public String getCurrentValue() {
      // Dates are almost always just date and not timestamp; default to that for value
      if (getAttributeType().isDate()) {
         Date date = getArtifact().getSoleAttributeValue(getAttributeType(), null);
         if (date != null) {
            return DateUtil.getMMDDYY(date);
         }
         return Widgets.NOT_SET;
      }
      String value = getArtifact().getAttributesToString(getAttributeType());
      if (Strings.isInValid(value)) {
         value = Widgets.NOT_SET;
      }
      return value;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               String currValue = getCurrentValue();
               if (Widgets.NOT_SET.equals(currValue)) {
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
         if (status.isOK() && getAttributeType().isDate() && isValidateDate()) {
            try {
               Date date = getArtifact().getSoleAttributeValue(getAttributeType(), null);
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

   @Override
   public boolean isRequiredEntry() {
      for (ConditionalRule rule : getConditions()) {
         if (rule instanceof RequiredIfInRelationCondition) {
            RequiredIfInRelationCondition cond = (RequiredIfInRelationCondition) rule;
            if (getArtifact().isInRelation(cond.getRelationSide())) {
               return true;
            }
         }
      }
      return super.isRequiredEntry();
   }

}
