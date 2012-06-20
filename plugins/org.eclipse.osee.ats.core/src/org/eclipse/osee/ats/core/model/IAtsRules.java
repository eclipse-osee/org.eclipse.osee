/*
 * Created on May 31, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.Collection;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IAtsRules {

   public Collection<RuleDefinition> getRules() throws OseeCoreException;

   public RuleDefinition addRule(RuleDefinitionOption option) throws OseeCoreException;

   public RuleDefinition addRule(String ruleId) throws OseeCoreException;

   public boolean hasRule(RuleDefinitionOption option) throws OseeCoreException;

   public boolean hasRule(String ruleId) throws OseeCoreException;

}
