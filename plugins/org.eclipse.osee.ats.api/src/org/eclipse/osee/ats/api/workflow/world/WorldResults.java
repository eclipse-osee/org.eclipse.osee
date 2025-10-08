/*********************************************************************
 * Copyright (c) 2017 Boeing
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
package org.eclipse.osee.ats.api.workflow.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class WorldResults {

   private List<String> orderedHeaders = new ArrayList<>();
   private List<Map<String, String>> rows = new ArrayList<>();
   private XResultData rd = new XResultData();
   private TransactionToken tx = TransactionToken.SENTINEL;
   private ArtifactToken collectorArt = ArtifactToken.SENTINEL;
   private String atsId = "";
   private String title = "";

   public List<Map<String, String>> getRows() {
      return rows;
   }

   public void setRows(List<Map<String, String>> rows) {
      this.rows = rows;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   public List<String> getOrderedHeaders() {
      return orderedHeaders;
   }

   public void setOrderedHeaders(List<String> orderedHeaders) {
      this.orderedHeaders = orderedHeaders;
   }

   public TransactionToken getTx() {
      return tx;
   }

   public void setTx(TransactionToken tx) {
      this.tx = tx;
   }

   public ArtifactToken getCollectorArt() {
      return collectorArt;
   }

   public void setCollectorArt(ArtifactToken collectorArt) {
      this.collectorArt = collectorArt;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

}
