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

package org.eclipse.osee.framework.core.message.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeItem;
import org.eclipse.osee.framework.core.message.ChangeReportResponse;
import org.eclipse.osee.framework.core.message.internal.DataTranslationService;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeItemTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.message.test.mocks.MockRequestFactory;
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
   protected void checkEquals(ChangeReportResponse expected, ChangeReportResponse actual) {
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
      response.addItem(MockRequestFactory.createArtifactChangeItem());
      response.addItem(MockRequestFactory.createArtifactChangeItem());
      response.addItem(MockRequestFactory.createArtifactChangeItem());

      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ChangeReportResponse> translator = new ChangeReportResponseTranslator(dataTranslationService);

      data.add(new Object[] {response, translator});
      return data;
   }
}
