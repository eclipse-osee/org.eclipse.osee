/*
 * Created on Sep 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Jeff C. Phillips
 *
 */
public class ArtifactChangeItem extends ChangeItem {

   public ArtifactChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, long currentSourceTansactionNumber, int artId) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTansactionNumber);
      
      this.setKind(GammaKind.Artifact);
      this.setItemId(artId);
      this.setArt_id(artId);
   }
}
