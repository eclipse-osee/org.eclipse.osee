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
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.review.defect.AtsXDefectValidator;
import org.eclipse.osee.ats.core.client.review.role.AtsXUserRoleValidator;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidateManager;
import org.eclipse.osee.ats.core.validator.IAtsXWidgetValidator;
import org.eclipse.osee.ats.core.validator.WidgetResult;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManagerClient {
   private static boolean loaded = true;
   private static final String EXTENSION_ELEMENT = "AtsXWidgetValidator";
   private static final String EXTENSION_ID = Activator.PLUGIN_ID + "." + EXTENSION_ELEMENT;
   private static final String CLASS_NAME_ATTRIBUTE = "classname";

   private static void ensureLoaded() {
      if (loaded) {
         AtsXWidgetValidateManager.add(new AtsXHyperlinkMemberSelValidator());
         AtsXWidgetValidateManager.add(new AtsXDefectValidator());
         AtsXWidgetValidateManager.add(new AtsXUserRoleValidator());
         AtsXWidgetValidateManager.add(new AtsXCommitManagerValidator());
         AtsXWidgetValidateManager.add(new AtsOperationalImpactValidator());
         AtsXWidgetValidateManager.add(new AtsOperationalImpactWithWorkaroundValidator());

         ExtensionDefinedObjects<IAtsXWidgetValidator> validators =
            new ExtensionDefinedObjects<IAtsXWidgetValidator>(EXTENSION_ID, EXTENSION_ELEMENT, CLASS_NAME_ATTRIBUTE,
               true);
         for (IAtsXWidgetValidator validator : validators.getObjects()) {
            AtsXWidgetValidateManager.add(validator);
         }
      }
   }

   public static Collection<WidgetResult> validateTransition(AbstractWorkflowArtifact awa, StateDefinition toStateDef) {
      ensureLoaded();
      List<WidgetResult> results = new ArrayList<WidgetResult>();
      for (WidgetDefinition widgetDef : awa.getStateDefinition().getWidgetsFromStateItems()) {
         validateTransition(results, awa, widgetDef, awa.getStateDefinition(), toStateDef);
      }
      return results;
   }

   public static void validateTransition(List<WidgetResult> results, AbstractWorkflowArtifact awa, WidgetDefinition widgetDef, StateDefinition fromStateDef, StateDefinition toStateDef) {
      ensureLoaded();
      ArtifactValueProvider provider = new ArtifactValueProvider(awa, widgetDef);
      AtsXWidgetValidateManager.instance.validateTransition(results, provider, widgetDef, fromStateDef, toStateDef);
   }
}
