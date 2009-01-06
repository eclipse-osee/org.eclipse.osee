/*
 * Created on Jan 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * For backward compatibility of removed BlobWordAttribute in DB
 * 
 * @author Ryan D. Brooks
 */
public class BlobWordAttribute extends StringAttribute {

   /**
    * @param attributeType
    * @param artifact
    */
   public BlobWordAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

}
