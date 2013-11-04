/*
 * Created on Nov 4, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IWorkDefinitionMatch {

   public abstract String getWorkDefinitionId();

   public abstract void setWorkDefinitionId(String workDefinitionId);

   public abstract IAtsWorkDefinition getWorkDefinition();

   public abstract void setWorkDefinition(IAtsWorkDefinition WorkDefinition);

   public abstract void addTrace(String traceStr);

   public abstract List<String> getTrace();

   public abstract boolean isMatched();

}