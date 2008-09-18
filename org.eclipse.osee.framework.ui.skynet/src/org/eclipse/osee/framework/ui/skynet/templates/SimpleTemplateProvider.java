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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks This provider gets all of its templates from the common branch based on a name created from
 *         concatenating the getTemplate parameters together
 */
public class SimpleTemplateProvider implements ITemplateProvider {
   private HashMap<String, Artifact> templateMap;

   public SimpleTemplateProvider() {

   }

   private synchronized void ensureTemplateCachePopulated() throws SQLException, OseeCoreException {
      if (templateMap == null) {
         templateMap = new HashMap<String, Artifact>();
         Collection<Artifact> artifacts =
               ArtifactQuery.getArtifactsFromType("Renderer Template", BranchPersistenceManager.getCommonBranch());
         for (Artifact art : artifacts) {
            Collection<Attribute<String>> attrs = art.getAttributes("Template Match Criteria");
            for (Attribute<String> attr : attrs) {
               String matchCriteria = attr.getValue();
               Artifact cachedArt = templateMap.get(matchCriteria);
               if (cachedArt == null) {
                  templateMap.put(matchCriteria, art);
               } else { //use the artifact with the higher name value and warn the user that there are duplicate match criteria
                  int value = cachedArt.getDescriptiveName().compareTo(art.getDescriptiveName());
                  if (value < 0) {
                     templateMap.put(matchCriteria, art);
                  }
                  OSEELog.logSevere(
                        SimpleTemplateProvider.class,
                        String.format(
                              "SimpleTemplateProvider has detected a conflict with 'Template Match Criteria' [%s].  Artifact [%s] will supply the template for all requests with this match criteria.",
                              matchCriteria, templateMap.get(matchCriteria).getDescriptiveName()), false);

               }
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#getTemplate(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, java.lang.String)
    */
   public String getTemplate(IRenderer renderer, Artifact artifact, String presentationType, String option) throws Exception {
      ensureTemplateCachePopulated();
      List<String> possibleTemplateNames =
            getPossibleTemplateNamesOrderedBySpecialization(renderer, artifact, presentationType, option);

      for (String name : possibleTemplateNames) {
         Artifact template = templateMap.get(name);
         if (template != null) {
            return template.getSoleAttributeValue(AttributeTypeManager.getTypeWithWordContentCheck(template,
                  WordAttribute.CONTENT_NAME).getName());
         }
      }
      throw new IllegalStateException(String.format("Unable to find a valid template match for [%s, %s, %s, %s].",
            renderer.toString(), artifact.toString(), presentationType, option));
   }

   private List<String> getPossibleTemplateNamesOrderedBySpecialization(IRenderer renderer, Artifact artifact, String presentationType, String option) {
      if (renderer == null || presentationType == null) {
         throw new IllegalArgumentException(String.format("Invalid renderer[%s] or presentationType[%s]",
               renderer.toString(), presentationType.toString()));
      }
      List<String> list = new ArrayList<String>();

      if (artifact != null && option != null) {
         list.add(renderer.getId() + " " + artifact.getArtifactTypeName() + " " + presentationType + " " + option);
      }
      if (artifact != null) {
         list.add(renderer.getId() + " " + artifact.getArtifactTypeName() + " " + presentationType);
      }
      if (option != null) {
         list.add(renderer.getId() + " " + presentationType + " " + option);
      }

      list.add(renderer.getId() + " " + presentationType);
      return list;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#getApplicabilityRating(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, java.lang.String)
    */
   public int getApplicabilityRating(IRenderer rendererId, Artifact artifact, String presentationType, String option) {
      return ITemplateProvider.DEFAULT_MATCH;
   }

}
