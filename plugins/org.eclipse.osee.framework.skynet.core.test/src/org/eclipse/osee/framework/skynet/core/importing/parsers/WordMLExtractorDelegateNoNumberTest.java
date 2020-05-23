/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.net.URL;
import org.eclipse.osee.framework.core.operation.StringOperationLogger;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.junit.Test;

/**
 * Tests to make sure no exception is thrown when running into a numbering style with no contents <wx:t wx:val=""/>
 *
 * @link WordOutlineExtractor
 * @author David W. Miller
 */
public final class WordMLExtractorDelegateNoNumberTest {

   private static final String NUMBERING_PROBLEM_FILE = "testNumbering.xml";
   private final WordOutlineExtractor extractor = new WordOutlineExtractor();

   @Test
   public void testBadNumbering() throws Exception {
      StringOperationLogger logger = new StringOperationLogger();
      URL url = getClass().getResource(NUMBERING_PROBLEM_FILE);
      RoughArtifact parent = new RoughArtifact(RoughArtifactKind.PRIMARY);
      RoughArtifactCollector collector = new RoughArtifactCollector(parent);
      extractor.extractFromSource(logger, url.toURI(), collector);
   }
}