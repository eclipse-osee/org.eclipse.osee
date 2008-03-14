/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author b1528444
 *
 */
public class SRSSpecialPublishingAttributeHandler implements ITemplateAttributeHandler {

	
	public SRSSpecialPublishingAttributeHandler(){
	}
	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#process(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
	 */
	@Override
	public void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute attribute)
			throws SQLException, IllegalStateException, IOException {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#canHandle(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
	 */
	@Override
	public boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws SQLException{
		 // This is for SRS Publishing. Do not publish unspecified attributes
	      if ((attribute.getName().equals("Partition") || attribute.getName().equals("Safety Criticality"))) {
	         for (Attribute partition : artifact.getAttributeManager("Partition").getAttributes()) {
	            if (partition.getStringData().equals("Unspecified")) {
	               return true;
	            }
	         }
	      }
	      return false;
	}

}
