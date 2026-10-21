/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedPeerWorkflowDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedTaskWorkflowDefinitionOld;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.RelatedTaskWorkflowDefinitionReference;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.WorkflowDefinitionReference;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXWorkDefReferenceWidget extends XXAbstractBaseXWidget<AtsWorkDefinitionToken> {

   public static final WidgetId ID = WidgetId.XXWorkDefReferenceWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXWorkDefReferenceWidget() {
      this(ID, "Work Def");
   }

   public XXWorkDefReferenceWidget(String label) {
      this(ID, label);
   }

   public XXWorkDefReferenceWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_TEXT, XOption.NO_CLEAR);
      super.setAsProviderFor(//
         WorkflowDefinitionReference, //
         RelatedPeerWorkflowDefinitionReference, //
         RelatedTaskWorkflowDefinitionReference, //
         RelatedTaskWorkflowDefinitionOld);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public AtsWorkDefinitionToken getSentinel() {
      return AtsWorkDefinitionToken.SENTINEL;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof AtsWorkDefinitionToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact()) {
         setLabel(getAttributeType().getUnqualifiedName());
         ArtifactId workDefId = getArtifact().getSoleAttributeValue(getAttributeType(), ArtifactId.SENTINEL);
         if (workDefId.isValid()) {
            WorkDefinition workDef = AtsApiService.get().getWorkDefinitionService().getWorkDefinition(workDefId);
            if (workDef.isValid()) {
               setSelected(workDef.getWorkDefToken(), false);
            }
         }
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
