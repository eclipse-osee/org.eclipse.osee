/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.integration.util;

import static org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil.SAW_Bld_1;
import static org.eclipse.osee.disposition.rest.integration.util.DispositionTestUtil.SAW_Bld_1_FOR_DISPO;
import static org.eclipse.osee.disposition.rest.util.DispoUtil.discrepancyToJsonObj;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.DispoApi;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class DispositionInitializer {

   private final OrcsApi orcsApi;
   private final DispoApi dispoApi;

   public DispositionInitializer(OrcsApi orcsApi, DispoApi dispoApi) {
      this.orcsApi = orcsApi;
      this.dispoApi = dispoApi;
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable getDispositionUser() throws OseeCoreException {
      return getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }

   private ApplicationContext getContext() {
      return null;
   }

   private QueryFactory getQueryFactory() {
      return orcsApi.getQueryFactory(getContext());
   }

   public void initialize() throws Exception {

      orcsApi.getBranchOps(getContext()).createWorkingBranch(SAW_Bld_1_FOR_DISPO, getDispositionUser(), SAW_Bld_1, null).call();

      // create Dispo Config Art
      ArtifactReadable oseeSystem = findUser();
      TransactionBuilder tx =
         orcsApi.getTransactionFactory(null).createTransaction(CoreBranches.COMMON, oseeSystem, "Create Dispo Config");
      ArtifactId createArtifact = tx.createArtifact(CoreArtifactTypes.GeneralData, DispoStrings.Dispo_Config_Art);
      StringBuffer sb = new StringBuffer(String.valueOf(SAW_Bld_1.getUuid()));
      sb.append(":");
      sb.append(SAW_Bld_1_FOR_DISPO.getGuid());
      sb.append("\n");
      sb.append(SAW_Bld_1.getUuid());
      sb.append(":");
      sb.append(SAW_Bld_1_FOR_DISPO.getUuid());
      tx.createAttributeFromString(createArtifact, CoreAttributeTypes.GeneralStringData, sb.toString());
      tx.commit();

      // Creat Set and Item Arts
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName("DEMO SET");
      descriptor.setImportPath("c:");
      DispoProgram program = dispoApi.getDispoFactory().createProgram(SAW_Bld_1_FOR_DISPO);
      Identifiable<String> devSet = dispoApi.createDispoSet(program, descriptor);

      DispoItemData dispoItem = new DispoItemData();
      dispoItem.setName("Item One");
      Identifiable<String> itemOne = dispoApi.createDispoItem(program, devSet.getGuid(), dispoItem);

      createDiscrepancies(program, itemOne.getGuid());
   }

   private void createDiscrepancies(DispoProgram program, String itemId) {
      Map<String, JSONObject> discrepanciesToInit = new HashMap<String, JSONObject>();
      Discrepancy one = new Discrepancy();
      one.setId("idafd");
      one.setText("one");
      one.setLocation(1);
      discrepanciesToInit.put(one.getId(), discrepancyToJsonObj(one));

      Discrepancy two = new Discrepancy();
      two.setId("iddf");
      two.setText("two");
      two.setLocation(12);
      discrepanciesToInit.put(two.getId(), discrepancyToJsonObj(two));

      Discrepancy three = new Discrepancy();
      three.setId("absc");
      three.setText("three");
      three.setLocation(23);
      discrepanciesToInit.put(three.getId(), discrepancyToJsonObj(three));

      Discrepancy four = new Discrepancy();
      four.setId("cddg");
      four.setText("four");
      four.setLocation(25);
      discrepanciesToInit.put(four.getId(), discrepancyToJsonObj(four));

      Discrepancy five = new Discrepancy();
      five.setId("yoj");
      five.setText("five");
      five.setLocation(90);
      discrepanciesToInit.put(five.getId(), discrepancyToJsonObj(five));

      JSONObject discrepanciesList = new JSONObject(discrepanciesToInit);
      DispoItemData item = new DispoItemData();
      item.setDiscrepanciesList(discrepanciesList);
      dispoApi.editDispoItem(program, itemId, item);
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable findUser() {
      return orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }
}
