/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionStore {

   public abstract String loadWorkDefinitionString(String workDefId);

   public abstract IAttributeResolver getAttributeResolver();

   public abstract IUserResolver getUserResolver();

   public abstract List<Pair<String, String>> getWorkDefinitionStrings() throws OseeCoreException;
}
