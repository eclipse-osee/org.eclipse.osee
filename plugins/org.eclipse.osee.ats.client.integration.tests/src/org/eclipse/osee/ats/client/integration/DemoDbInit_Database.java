/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration;

import org.eclipse.osee.ats.client.integration.tests.DemoDbPopulateSuite;
import org.eclipse.osee.ats.client.integration.tests.util.DbInitTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Db Init database and populate with demo data. No extra tests run.
 * 
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DbInitTest.class, DemoDbPopulateSuite.class})
public class DemoDbInit_Database {
   // Test Suite
}
