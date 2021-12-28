/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactReferencedAttributeWidget;

/**
 * @author Donald G. Dunne
 */
public class XArtifactReferencedAtsObjectAttributeWidget extends XArtifactReferencedAttributeWidget {

   public static final String SENTINEL = "-1";

   public XArtifactReferencedAtsObjectAttributeWidget(String displayLabel) {
      super(displayLabel);
   }

   public XArtifactReferencedAtsObjectAttributeWidget(String displayLabel, ArtifactTypeToken validArtifactType) {
      super(displayLabel, validArtifactType);
   }

   @Override
   protected String getDisplayValue(String value) {
      String displayValue = value;
      String errorMsg = "";
      if (Strings.isNumeric(value)) {
         boolean isWorkDef = false;
         if (getAttributeType().equals(AtsAttributeTypes.WorkflowDefinitionReference) || getAttributeType().equals(
            AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference) || getAttributeType().equals(
               AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference)) {
            if (value.equals(SENTINEL)) {
               return "";
            }
            IAtsWorkDefinition workDef =
               AtsApiService.get().getWorkDefinitionService().getWorkDefinition(Long.valueOf(value));
            isWorkDef = true;
            if (workDef != null && workDef.isValid()) {
               displayValue = workDef.toStringWithId();
            } else {
               errorMsg = String.format("Error: Can't retrieve work def with id %s", value);
            }
         }
         if (!isWorkDef) {
            try {
               ArtifactToken artifactToken =
                  AtsApiService.get().getQueryService().getArtifactTokenOrSentinal(ArtifactId.valueOf(value));
               if (artifactToken.isValid()) {
                  displayValue = artifactToken.toStringWithId();
               } else {
                  errorMsg = String.format("Error: No artifact found with id %s", value);
               }
            } catch (Exception ex) {
               errorMsg =
                  String.format("Error: Exception retrieving artifact with id %s (see Error Log for details)", value);
               OseeLog.log(XArtifactReferencedAtsObjectAttributeWidget.class, Level.SEVERE, errorMsg, ex);
            }
         }
      }
      if (Strings.isValid(errorMsg)) {
         setMessage(IStatus.ERROR, errorMsg);
      }
      return displayValue;
   }

}
