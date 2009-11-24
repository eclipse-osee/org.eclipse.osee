/*
 * Created on Nov 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.services.ITranslatorId;

/**
 * @author b1122182
 */
public enum CoreTranslationIds implements ITranslatorId {
   //   BranchCommitRequest.class);   
   //   BranchCommitResponse.class); 
   BLAH;

   @Override
   public String getKey() {
      return this.name();
   }

}
