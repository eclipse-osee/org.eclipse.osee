/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

/**
 * @author Andrew M. Finkbeiner
 */
public class TemplateManager {
   private static final String EXTENSION_ID = "org.eclipse.osee.framework.ui.skynet.TemplateProvider";
   private static final String EXTENSION_ELEMENT = "TemplateProvider";
   private static final String EXTENSION_CLASSNAME = "classname";
   private final List<ITemplateProvider> templateProviders;
   private static final TemplateManager instance = new TemplateManager();

   private TemplateManager() {
      ExtensionDefinedObjects<ITemplateProvider> extensionDefinedObjects =
         new ExtensionDefinedObjects<ITemplateProvider>(EXTENSION_ID, EXTENSION_ELEMENT, EXTENSION_CLASSNAME);
      templateProviders = extensionDefinedObjects.getObjects();
   }

   public static Artifact getTemplate(IRenderer renderer, Artifact artifact, PresentationType presentationType, String option) {
      ITemplateProvider bestTemplateProvider = null;
      int highestRating = 0;
      for (ITemplateProvider templateProvider : instance.templateProviders) {
         int rating = templateProvider.getApplicabilityRating(renderer, artifact, presentationType, option);
         if (rating > highestRating) {
            bestTemplateProvider = templateProvider;
            highestRating = rating;
         }
      }
      if (bestTemplateProvider != null) {
         return bestTemplateProvider.getTemplate(renderer, artifact, presentationType, option);
      }

      return null;
   }

   public static List<Artifact> getAllTemplates() {
      List<Artifact> templates = new ArrayList<>();
      try {
         for (ITemplateProvider provider : instance.templateProviders) {
            templates.addAll(provider.getAllTemplates());
         }
         Collections.sort(templates);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return templates;
   }
}