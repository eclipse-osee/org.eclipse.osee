/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IValueProvider {

   public String getName();

   public boolean isEmpty() throws OseeCoreException;

   public Collection<String> getValues() throws OseeCoreException;

   public Collection<Date> getDateValues() throws OseeCoreException;

}
