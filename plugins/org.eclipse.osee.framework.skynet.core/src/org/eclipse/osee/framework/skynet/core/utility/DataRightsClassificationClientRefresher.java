/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.DataRightsClassificationNameParser;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Client-side mirror of the server's required-indicator refresh. The client type registry is
 * compiled and independent of the server's, so footer-derived data rights classification names
 * promoted on the server are not visible to client attribute pick-lists until the client promotes
 * them into its own {@link CoreAttributeTypes#DataRightsClassification} valid enum set.
 * <p>
 * This reads the {@code DataRightsFooters} artifact from the common branch through the normal client
 * query path, derives classification names with the shared
 * {@link DataRightsClassificationNameParser}, and adds any new names to the valid enum set via
 * {@code addDbLoadedValidEnum}. The read is performed at most once per session (guarded by
 * {@link #refreshed}); the promotion is additive and idempotent, so seed and previously added values
 * are always retained. A missing or unreadable footers artifact leaves the compiled seed governing
 * and never throws.
 *
 * @author David W. Miller
 */

public final class DataRightsClassificationClientRefresher {

   private static volatile boolean refreshed = false;

   private DataRightsClassificationClientRefresher() {
      // Utility class
   }

   /**
    * Reads the footer-defined classification names once per session and promotes any that are not
    * already valid into the client {@code DataRightsClassification} valid enum set. Subsequent calls
    * are no-ops for the remainder of the session. Never throws for a missing or unreadable footers
    * artifact.
    */

   public static void ensureRefreshed() {

      if (DataRightsClassificationClientRefresher.refreshed) {
         return;
      }

      synchronized (DataRightsClassificationClientRefresher.class) {

         if (DataRightsClassificationClientRefresher.refreshed) {
            return;
         }

         try {
            DataRightsClassificationClientRefresher.promoteFooterClassifications();
         } catch (Exception e) {
            /*
             * A missing or unreadable footers artifact must leave the compiled seed governing. The
             * guard is still set so a failed read is not retried on every pick-list build; the seed
             * set remains fully valid regardless. Logged at FINE so a genuinely unreadable but
             * present artifact is diagnosable without disrupting pick-list construction.
             */
            OseeLog.log(Activator.class, Level.FINE, e);
         }

         DataRightsClassificationClientRefresher.refreshed = true;
      }
   }

   /**
    * Clears the once-per-session guard so the next {@link #ensureRefreshed()} re-reads the footers
    * artifact. Intended to be invoked when client caches are cleared so footer edits made during a
    * session are reflected in the {@code DataRightsClassification} pick-list after a cache refresh.
    * Already-promoted names remain valid (promotion is additive); this only permits a subsequent
    * re-read to pick up newly added footer names.
    */

   public static void reset() {
      DataRightsClassificationClientRefresher.refreshed = false;
   }

   private static void promoteFooterClassifications() {

      Artifact footersArtifact = ArtifactQuery.getArtifactFromToken(CoreArtifactTokens.DataRightsFooters);

      List<String> footerValues = footersArtifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);

      for (String footerValue : footerValues) {
         DataRightsClassificationNameParser.parseClassificationName(footerValue).ifPresent(
            CoreAttributeTypes.DataRightsClassification::addDbLoadedValidEnum);
      }
   }

}
