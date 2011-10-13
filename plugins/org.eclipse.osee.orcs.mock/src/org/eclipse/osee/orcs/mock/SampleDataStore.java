/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.mock;

import org.junit.runners.model.FrameworkMethod;

public interface SampleDataStore {

   void stopDataStore();

   void startDataStore(FrameworkMethod method, Object target) throws Exception;
}
