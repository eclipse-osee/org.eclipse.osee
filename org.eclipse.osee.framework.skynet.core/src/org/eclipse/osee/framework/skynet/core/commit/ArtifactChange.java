/**
 * 
 */
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactChange extends OseeChange {

   protected ArtifactChange(TxChange txChange, int gammaId, ModificationType modificationType, int itemId) {
      super(txChange, gammaId, modificationType, itemId);
   }

   @Override
   public void accept(IChangeResolver resolver) throws OseeCoreException {
   }
}