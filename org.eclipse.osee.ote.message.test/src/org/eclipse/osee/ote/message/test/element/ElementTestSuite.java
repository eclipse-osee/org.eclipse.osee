/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.test.element;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { //
Float32ElementTest.class,//
      Float64ElementTest.class,//
      IntegerElementTest.class,//
      LongIntegerElementTest.class,//
      SignedInteger16ElementTest.class,//
      StringElementTest.class,//
})
public class ElementTestSuite {

}
