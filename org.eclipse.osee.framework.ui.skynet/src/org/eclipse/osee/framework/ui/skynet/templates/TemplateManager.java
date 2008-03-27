/*
 * Created on Mar 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.templates;

import java.util.List;

import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

/**
 * @author b1528444
 *
 */
public class TemplateManager {
	
	List<ITemplateProvider> templateProviders;
	
   private static final String EXTENSION_ID = "org.eclipse.osee.framework.ui.skynet.TemplateProvider";
   private static final String EXTENSION_ELEMENT = "TemplateProvider";
   private static final String EXTENSION_CLASSNAME = "classname";
	
	private static TemplateManager instance;
	
	public static TemplateManager getInstance(){
		if(instance == null){
			instance = new TemplateManager();
		}
		return instance;
	}
	
	private TemplateManager(){
	  ExtensionDefinedObjects<ITemplateProvider> extensionDefinedObjects =
            new ExtensionDefinedObjects<ITemplateProvider>(EXTENSION_ID, EXTENSION_ELEMENT, EXTENSION_CLASSNAME);
	  templateProviders = extensionDefinedObjects.getObjects();
	}
	
	public String getTemplate(IRenderer renderer, Artifact artifact, String presentationType, String option) throws Exception{
		ITemplateProvider templateProviderToReturn = null;
		int highestRating = 0;
		for(ITemplateProvider templateProvider:templateProviders){
			int rating = templateProvider.getApplicabilityRating(renderer, artifact, presentationType, option);
			if(rating > highestRating){
				templateProviderToReturn = templateProvider;
				highestRating = rating;
			}
		}
		String template = templateProviderToReturn.getTemplate(renderer, artifact, presentationType, option);
		return template;
	}
}
