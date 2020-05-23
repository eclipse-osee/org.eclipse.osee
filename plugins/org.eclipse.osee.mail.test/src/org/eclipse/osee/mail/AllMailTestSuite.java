/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.mail;

import org.eclipse.osee.mail.internal.MailConfigurationTest;
import org.eclipse.osee.mail.internal.MailMessageTest;
import org.eclipse.osee.mail.internal.MailUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MailConfigurationTest.class, MailMessageTest.class, MailUtilsTest.class})
public class AllMailTestSuite {
   // Test Suite
}