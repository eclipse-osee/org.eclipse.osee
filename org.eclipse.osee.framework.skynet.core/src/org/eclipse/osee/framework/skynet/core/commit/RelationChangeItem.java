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
public class RelationChangeItem extends ChangeItem{
   private int bArtId;
   private int relTypeId;
   private String rationale;
   
   public RelationChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, long currentSourceTansactionNumber, int aArtId, int bArtId, int relLinkId, int relTypeId, String rationale) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTansactionNumber);
      
      this.setKind(GammaKind.Relation);
      this.setItemId(relLinkId);
      this.getCurrent().setValue(rationale);
      this.setArt_id(aArtId);
      
      this.bArtId = bArtId;
      this.relTypeId = relTypeId;
      this.rationale = rationale;
   }

   public int getBArtId() {
      return bArtId;
   }

   public int getRelTypeId() {
      return relTypeId;
   }
   
   public String getRationale(){
      return rationale;
   }
}
