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

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

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
    * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#getTemplate(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, java.lang.String)
    */
   @Override
   public String getTemplate(IRenderer rendererId, Artifact artifact, String presentationType, String option) throws Exception {
	  
	   List<String> possibleTemplateNames = getPossibleTemplateNamesStartingWithTheMostSpecialized(rendererId, artifact, presentationType, option);
	   
	   for(String name:possibleTemplateNames){
		   Artifact template =
               artifactManager.getArtifactFromTypeName("Template (WordML)", name,
                     branchManager.getCommonBranch(), false);
		   if(template != null){
			   templateMap.put(name, template);
			   return template.getSoleXAttributeValue(WordAttribute.CONTENT_NAME);
		   }
	   }
	   throw new IllegalStateException(String.format("Unable to find a valid tempalte for [%s, %s, %s, %s].", rendererId.toString(), artifact.toString(), presentationType, option));
   }
   
   private List<String> getPossibleTemplateNamesStartingWithTheMostSpecialized(IRenderer rendererId, Artifact artifact, String presentationType, String option){
	   if(rendererId == null || presentationType == null){
		   throw new IllegalArgumentException(String.format(
	               "Invalid renderer[%s] or presentationType[%s]", rendererId.toString(), presentationType.toString()));
	   }
	   List<String> list = new ArrayList<String>();
	   if(artifact != null && option != null){
		  list.add(rendererId.getId() + " " + artifact.getArtifactTypeName() + " " + presentationType + " " + option);
	   }
	   if(option != null){
		  list.add(rendererId.getId() + " " + presentationType + " " + option);
	   }
	   list.add(rendererId.getId() + " " + presentationType);
	   return list;
   }

/* (non-Javadoc)
 * @see org.eclipse.osee.framework.skynet.core.template.ITemplateProvider#getApplicabilityRating(java.lang.String, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, java.lang.String)
 */
@Override
public int getApplicabilityRating(IRenderer rendererId, Artifact artifact,
		String presentationType, String option) {
	return ITemplateProvider.DEFAULT_MATCH;
}

public void somefunctionToBeUsedbyDBinit() throws SQLException, IllegalStateException, IOException{
	 Artifact templateFolder = ArtifactPersistenceManager.getInstance().getArtifactFromTypeName("Folder", "Document Templates", BranchPersistenceManager.getInstance().getCommonBranch(), true);

     ArtifactSubtypeDescriptor descriptor = ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Template (WordML)");
     Artifact template = descriptor.makeNewArtifact(templateFolder.getBranch());
     String name = null;
	template.setDescriptiveName(name);
     InputStream stream = null;
	template.setSoleAttributeFromStream(WordAttribute.CONTENT_NAME, stream);
     templateFolder.addChild(template);
}

}
