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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
final class ArtifactSnapshot {

   private final String guid;
   private final long gammaId;
   private final Timestamp createdOn;

   private String renderedData;
   private Map<String, byte[]> binaryData;

   ArtifactSnapshot(String guid, long gammaId, Timestamp creationDate) throws OseeCoreException {
      this.guid = guid;
      this.gammaId = gammaId;
      this.createdOn = creationDate;
      this.renderedData = null;
      this.binaryData = new HashMap<String, byte[]>();
   }

   protected void setRenderedData(String data) {
      this.renderedData = data;
   }

   public Timestamp getCreatedOn() {
      return createdOn;
   }

   public boolean isDataValid() {
      return Strings.isValid(renderedData);
   }

   protected void addBinaryData(String key, byte[] data) {
      this.binaryData.put(key, data);
   }

   public String getGuid() {
      return guid;
   }

   public String getRenderedData() {
      return renderedData;
   }

   public long getGamma() {
      return gammaId;
   }

   public Set<String> getBinaryDataKeys() {
      return this.binaryData.keySet();
   }

   public byte[] getBinaryData(String key) {
      byte[] toReturn = binaryData.get(key);
      return toReturn != null ? toReturn : new byte[0];
   }

   public String toString() {
      return String.format("Snapshot: %s - %s \nCreated On: %s\t Binary Objects: %s", getGuid(), getGamma(),
            getCreatedOn(), binaryData.size());
   }
}
