/*
 * Created on Apr 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.event;

/**
 * @author Donald G. Dunne
 */
public interface IBasicGuidRelation {

   DefaultBasicGuidArtifact getArtA();

   DefaultBasicGuidArtifact getArtB();

   String getRelTypeGuid();

   String getBranchGuid();

   int getGammaId();

}
