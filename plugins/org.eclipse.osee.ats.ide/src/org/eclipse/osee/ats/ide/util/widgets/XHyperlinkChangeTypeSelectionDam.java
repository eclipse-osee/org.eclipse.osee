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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * XWidget where label is hyperlink and value is label.
 *
 * @author Donald G. Dunne
 */
public class XHyperlinkChangeTypeSelectionDam extends XHyperlinkChangeTypeSelection implements AttributeWidget {

   public static final String WIDGET_ID = XHyperlinkChangeTypeSelectionDam.class.getSimpleName();

   protected Artifact artifact;
   protected AttributeTypeToken attributeType = AtsAttributeTypes.ChangeType;

   public static String NOT_SET = Widgets.NOT_SET;

   public XHyperlinkChangeTypeSelectionDam() {
      this("");
   }

   public XHyperlinkChangeTypeSelectionDam(String label) {
      super(label);
   }

   public XHyperlinkChangeTypeSelectionDam(String label, ChangeTypes... changeTypes) {
      super(label, changeTypes);
   }

   @Override
   protected void handleSelected(ChangeTypes selected) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set Change Type");
      if (selected == null || selected == ChangeTypes.None) {
         changes.deleteAttributes(artifact, attributeType);
      } else {
         changes.setSoleAttributeValue(artifact, attributeType, selected.name());
      }
      changes.executeIfNeeded();
      super.handleSelected(selected);
   }

   @Override
   protected List<ChangeTypes> getChangeTypes() {
      if (changeTypes != null && !changeTypes.isEmpty()) {
         return changeTypes;
      }
      List<ChangeTypes> options =
         AtsApiService.get().getWorkItemService().getChangeTypeOptions((IAtsTeamWorkflow) artifact);
      return options;
   }

   @Override
   public String getCurrentValue() {
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

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public IStatus isValid() {
      IStatus status = Status.OK_STATUS;
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
      return status;
   }

}
