/*
 * Created on Jun 14, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

public interface Version extends HasLocalId {

   long getGammaId();

   void setGammaId(long gamma);

   ModificationType getModificationType();

   void setModificationType(ModificationType modType);

   void setLocalId(int localId);

   int getTransactionId();

   void setTransactionId(int txId);

   boolean isInStorage();

   boolean isHistorical();

   void setHistorical(boolean historical);

}
