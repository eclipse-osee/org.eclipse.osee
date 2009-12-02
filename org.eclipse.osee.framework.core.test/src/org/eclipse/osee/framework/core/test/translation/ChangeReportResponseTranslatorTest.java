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

package org.eclipse.osee.framework.core.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.ChangeItemTranslator;
import org.eclipse.osee.framework.core.translation.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link ChangeReportResponseTranslator}
 * 
 * @author Jeff C. Phillips
 */
@RunWith(Parameterized.class)
public class ChangeReportResponseTranslatorTest extends BaseTranslatorTest<ChangeReportResponse> {

	public ChangeReportResponseTranslatorTest(ChangeReportResponse data, ITranslator<ChangeReportResponse> translator) {
		super(data, translator);
	}

	@Override
	protected void checkEquals(ChangeReportResponse expected, ChangeReportResponse actual) throws OseeCoreException {
		List<ChangeItem> expectedChangeItems = expected.getChangeItems();
		List<ChangeItem> actualChangeItems = actual.getChangeItems();

		for (int i = 0; i < expected.getChangeItems().size(); i++) {
			DataAsserts.assertEquals(expectedChangeItems.get(i), actualChangeItems.get(i));
		}
	}

	@Parameters
	public static Collection<Object[]> data() throws OseeCoreException {
		DataTranslationService dataTranslationService = new DataTranslationService();
		dataTranslationService.addTranslator(new ChangeItemTranslator(dataTranslationService),
				CoreTranslatorId.CHANGE_ITEM);
		dataTranslationService.addTranslator(new ChangeVersionTranslator(), CoreTranslatorId.CHANGE_VERSION);

		ChangeReportResponse response = new ChangeReportResponse();
		response.addItem(MockDataFactory.createArtifactChangeItem());
		response.addItem(MockDataFactory.createArtifactChangeItem());
		response.addItem(MockDataFactory.createArtifactChangeItem());

		List<Object[]> data = new ArrayList<Object[]>();
		ITranslator<ChangeReportResponse> translator = new ChangeReportResponseTranslator(dataTranslationService);

		data.add(new Object[] { response, translator });
		return data;
	}
}
