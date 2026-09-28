/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.core.model.event;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranchId;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class DefaultBasicGuidArtifact extends BaseIdentity<String> implements HasBranchId {
   private final BranchId branch;
   private ArtifactTypeToken artifactType;
   private long artId;

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeToken artifactType, String artGuid) {
      super(artGuid);
      this.branch = branch;
      this.artifactType = artifactType;
   }

   public boolean isTypeEqual(ArtifactTypeToken artifactType) {
      return artifactType.equals(this.artifactType);
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactTypeToken artifactType) {
      this(branch, artifactType, GUID.create());
   }

   public DefaultBasicGuidArtifact(BranchId branch, ArtifactToken artifact) {
      this(branch, artifact.getArtifactType(), artifact.getGuid());
      this.artId = artifact.getId();
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public Long getArtTypeGuid() {
      return artifactType.getId();
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public String toString() {
      return String.format("branchId = %s; artType = %s; guid = %s", branch.getId(), artifactType.getId(), getGuid());
   }

   /**
    * Hashes by artId when set (the identity we are transitioning to), falling back to the GUID hash
    * only for legacy instances that carry no artId. artId is the key the primary path relies on:
    * a web-originated event's GUID field holds a numeric-id placeholder that differs from the cached
    * artifact's real GUID, so those two match only by artId -- and to match in a `HashSet` (e.g.
    * `ArtifactEvent.isModified()` -> `HashSet.contains`) they must also hash alike, by artId.
    *
    * The mixed-pair case (a new artId-bearing instance vs a legacy artId-less one, same real GUID)
    * is largely eliminated by the receiver: FrameworkEventUtil derives the artId from the GUID via
    * an in-memory cache lookup when a legacy sender omitted it, so both sides normally carry artId
    * and hash alike. It can only remain for an artifact NOT in the cache -- which is never displayed,
    * so no HashSet-gated UI reload depends on the match, and the cache delta-apply path resolves it
    * by field lookup regardless. The GUID equals/hash fallback is thus a rarely-hit safety net; it
    * (and this artId-vs-GUID split) goes away once every producer emits artId and the fallback is
    * removed (see the migration plan in docs/ai/web/sse-real-time.md).
    */
   @Override
   public int hashCode() {
      if (artId > 0) {
         return Long.hashCode(artId);
      }
      return super.hashCode();
   }

   /**
    * Prefers artId comparison when both sides have it (the identity we are moving to); otherwise
    * falls back to GUID + type comparison so legacy artId-less events (old desktop clients) still
    * match. Always requires the same branch.
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DefaultBasicGuidArtifact) {
         DefaultBasicGuidArtifact other = (DefaultBasicGuidArtifact) obj;
         // Prefer artId comparison when both have it set (new path)
         if (this.artId > 0 && other.artId > 0) {
            return this.artId == other.artId && isOnSameBranch(other);
         }
         // Fall back to GUID comparison (legacy path)
         boolean guidEquals = super.equals(obj);
         if (guidEquals) {
            return other.artifactType.equals(artifactType) && isOnSameBranch(other);
         }
         return false;
      }
      return super.equals(obj);
   }

   public void setArtTypeGuid(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   /**
    * Numeric artifact ID. New field for transitioning away from GUIDs.
    * Returns 0 if not set (constructed without an ArtifactToken).
    */
   public long getArtId() {
      return artId;
   }

   public void setArtId(long artId) {
      this.artId = artId;
   }
}