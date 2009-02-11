/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractLegacyArtifactSearchQuery extends AbstractArtifactSearchQuery {

   protected abstract Collection<Artifact> getArtifacts() throws Exception;

   public IStatus run(final IProgressMonitor pm) {
      setIsDoneRunning(false);
      aResult.removeAll();
      try {
         for (Artifact artifact : getArtifacts()) {
            Match match = new Match(artifact, 1, 2);
            aResult.addMatch(match);
         }
      } catch (Exception ex) {
         OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
      }

      setIsDoneRunning(true);
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }
}
