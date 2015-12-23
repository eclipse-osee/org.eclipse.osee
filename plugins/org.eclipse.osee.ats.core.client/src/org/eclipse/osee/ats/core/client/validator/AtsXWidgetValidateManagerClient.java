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
package org.eclipse.osee.ats.core.client.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.core.client.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.validator.AtsCoreXWidgetValidatorProvider;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidateManager;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidatorProvider;
import org.eclipse.osee.ats.core.validator.IAtsXWidgetValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManagerClient implements AtsXWidgetValidatorProvider {
   private static boolean loaded = false;
   private static final String EXTENSION_ELEMENT = "AtsXWidgetValidator";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";
   private static List<IAtsXWidgetValidator> atsValidators;
   public static AtsXWidgetValidateManagerClient instance = new AtsXWidgetValidateManagerClient();

   @Override
   public Collection<IAtsXWidgetValidator> getValidators() {
      if (atsValidators == null) {
         atsValidators = new ArrayList<>();
         atsValidators.add(new AtsXHyperlinkMemberSelValidator());
         atsValidators.add(new AtsXDefectValidator());
         atsValidators.add(new AtsXUserRoleValidator());
         atsValidators.add(new AtsXCommitManagerValidator());
         atsValidators.add(new AtsOperationalImpactValidator());
         atsValidators.add(new AtsOperationalImpactWithWorkaroundValidator());

         ExtensionDefinedObjects<IAtsXWidgetValidator> validators = new ExtensionDefinedObjects<IAtsXWidgetValidator>(
            EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE, true);
         for (IAtsXWidgetValidator validator : validators.getObjects()) {
            atsValidators.add(validator);
         }
      }
      return atsValidators;
   }

   private void ensureLoaded() {
      if (!loaded) {
         loaded = true;
         AtsXWidgetValidateManager.instance.add(instance);
         AtsXWidgetValidateManager.instance.add(new AtsCoreXWidgetValidatorProvider());
      }
   }

   public Collection<WidgetResult> validateTransition(AbstractWorkflowArtifact awa, IAtsStateDefinition toStateDef) throws OseeStateException {
      ensureLoaded();
      List<WidgetResult> results = new ArrayList<>();
      for (IAtsWidgetDefinition widgetDef : AtsClientService.get().getWorkDefinitionAdmin().getWidgetsFromLayoutItems(
         awa.getStateDefinition())) {
         ArtifactValueProvider provider = new ArtifactValueProvider(awa, widgetDef);
         AtsXWidgetValidateManager.instance.validateTransition(results, provider, widgetDef, awa.getStateDefinition(),
            toStateDef, AtsClientService.get().getServices());
      }
      return results;
   }

}
