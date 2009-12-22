/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageStore {

   public abstract void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException;

   public abstract Result save() throws OseeCoreException;

   public void delete(boolean purge) throws OseeCoreException;

}
