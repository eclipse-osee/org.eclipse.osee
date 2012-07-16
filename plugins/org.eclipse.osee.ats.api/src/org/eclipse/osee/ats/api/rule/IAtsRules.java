/*
 * Created on May 31, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.rule;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsRules {

   public Collection<String> getRules() throws OseeCoreException;

   public void addRule(String rule) throws OseeCoreException;

   public boolean hasRule(String rule) throws OseeCoreException;

   public void removeRule(String rule) throws OseeCoreException;

}
