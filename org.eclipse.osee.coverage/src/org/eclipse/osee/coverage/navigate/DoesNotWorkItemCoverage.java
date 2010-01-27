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
package org.eclipse.osee.coverage.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.store.CoverageAttributes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.providers.DefaultAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemCoverage extends XNavigateItemAction {

   public DoesNotWorkItemCoverage() {
      super(null, "Does Not Work - Coverage - fixCoverageInformation ", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }

      //      Artifact artifact = ArtifactQuery.getArtifactFromId("AFLY_zvqoHPNSwfetyQA", BranchManager.getBranch(3308));
      //      System.out.println("print got it " + artifact);
      try {
         //         fixCoverageInformation();
         importTestUnitNamesToDbTables();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
   }

   private void importTestUnitNamesToDbTables() throws Exception {
      // BlkII Code Coverage Branch
      Branch branch = BranchManager.getBranchByGuid("QyUb5GYLbDS3AmXKZWgA");
      Set<String> allTestUnitNames = new HashSet<String>();
      int fixCount = 0, binaryMoveCount = 0, totalCoverageUnits = 0, totalCoverageItems = 0;
      XResultData rd = new XResultData();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType("Coverage Unit", branch)) {
         System.out.println("Processing Item " + artifact);
         totalCoverageUnits++;
         for (Attribute<?> attr : artifact.getAttributes(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            totalCoverageItems++;
            String xml = (String) attr.getValue();
            CoverageItem coverageItem = new CoverageItem(null, xml, CoverageOptionManagerDefault.instance());
            allTestUnitNames.addAll(coverageItem.getTestUnits());
            Collection<String> testUnitNames = coverageItem.getTestUnits();
            if (coverageItem.getTestUnits().size() > 0) {
               fixCount++;
               coverageItem.setTestUnits(new ArrayList<String>());
               String newXml = coverageItem.toXml();
               //               TestUnitStore.instance().setTestUnits(coverageItem, testUnitNames);
               int additionalSize = 20 + (7 * testUnitNames.size());
               rd.log("Num Test Units " + testUnitNames.size() + " Pre-size " + xml.length() + " Post-size " + newXml.length() + " Post-size w/ name_id " + (newXml.length() + additionalSize));
               if (newXml.length() > DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH) {
                  rd.logError("Still too big " + newXml.length());
               }
               if (xml.length() > DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH && newXml.length() < DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH) {
                  binaryMoveCount++;
               }
            }
         }
      }
      rd.log(Collections.toString(allTestUnitNames, "\n"));
      rd.log("Num Coverage Units " + totalCoverageUnits + " Num Coverage Items " + totalCoverageItems);
      rd.log("Fixed " + fixCount + " Binary Moved " + binaryMoveCount);
      rd.report("Test Unit Import");
   }

   private void fixCoverageInformation() throws Exception {
      // BlkII Code Coverage Branch
      Branch branch = BranchManager.getBranchByGuid("QyUb5GYLbDS3AmXKZWgA");
      // Don Coverage Branch
      PropertyStore store = new PropertyStore();
      //      Branch branch = BranchManager.getBranchByGuid("ANPixlmF+BNVrPJIUvQA");
      SkynetTransaction transaction = new SkynetTransaction(branch, "Add missing Decision Reviews");

      boolean persist = true;
      Set<Artifact> coverageUnitsFixed = new HashSet<Artifact>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType("Coverage Unit", branch)) {
         System.out.println("Processing Item " + artifact);
         for (Attribute<?> attr : artifact.getAttributes(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            String str = (String) attr.getValue();
            store.load(str);
            CoverageItem item = new CoverageItem(null, str, CoverageOptionManagerDefault.instance());

            String executeNum = store.get("executeNum");
            if (!Strings.isValid(executeNum)) {
               System.err.println("Invalid executeNum from artifact " + artifact + " item " + str);
               continue;
            }
            item.setOrderNumber(executeNum);
            System.out.println(String.format("Setting order to %s", executeNum));

            String text = store.get("text");
            if (!Strings.isValid(text)) {
               System.err.println("Invalid text from artifact " + artifact + " item " + str);
               continue;
            }
            item.setName(text);
            System.out.println(String.format("Setting text to %s", text));

            attr.setFromString(item.toXml());

            // Update this artifact's method Num, if not already set
            if (!coverageUnitsFixed.contains(artifact)) {
               // Update this artifact's method Num, if not already set
               String methodNum = store.get("methodNum");
               if (!Strings.isValid(methodNum)) {
                  System.err.println("Invalid method from artifact " + artifact + " item " + str);
                  continue;
               }
               artifact.addAttribute(CoverageAttributes.ORDER.getStoreName(), methodNum);
               coverageUnitsFixed.add(artifact);
               System.out.println(String.format("Adding method num [%s] to unit [%s]", methodNum, artifact));
            }
         }
         if (persist) artifact.persist(transaction);
      }

      transaction.execute();
   }
}
