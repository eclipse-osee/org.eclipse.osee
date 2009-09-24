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
public class AttributeChangeItem extends ChangeItem{

   /**
    * @param currentSourceGammaId
    * @param currentSourceModType
    * @param currentSourceTansactionNumber
    * @param attrId 
    * @param artId 
    * @param value 
    * @param hasDestinationBranch TODO
    */
   public AttributeChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, long currentSourceTansactionNumber, int attrId, int artId, String value, boolean hasDestinationBranch) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTansactionNumber, hasDestinationBranch);
      
      this.setKind(GammaKind.Attribute);
      this.setItemId(attrId);
      this.setArt_id(artId);
      this.getCurrent().setValue(value);
   }

}
