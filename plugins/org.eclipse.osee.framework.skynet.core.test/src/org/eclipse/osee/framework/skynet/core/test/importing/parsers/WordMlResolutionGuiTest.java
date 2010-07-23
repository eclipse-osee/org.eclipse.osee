/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.importing.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.Assert;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate.ContentType;
import org.junit.After;
import org.junit.Test;

/**
 * @formatter:off
 * Tests by inputing the whole requirements sample ICD file.
 * Tests:
 * 	- if user answers store as content vs store as an outline number, 
 *      depending on what user selects.
 *      
 *      THESE TESTS MUST RUN AS PART OF THE SUITE, RoughArtifact* classes need
 *      the osee caching service.
 * 
 * @link WordOutlineExtractorDelegate 
 * 
 * @author Karol M. Wilk
 */
//@formatter:on
public class WordMlResolutionGuiTest {

	private static final Pattern PARAGRAPH_REGEX =
				Pattern.compile("<w:p[ >].*?</w:p>", Pattern.DOTALL);

	private static final String FILE_INPUT =
				"outlineResolutionCausingInput_no_toc_formatted.xml";

	private WordOutlineExtractorDelegate delegate = null;

	public WordMlResolutionGuiTest() {
		delegate = new WordOutlineExtractorDelegate(
					new MockResolutionGui());
	}

	@Test
	public void uiResolutionTests() throws Exception {
		//init some ds in delegate
		delegate.initialize();

		//TODO: not finished, needs to be able to compare resulting rough artifact to data
		RoughArtifact testRoughArtifactParent = new RoughArtifact(RoughArtifactKind.CONTAINER);
		RoughArtifactCollector testCollector = new RoughArtifactCollector(testRoughArtifactParent);

		MockResolutionGui resolvingGui =
					(MockResolutionGui)
						delegate.getOutlineResolvingUi();
		//as if the user selected content
		//if asked answer No, treat questions as content
		resolvingGui.setMockUserAnswer(ContentType.CONTENT);

		//pre load with some real data
		String rawData = getResourceData(FILE_INPUT);
		Matcher matcher = PARAGRAPH_REGEX.matcher(rawData);
		boolean foundSomething = false;
		matcher.find();//skip 1.0
		matcher.find();//skip 1.1
		//read the 3.1
		if (matcher.find()) {
			foundSomething = true;
			String singleWp = matcher.group();
			//TODO: pass in the real testCollector
			delegate.processContent(null, false, false, null,
						null, null, singleWp, false);
		}

		//TODO:
		//because this case puts stuff into content
		//there should be a node containing 'Meters and 5.4 Meters'
		//i.e. testRoughArtifactParent.getContent().equals( "Meters and 5.4 Meters" );

		Assert.assertTrue(foundSomething);
	}

	@After
	public void testCleanup() {
		delegate.dispose();
		Assert.assertNull(delegate.getLastHeaderNumber());
		Assert.assertNull(delegate.getLastHeaderName());
		Assert.assertNull(delegate.getLastContent());
	}

	private static String getResourceData(String name)
				throws IOException {

		InputStream inputStream = null;
		try {
			inputStream =
						WordMlResolutionGuiTest.class.getResourceAsStream(name);
			String data = Lib.inputStreamToString(inputStream);
			Assert.assertTrue(Strings.isValid(data));
			return data;
		} finally {
			Lib.close(inputStream);
		}
	}
}
