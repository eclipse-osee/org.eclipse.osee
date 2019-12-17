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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * Rollup of ChangeItems for given artifact in change report
 *
 * @author Donald G. Dunne
 */
public class ChangeReportRollup {

   private ArtifactId artId;
   private ArtifactTypeToken artType;
   private List<ChangeItem> changeItems = new ArrayList<ChangeItem>();

   public ChangeReportRollup(ArtifactId art) {
      this.artId = art;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   public void setArtId(ArtifactId artId) {
      this.artId = artId;
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public void setArtType(ArtifactTypeToken artType) {
      this.artType = artType;
   }

   public List<ChangeItem> getChangeItems() {
      return changeItems;
   }

   public void setChangeItems(List<ChangeItem> changeItems) {
      this.changeItems = changeItems;
   }

   @Override
   public String toString() {
      return "CRR [artId=" + artId + ", " + "items=" + changeItems.size() + " artType=" + artType + "]";
   }

}
