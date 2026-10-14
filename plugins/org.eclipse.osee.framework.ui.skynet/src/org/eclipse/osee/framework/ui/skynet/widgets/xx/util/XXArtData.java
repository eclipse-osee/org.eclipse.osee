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

package org.eclipse.osee.framework.ui.skynet.widgets.xx.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * Class that, given a beginning artifact, can provide all the data for attrs, rels, relatedArts and etc. This can
 * eventually be sent from server rather than client loading it. It does provide for a single loading of data so each
 * widget in a ArtifactEditor doesn't need to make it's own loading and conversions.
 *
 * @author Donald G. Dunne
 */
public class XXArtData {

   public static boolean enableArtCheck = false;
   public static XXArtData SENTINEL = new XXArtData(null);
   private static Map<Long, XXArtData> idToXXArt = new HashMap<>(500);

   protected Artifact artifact;
   protected TransactionId txId = TransactionId.SENTINEL;

   public static void put(XXArtData xxArt) {
      idToXXArt.put(xxArt.getId(), xxArt);
   }

   public static XXArtData get(Artifact art) {
      XXArtData xxArt = idToXXArt.get(art.getId());
      if (xxArt == null) {
         XWidget.debugXWidgets.syserr("Create new xxArt: [%s] \n", art.toStringWithId());
         xxArt = new XXArtData(art);
         put(xxArt);
      } else {
         xxArt.updateArtifact(art);
         XWidget.debugXWidgets.syserr("Cached xxArt: [%s] \n", art.toStringWithId());
      }
      return xxArt;
   }

   public XXArtData(Artifact artifact) {
      this.artifact = artifact;
   }

   public boolean isArtifact() {
      return artifact != null;
   }

   // Update artifact if changed
   protected void updateArtifact(Artifact art) {
      if (txId.isInvalid()) {
         txId = art.getTransaction();
      } else if (!art.getTransaction().equals(txId)) {
         txId = art.getTransaction();
         artifact = art;
         clear();
      }
   }

   protected void clear() {
      // for subclass
   }

   public Artifact getArtifact() {
      return artifact;
   }

   private Long getId() {
      return artifact.getId();
   }

}
