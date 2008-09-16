/*
 * Created on Sep 15, 2008
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
public class FrameworkTransactionEvent implements IFrameworkTransactionEvent {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleArtifactsAdded(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsAdded(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleArtifactsChanged(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsChanged(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleArtifactsDeleted(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsDeleted(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<UnloadedArtifact> unloadedArtifacts) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleRelationsAdded(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleRelationsAdded(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleRelationsChanged(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleRelationsChanged(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEvent#handleRelationsDeleted(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, java.util.Collection, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleRelationsDeleted(Source source, Collection<? extends Artifact> cacheArtifacts, Collection<LoadedRelation> cacheRelations, Collection<UnloadedRelation> unloadedRelation) {
   }

}
