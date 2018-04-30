/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author David W. Miller
 */
public class WordUpdateChange {
   private TransactionId tx;
   private List<WordArtifactChange> changedArts = new LinkedList<>();
   private final Map<Long, String> trackedChangeArts = new HashMap<>();
   private final Map<Long, String> invalidApplicabilityTagsArts = new HashMap<>();
   private BranchId branch;

   public TransactionId getTx() {
      return tx;
   }

   public void setTx(TransactionId tx) {
      this.tx = tx;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public List<WordArtifactChange> getChangedArts() {
      return changedArts;
   }

   public void setChangedArts(List<WordArtifactChange> changedArts) {
      this.changedArts = changedArts;
   }

   public void addChangedArt(WordArtifactChange change) {
      this.changedArts.add(change);
   }

   public WordArtifactChange getWordArtifactChange(long artId) {
      WordArtifactChange toReturn = null;
      if (changedArts != null && !changedArts.isEmpty()) {
         for (WordArtifactChange change : changedArts) {
            if (change.artId == artId) {
               toReturn = change;
               break;
            }
         }
      }
      return toReturn;
   }

   public boolean hasSafetyRelatedArtifactChange() {
      boolean hasRelatedChange = false;
      for (WordArtifactChange change : changedArts) {
         if (change.isSafetyRelated()) {
            hasRelatedChange = true;
            break;
         }
         if (change.isCreated()) {
            hasRelatedChange = true;
            break;
         }
      }
      return hasRelatedChange;
   }

   public Map<Long, String> getTrackedChangeArts() {
      return trackedChangeArts;
   }

   public void setTrackedChangeArts(Long artId, String name) {
      trackedChangeArts.put(artId, name);
   }

   public Map<Long, String> getInvalidApplicabilityTagArts() {
      return invalidApplicabilityTagsArts;
   }

   public void setInvalidApplicabilityTagArts(Long artId, String name) {
      invalidApplicabilityTagsArts.put(artId, name);
   }
}
