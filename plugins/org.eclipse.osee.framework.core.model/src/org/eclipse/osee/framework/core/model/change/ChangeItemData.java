/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.change;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * Collection of changes from ChangeItems. Provides convenience methods to request rolled up information about changes.
 *
 * @author Donald G. Dunne
 */
public class ChangeItemData {

   private final Collection<ChangeItem> changes;
   private boolean loaded = false;
   private final Map<ArtifactId, ChangeReportRollup> rollups = new HashMap<ArtifactId, ChangeReportRollup>();

   public ChangeItemData(Collection<ChangeItem> changes) {
      this.changes = changes;
   }

   public boolean isEmpty() {
      return getChanges() == null || getChanges().isEmpty();
   }

   public Collection<ChangeItem> getChanges() {
      return changes;
   }

   /**
    * Run through change items and categorize changes for convenience methods to use
    */
   public void ensureLoaded() {
      if (!loaded) {
         for (ChangeItem item : changes) {
            ArtifactId artA = item.getArtId();
            processArtifact(artA, item);
            ArtifactId artB = item.getArtIdB();
            processArtifact(artB, item);
         }
         loaded = true;
      }
   }

   public void processArtifact(ArtifactId art, ChangeItem item) {
      if (art.isValid()) {
         ChangeReportRollup rollup = rollups.get(art);
         if (rollup == null) {
            rollup = new ChangeReportRollup(art);
            rollups.put(art, rollup);
         }
         rollup.getChangeItems().add(item);
      }
   }

   public Map<ArtifactId, ChangeReportRollup> getRollups() {
      ensureLoaded();
      return rollups;
   }

}
