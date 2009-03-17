/*
 * Created on Feb 17, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision;

import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Jeff C. Phillips
 *
 */
public class InternalHistoryManager {
   private static InternalHistoryManager instance = new InternalHistoryManager();
   
   private InternalHistoryManager() {
   }

   public InternalHistoryManager getInstance(){
      return instance;
   }
   
   public List<Change> getArtifactHistory(Artifact artifact){      return null;
   }
}
