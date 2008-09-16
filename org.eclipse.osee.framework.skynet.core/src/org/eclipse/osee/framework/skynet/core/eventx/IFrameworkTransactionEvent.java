/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;

/**
 * @author Donald G. Dunne
 */
public interface IFrameworkTransactionEvent extends IXEventListener {

   public void handleArtifactsAdded(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts);

   public void handleArtifactsChanged(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts);

   public void handleArtifactsDeleted(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts);

   public void handleRelationsAdded(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation);

   public void handleRelationsChanged(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation);

   public void handleRelationsDeleted(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation);

}
