/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public interface AtsXWidgetValidatorProvider {

   public Collection<IAtsXWidgetValidator> getValidators();
}
