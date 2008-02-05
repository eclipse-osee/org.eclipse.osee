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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.template.AbstractTemplateProvider;
import org.eclipse.osee.framework.skynet.core.template.TemplateLocator;

/**
 * @author Roberto E. Escobar
 */
public class WordTemplateProvider extends AbstractTemplateProvider {
   private HashMap<String, String> defaultTemplateMap;

   private static WordTemplateProvider instance = null;

   private WordTemplateProvider() {
      this.defaultTemplateMap = null;
   }

   public static WordTemplateProvider getInstance() {
      if (instance == null) {
         instance = new WordTemplateProvider();
      }
      return instance;
   }

   public void setDefaultTemplates(String rendererId, Artifact document, String presentationType, Branch branch) throws Exception {
      if (document != null) {
         document.setDescriptiveName(rendererId + " " + presentationType);
      }
      super.setDefaultTemplates(rendererId, document, presentationType, branch);
   }

   protected HashMap<String, String> getDefaultTemplateMap() throws Exception {
      if (defaultTemplateMap == null) {
         defaultTemplateMap = new HashMap<String, String>();
         try {
            List<TemplateLocator> defaultTemplates = getExtensionDefinedTemplates();
            for (TemplateLocator templateLocationData : defaultTemplates) {
               addTemplateToMap(defaultTemplateMap, templateLocationData);
            }
         } catch (Exception ex) {
            throw new Exception("Unable to load extension defined templates.", ex);
         }
      }
      return defaultTemplateMap;
   }

   private List<TemplateLocator> getExtensionDefinedTemplates() {
      List<TemplateLocator> extensionTemplates = new ArrayList<TemplateLocator>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements("org.eclipse.osee.framework.ui.skynet.ArtifactRendererTemplate",
                  "Template");

      for (IConfigurationElement element : elements) {
         String bundleName = element.getContributor().getName();
         String templateName = element.getAttribute("templateName");
         String templatePath = element.getAttribute("templateFile");
         extensionTemplates.add(new TemplateLocator(bundleName, templateName, templatePath));
      }
      return extensionTemplates;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#initializeTemplates(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   public void initializeTemplates(Artifact parentFolder, ArtifactSubtypeDescriptor documentDescriptor) throws Exception {
      String wordRendererId = "org.eclipse.osee.framework.ui.skynet.word";
      for (PresentationType presentationType : PresentationType.values()) {
         Artifact document = documentDescriptor.makeNewArtifact();
         parentFolder.addChild(document);
         setDefaultTemplates(wordRendererId, document, presentationType.name(), parentFolder.getBranch());
      }
   }
}
