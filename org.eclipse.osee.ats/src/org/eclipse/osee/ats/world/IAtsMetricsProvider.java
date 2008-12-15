/*
 * Created on Nov 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsMetricsProvider {

   public Collection<? extends Artifact> getMetricsArtifacts() throws OseeCoreException;

   public VersionArtifact getMetricsVersionArtifact() throws OseeCoreException;

   public double getManHoursPerDayPreference() throws OseeCoreException;
}
