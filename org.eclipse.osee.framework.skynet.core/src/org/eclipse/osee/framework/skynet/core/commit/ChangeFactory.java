package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;

/**
 * @author Ryan D. Brooks
 */
public class ChangeFactory implements IChangeFactory {

   @Override
   public OseeChange createArtifactChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId) {
      return new ArtifactChange(txChange, gammaId, modificationType, itemId);
   }

   @Override
   public OseeChange createAttributeChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId) {
      return new AttributeChange(txChange, gammaId, modificationType, itemId);
   }

   @Override
   public OseeChange createRelationChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId) {
      return new RelationChange(txChange, gammaId, modificationType, itemId);
   }

}
