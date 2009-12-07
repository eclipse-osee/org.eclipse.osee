/*
 * Created on Nov 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public class MessageMergeItem extends MergeItem {

   private final String message;

   public MessageMergeItem(String message) {
      super(MergeType.Error__Message, null, null, false);
      this.message = message;
   }

   @Override
   public String getName() {
      return message;
   }

   @Override
   public Collection<? extends ICoverage> getChildren() {
      return Collections.emptyList();
   }

   @Override
   public Collection<? extends ICoverage> getChildren(boolean recurse) {
      return Collections.emptyList();
   }

}
