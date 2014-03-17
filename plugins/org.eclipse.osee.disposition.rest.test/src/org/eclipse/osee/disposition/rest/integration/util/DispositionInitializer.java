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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoProgram;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.model.LocationRange;
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
import org.json.JSONArray;
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
      StringBuffer sb = new StringBuffer(SAW_Bld_1.getGuid());
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
      List<JSONObject> discrepanciesToInit = new ArrayList<JSONObject>();
      Discrepancy one = new Discrepancy();
      one.setId(0);
      one.setIdsOfCoveringAnnotations(new JSONArray());
      one.setText("one");
      one.setLocationRange(new LocationRange(1, 10));
      discrepanciesToInit.add(discrepancyToJsonObj(one));

      Discrepancy two = new Discrepancy();
      two.setId(1);
      two.setIdsOfCoveringAnnotations(new JSONArray());
      two.setText("two");
      two.setLocationRange(new LocationRange(12, 20));
      discrepanciesToInit.add(discrepancyToJsonObj(two));

      Discrepancy three = new Discrepancy();
      three.setId(2);
      three.setIdsOfCoveringAnnotations(new JSONArray());
      three.setText("three");
      three.setLocationRange(new LocationRange(23));
      discrepanciesToInit.add(discrepancyToJsonObj(three));

      Discrepancy four = new Discrepancy();
      four.setId(3);
      four.setIdsOfCoveringAnnotations(new JSONArray());
      four.setText("four");
      four.setLocationRange(new LocationRange(25));
      discrepanciesToInit.add(discrepancyToJsonObj(four));

      Discrepancy five = new Discrepancy();
      five.setId(4);
      five.setIdsOfCoveringAnnotations(new JSONArray());
      five.setText("five");
      five.setLocationRange(new LocationRange(32, 90));
      discrepanciesToInit.add(discrepancyToJsonObj(five));

      JSONArray array = new JSONArray(discrepanciesToInit);
      DispoItemData item = new DispoItemData();
      item.setDiscrepanciesList(array);
      dispoApi.editDispoItem(program, itemId, item);
   }

   @SuppressWarnings("unchecked")
   private ArtifactReadable findUser() {
      return orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(SystemUser.OseeSystem).getResults().getExactlyOne();
   }
}
