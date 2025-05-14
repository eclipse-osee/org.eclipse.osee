/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.api.workflow.pr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class PrViewData {

   List<String> headers = new ArrayList<>();
   List<ArtifactToken> prWfs = new ArrayList<>();
   List<ArtifactResultRow> artRows = new ArrayList<>();
   XResultData rd = new XResultData();

   public PrViewData() {
      /// for jsx-rs
   }

   public List<ArtifactResultRow> getArtRows() {
      return artRows;
   }

   public List<String> getHeaders() {
      return headers;
   }

   public void setHeaders(List<String> headers) {
      this.headers = headers;
   }

   public void setArtRows(List<ArtifactResultRow> artRows) {
      this.artRows = artRows;
   }

   public Collection<ArtifactToken> getArtTokens() {
      List<ArtifactToken> artifacts = new ArrayList<>();
      for (ArtifactResultRow row : artRows) {
         artifacts.add(row.getArtifact());
      }
      return artifacts;
   }

   public void add(ArtifactResultRow row) {
      artRows.add(row);
   }

   public List<ArtifactToken> getPrWfs() {
      return prWfs;
   }

   public void setPrWfs(List<ArtifactToken> prWfs) {
      this.prWfs = prWfs;
   }

   public void addPrWf(ArtifactToken prWf) {
      prWfs.add(prWf);
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

}
