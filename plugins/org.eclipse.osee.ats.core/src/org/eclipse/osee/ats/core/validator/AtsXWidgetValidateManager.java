/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.core.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {
   private static final String EXTENSION_ELEMENT = "AtsXWidgetValidator";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";
   private static List<IAtsXWidgetValidator> atsValidators;
   public static AtsXWidgetValidateManager instance = new AtsXWidgetValidateManager();

   private AtsXWidgetValidateManager() {
      atsValidators = new ArrayList<IAtsXWidgetValidator>();
      atsValidators.add(new AtsXIntegerValidator());
      atsValidators.add(new AtsXFloatValidator());
      atsValidators.add(new AtsXTextValidator());
      atsValidators.add(new AtsXDateValidator());
      atsValidators.add(new AtsXComboValidator());
      atsValidators.add(new AtsXComboBooleanValidator());
      atsValidators.add(new AtsXListValidator());
      atsValidators.add(new AtsXHyperlinkMemberSelValidator());
      atsValidators.add(new AtsXDefectValidator());
      atsValidators.add(new AtsXUserRoleValidator());
      atsValidators.add(new AtsXCommitManagerValidator());
      atsValidators.add(new AtsOperationalImpactValidator());
      atsValidators.add(new AtsOperationalImpactWithWorkaroundValidator());
      ExtensionDefinedObjects<IAtsXWidgetValidator> validators =
         new ExtensionDefinedObjects<IAtsXWidgetValidator>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE, true);
      atsValidators.addAll(validators.getObjects());
   }

   public Collection<WidgetResult> validateTransition(AbstractWorkflowArtifact awa, StateDefinition toStateDef) {
      List<WidgetResult> results = new ArrayList<WidgetResult>();
      for (WidgetDefinition widgetDef : awa.getStateDefinition().getWidgetsFromStateItems()) {
         validateTransition(results, awa, widgetDef, awa.getStateDefinition(), toStateDef);
      }
      return results;
   }

   public void validateTransition(List<WidgetResult> results, AbstractWorkflowArtifact awa, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) {
      ArtifactValueProvider provider = new ArtifactValueProvider(awa, widgetDef);
      for (IAtsXWidgetValidator validator : atsValidators) {
         try {
            WidgetResult status = validator.validateTransition(provider, widgetDef, fromStateDef, toStateDef);
            if (!status.isValid()) {
               results.add(status);
            }
         } catch (Exception ex) {
            results.add(new WidgetResult(WidgetStatus.Exception, widgetDef, ex, String.format(
               "Exception retriving validation for widget [%s] Exception [%s]", validator.getClass().getSimpleName(),
               ex.getLocalizedMessage()), ex));
            return;
         }
      }
   }
}
