/*
 * Created on Feb 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactXmlQueryResultParser.MatchLocation;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch {
   private Artifact artifact;
   private boolean allowDeleted;
   private HashCollection<Long, MatchLocation> attributeMatches;

   protected ArtifactMatch(Artifact artifact, boolean allowDeleted) {
      this.artifact = artifact;
      this.allowDeleted = allowDeleted;
      this.attributeMatches = null;
   }

   public boolean hasMatchData() {
      return attributeMatches != null;
   }

   protected void addMatches(HashCollection<Long, MatchLocation> attributeMatches) {
      this.attributeMatches = attributeMatches;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public HashCollection<Attribute<?>, MatchLocation> getMatchData() throws OseeCoreException {
      HashCollection<Attribute<?>, MatchLocation> matchData = new HashCollection<Attribute<?>, MatchLocation>();

      for (Attribute<?> attribute : artifact.getAttributes(allowDeleted)) {
         Collection<MatchLocation> locations = attributeMatches.getValues((long) attribute.getGammaId());
         if (locations != null) {
            matchData.put(attribute, locations);
         }
      }
      return matchData;
   }
}
