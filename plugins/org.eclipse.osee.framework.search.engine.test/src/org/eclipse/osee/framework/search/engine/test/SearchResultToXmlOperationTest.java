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
package org.eclipse.osee.framework.search.engine.test;

import java.io.StringWriter;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.search.engine.SearchResult;
import org.eclipse.osee.framework.search.engine.SearchResultToXmlOperation;
import org.junit.Test;

/**
 * Test Case for {@link SearchResultToXmlOperation }
 * 
 * @author Roberto E. Escobar
 */
public class SearchResultToXmlOperationTest {

   @Test(expected = OseeArgumentException.class)
   public void testArgumentException1() throws OseeCoreException {
      IOperation operation = new SearchResultToXmlOperation(null, new StringWriter());
      Operations.executeWorkAndCheckStatus(operation);
   }

   @Test(expected = OseeArgumentException.class)
   public void testArgumentException2() throws OseeCoreException {
      IOperation operation = new SearchResultToXmlOperation(new SearchResult(""), null);
      Operations.executeWorkAndCheckStatus(operation);
   }

   public void testSearchResult() {

   }
}
