/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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

   public static final String WIDGET_ID = XArtifactReferencedAtsObjectAttributeWidget.class.getSimpleName();

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
            IAtsWorkDefinition workDef =
               AtsClientService.get().getWorkDefinitionService().getWorkDefinition(Long.valueOf(value));
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
                  AtsClientService.get().getQueryService().getArtifactTokenOrSentinal(ArtifactId.valueOf(value));
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
