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
package org.eclipse.osee.framework.core.test.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.data.TransactionRecord;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BasicArtifactDataTranslator;
import org.eclipse.osee.framework.core.exchange.BranchCommitDataTranslator;
import org.eclipse.osee.framework.core.exchange.BranchTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.core.exchange.TransactionRecordTranslator;
import org.eclipse.osee.framework.core.test.util.BranchTestUtil;
import org.eclipse.osee.framework.core.test.util.UserArtifactTestUtil;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitDataTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchCommitDataTranslatorTest extends BaseTranslatorTest<BranchCommitData> {

   public BranchCommitDataTranslatorTest(BranchCommitData data, IDataTranslator<BranchCommitData> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCommitData expected, BranchCommitData actual) throws OseeCoreException {
      DataUtility.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(Branch.class, new BranchTranslator(service));
      service.addTranslator(IBasicArtifact.class, new BasicArtifactDataTranslator());
      service.addTranslator(TransactionRecord.class, new TransactionRecordTranslator(service));
      IDataTranslator<BranchCommitData> translator = new BranchCommitDataTranslator(service);
      data.add(new Object[] {new BranchCommitData(getUserArtifact(), getSourceBranch(), getDestinationBranch(), false),
            translator});
      return data;
   }

   private static BranchTestUtil getSourceBranch() {
      return new BranchTestUtil("gjdkfghfr183848754", "Source Branch", BranchState.MODIFIED, BranchType.WORKING,
            BranchArchivedState.UNARCHIVED, false);
   }

   private static BranchTestUtil getDestinationBranch() {
      return new BranchTestUtil("gjdkfghfr185448754", "Destination Branch", BranchState.CREATED, BranchType.BASELINE,
            BranchArchivedState.UNARCHIVED, false);
   }

   private static UserArtifactTestUtil getUserArtifact() {
      return new UserArtifactTestUtil("Tom Jones", "EJDFKGJDFKGJ19394FDJDLF", 999999);
   }

}
