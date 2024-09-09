/*
 * Created on Sep 9, 2024
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.conditions.EnableIfCondition;

public class AtsEnabledIfCondition extends EnableIfCondition {

   public AtsEnabledIfCondition(AttributeTypeToken attrType, Object... value) {
      super(attrType, value);
   }

   @Override
   public boolean isEnabled(ArtifactToken artifact) {
      return super.isEnabled(artifact);
   }

}
