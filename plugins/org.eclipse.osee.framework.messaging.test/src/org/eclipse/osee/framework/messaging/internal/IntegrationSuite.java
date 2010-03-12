/*
 * Created on Oct 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.services.internal.TestMessageServices;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestSendingAndRecieving.class,
  TestBrokerServiceInterruptions.class,
  TestMessageServices.class
})
public class IntegrationSuite {
}

