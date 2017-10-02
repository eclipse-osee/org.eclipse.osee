/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDelta {

   private final Artifact startArt;
   private final Artifact endArt;
   private final Artifact baseArt;
   private final TransactionDelta txDelta;

   public ArtifactDelta(TransactionDelta txDelta, Artifact startArt, Artifact endArt)  {
      if (startArt == null && endArt == null) {
         throw new OseeArgumentException("the start and end artifacts can not both be null.");
      }
      this.startArt = startArt;
      this.endArt = endArt;
      this.baseArt = startArt;
      this.txDelta = txDelta;
   }

   public ArtifactDelta(TransactionDelta txDelta, Artifact startArt, Artifact endArt, Artifact baseArt)  {
      if (startArt == null && endArt == null) {
         throw new OseeArgumentException("the start and end artifacts can not both be null.");
      }
      this.startArt = startArt;
      this.endArt = endArt;
      this.baseArt = baseArt;
      this.txDelta = txDelta;
   }

   public ArtifactDelta(Artifact startArt, Artifact endArt)  {
      this(null, startArt, endArt);
   }

   public TransactionDelta getTxDelta() {
      return txDelta;
   }

   public Artifact getStartArtifact() {
      return startArt;
   }

   public Artifact getEndArtifact() {
      return endArt;
   }

   public Artifact getBaseArtifact() {
      return baseArt;
   }

   public IOseeBranch getBranch() {
      return getStartArtifact() != null ? getStartArtifact().getBranchToken() : getEndArtifact().getBranchToken();
   }

   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof ArtifactDelta) {
         ArtifactDelta other = (ArtifactDelta) obj;
         boolean left = startArt == null ? other.startArt == null : startArt.equals(other.startArt);
         boolean right = endArt == null ? other.endArt == null : endArt.equals(other.endArt);
         result = left && right;
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = 17;
      if (startArt != null) {
         result = prime * result + startArt.hashCode();
      } else {
         result = prime * result;
      }
      if (endArt != null) {
         result = prime * result + endArt.hashCode();
      } else {
         result = prime * result;
      }
      return result;
   }

   @Override
   public String toString() {
      String firstString = String.valueOf(getStartArtifact());
      String secondString = String.valueOf(getEndArtifact());
      return String.format("[start:%s, end:%s]", firstString, secondString);
   }
}
