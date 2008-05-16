/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * Enumeration used to determine the level of artifact data that will be pre-loaded from the datastore
 * 
 * @author Ryan D. Brooks
 */
public enum ArtifactLoad {
   SHALLOW, FULL, RELATION, ATTRIBUTE, FULL_ATTRIBUTE, FULL_FULL, SHALLOW_FULL;
}
