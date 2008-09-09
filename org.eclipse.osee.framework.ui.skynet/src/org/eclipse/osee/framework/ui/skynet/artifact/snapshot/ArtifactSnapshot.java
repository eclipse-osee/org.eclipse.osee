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
package org.eclipse.osee.framework.ui.skynet.artifact.snapshot;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;

/**
 * @author Roberto E. Escobar
 */
final class ArtifactSnapshot implements Serializable {
   private static final long serialVersionUID = -8702924997281390156L;

   private String namespace;
   private String key;
   private String renderedData;
   private int gammaId;
   private Timestamp createdOn;
   private Map<String, byte[]> binaryData;

   protected ArtifactSnapshot(String namespace, String key, Artifact artifact) throws OseeCoreException, SQLException {
      this.namespace = namespace;
      this.key = key;
      this.gammaId = getGamma(artifact);
      this.binaryData = new HashMap<String, byte[]>();
      this.createdOn = getCreationDate(artifact);
   }

   public Timestamp getCreatedOn() {
      return createdOn;
   }

   protected void setRenderedData(String data) {
      this.renderedData = data;
   }

   public boolean isDataValid() {
      return Strings.isValid(this.renderedData);
   }

   protected void addBinaryData(String key, byte[] data) {
      this.binaryData.put(key, data);
   }

   public String getNamespace() {
      return namespace;
   }

   public String getRenderedData() {
      return renderedData;
   }

   public int getGamma() {
      return gammaId;
   }

   public byte[] getBinaryData(String key) {
      byte[] toReturn = binaryData.get(key);
      return toReturn != null ? toReturn : new byte[0];
   }

   public String getKey() {
      return key;
   }

   public String toString() {
      return String.format("Snapshot: %s - %s \nCreated On: %s\t Binary Objects: %s", getNamespace(), getKey(),
            getCreatedOn(), binaryData.size());
   }

   /**
    * Determine whether data in the snapshot is stale compared to data in the artifact
    * 
    * @param artifact The artifact in question
    * @return isStale <b>true</b> if the snapshot is stale, otherwise <b>false</b>
    */
   public boolean isStaleComparedTo(Artifact artifact) throws OseeCoreException, SQLException {
      boolean snapshotGammaLessThanArts = this.getGamma() != getGamma(artifact);
      boolean snapshotCreationAfterArtifacts = this.getCreatedOn().before(getCreationDate(artifact));
      return snapshotGammaLessThanArts || snapshotCreationAfterArtifacts;
   }

   /**
    * Determine whether this snapshot is a valid representation of the artifact
    * 
    * @param artifact The artifact in question
    * @return isValid <b>true</b> if the snapshot pertains to this version of the artifact, otherwise <b>false</b>
    */
   public boolean isValidFor(Artifact artifact) throws OseeCoreException, SQLException {
      boolean gammasAreEqual = this.getGamma() == getGamma(artifact);
      long snapTime = this.getCreatedOn().getTime();
      long artTime = getCreationDate(artifact).getTime();
      boolean creationDatesMatch = snapTime >= artTime;
      return gammasAreEqual && creationDatesMatch;
   }

   private int getGamma(Artifact artifact) {
      return artifact.getGammaId();
   }

   private Timestamp getCreationDate(Artifact artifact) throws OseeCoreException, SQLException {
      List<TransactionData> txData =
            new ArrayList<TransactionData>(RevisionManager.getInstance().getTransactionsPerArtifact(artifact));
      for (TransactionData data : txData) {
         if (artifact.getArtId() == data.getAssociatedArtId()) {
            return data.getTimeStamp();
         }
      }
      return null;
   }
}
