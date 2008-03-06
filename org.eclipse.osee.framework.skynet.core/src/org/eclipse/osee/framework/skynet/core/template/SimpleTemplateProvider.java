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

import java.util.HashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

/**
 * @author Ryan D. Brooks This provider gets all of its templates from the common branch based on a name created from
 *         concatenating the getTemplate parameters together
 */
public class SimpleTemplateProvider implements ITemplateProvider {
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private final HashMap<String, Artifact> templateMap;

   public SimpleTemplateProvider() {
      templateMap = new HashMap<String, Artifact>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#addTemplate(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, java.lang.String, org.eclipse.osee.framework.skynet.core.template.TemplateLocator)
    */
   @Override
   public void addTemplate(String rendererId, Branch branch, String presentationType, TemplateLocator locationData) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#getTemplate(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, java.lang.String)
    */
   @Override
   public String getTemplate(String rendererId, Branch branch, Artifact artifact, String presentationType, String option) throws Exception {
      String templateName = rendererId + " " + presentationType + " " + option;
      Artifact template = templateMap.get(templateName);
      if (template == null) {
         template =
               artifactManager.getArtifactFromTypeName("Template (WordML)", templateName,
                     branchManager.getCommonBranch());
         templateMap.put(templateName, template);
      }
      return template.getSoleXAttributeValue(WordAttribute.CONTENT_NAME);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#initializeTemplates(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor)
    */
   @Override
   public void initializeTemplates(Artifact documentFolder, ArtifactSubtypeDescriptor documentDescriptor) throws Exception {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#setDefaultTemplates(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   @Override
   public void setDefaultTemplates(String rendererId, Artifact document, String presentationType, Branch branch) throws Exception {
   }

}
