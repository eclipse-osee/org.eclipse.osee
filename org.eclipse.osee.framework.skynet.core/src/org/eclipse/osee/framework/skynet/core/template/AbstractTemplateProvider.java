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
package org.eclipse.osee.framework.skynet.core.template;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.JavaObjectAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractTemplateProvider implements ITemplateProvider {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(AbstractTemplateProvider.class);
   private static final String TEMPLATE_MAP_ATTRIBUTE_NAME = "Template Map";
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   private final DoubleKeyHashMap<Branch, String, Artifact> documentMap;

   protected AbstractTemplateProvider() {
      this.documentMap = new DoubleKeyHashMap<Branch, String, Artifact>();
   }

   @SuppressWarnings("unchecked")
   public void addTemplate(String rendererId, Branch branch, String presentationType, TemplateLocator locationData) throws Exception {
      Artifact document = getDocumentArtifact(rendererId, presentationType, branch);
      JavaObjectAttribute javaAttribute = getJavaObjectAttribute(document);
      HashMap<String, String> templateMap = (HashMap<String, String>) javaAttribute.getObject();
      if (templateMap == null) {
         templateMap = new HashMap<String, String>();
      }
      addTemplateToMap(templateMap, locationData);

      javaAttribute.setObject(templateMap);
      document.persistAttributes();
   }

   protected void addTemplateToMap(HashMap<String, String> templateMap, TemplateLocator locationData) throws IOException {
      templateMap.put(locationData.getTemplateName(), locationData.getTemplate());
   }

   public void setDefaultTemplates(String rendererId, Artifact document, String presentationType, Branch branch) throws Exception {
      if (document == null) {
         document = getDocumentArtifact(rendererId, presentationType, branch);
      }
      HashMap<String, String> defaultMap = getDefaultTemplateMap();
      JavaObjectAttribute javaAttribute = getJavaObjectAttribute(document);
      javaAttribute.setObject(defaultMap);
      document.persistAttributes();
   }

   protected abstract HashMap<String, String> getDefaultTemplateMap() throws Exception;

   protected Artifact getDocumentArtifact(String rendererId, String presentationType, Branch branch) throws SQLException {
      Artifact document = documentMap.get(branch, presentationType);
      if (document == null) {
         try {
            document = artifactManager.getArtifactFromTypeName("Document", rendererId + " " + presentationType, branch);
         } catch (IllegalStateException ex) {
            if (branch == branchManager.getCommonBranch()) {
               document = null;
            } else if (branch.getParentBranch() == null) {
               document = getDocumentArtifact(rendererId, presentationType, branchManager.getCommonBranch());
            } else {
               document = getDocumentArtifact(rendererId, presentationType, branch.getParentBranch());
            }
         }
         documentMap.put(branch, presentationType, document);
      }
      return document;
   }

   protected JavaObjectAttribute getJavaObjectAttribute(Artifact document) throws SQLException {
      return (JavaObjectAttribute) document.getAttributeManager(TEMPLATE_MAP_ATTRIBUTE_NAME).getSoleAttribute();
   }

   @SuppressWarnings("unchecked")
   public String getTemplate(String rendererId, Branch branch, Artifact artifact, String presentationType, String option) throws Exception {
      String template = null;
      try {
         Artifact document = getDocumentArtifact(rendererId, presentationType, branch);
         JavaObjectAttribute javaAttribute = getJavaObjectAttribute(document);
         HashMap<String, String> templateMap = (HashMap<String, String>) javaAttribute.getObject();

         if (option != null) {
            template = templateMap.get(option);
         }
         if (template == null && artifact != null) {
            template = templateMap.get(artifact.getArtifactTypeName());
         }

         if (template == null) {
            template = templateMap.get("default");
            if (template == null) {
               throw new IllegalArgumentException("No default template found for the artifact: " + document);
            }
         }
      } catch (Exception ex) {
         String message = "Error obtaining template from database. Using default templates.";
         template = getDefaultTemplateMap().get("default");
         handleException(new IllegalStateException(message, ex));
      }
      return template;
   }

   private void handleException(Exception ex) {
      AWorkbench.popup("ERROR", "" + ex.getLocalizedMessage());
      logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
   }
}
