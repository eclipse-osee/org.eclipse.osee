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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.SimpleTestUnitProvider;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.CoverageAttributes;
import org.eclipse.osee.coverage.store.DbTestUnitProvider;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
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
      super(null, "Does Not Work - Coverage - Zip VCast", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }

      //      Artifact artifact = ArtifactQuery.getArtifactFromId("AFLY_zvqoHPNSwfetyQA", BranchManager.getBranch(3308));
      //      System.out.println("print got it " + artifact);
      try {
         zipVcastFiles();
         //         fixCoverageItemNames();
         //         fixCoverageInformation();
         //         importTestUnitNamesToDbTables();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
   }

   private void zipVcastFiles() throws IOException {
      String base = "I:\\links\\lba_mp\\us\\code\\v01_\\msm.ss\\user\\jmckinne\\msm_FTB4\\msm.ftb4_2.wrk\\";
      String vcastBase = base + "vcast\\";
      String resultsBase = base + "vcast\\results\\";
      List<File> files = new ArrayList<File>();
      files.add(new File(base + "vcast.vcp"));
      files.add(new File(base + "CCAST_.CFG"));
      files.add(new File(vcastBase + "build_info.xml"));
      files.add(new File(resultsBase + "lba.test.script.qual.msm.asm.MSM_ASM_ac_bus_tie_indication_MSM_1_01052010.DAT"));

      Lib.compressFiles(base, files, "C://UserData//try.zip");
   }

   @SuppressWarnings("unused")
   private void importTestUnitNamesToDbTables() throws Exception {
      // BlkII Code Coverage Branch
      Branch branch = BranchManager.getBranchByGuid("QyUb5GYLbDS3AmXKZWgA");
      Set<String> allTestUnitNames = new HashSet<String>();
      int fixCount = 0, binaryMoveCount = 0, totalCoverageUnits = 0, totalCoverageItems = 0;
      XResultData rd = new XResultData();
      SkynetTransaction transaction = new SkynetTransaction(branch, "Coverage Item to name_id");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(CoverageArtifactTypes.CoverageUnit, branch)) {
         System.out.println("Processing Item " + artifact);
         totalCoverageUnits++;
         for (Attribute<?> attr : artifact.getAttributes(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            totalCoverageItems++;
            String xml = (String) attr.getValue();
            CoverageItem coverageItem =
                  new CoverageItem(null, xml, CoverageOptionManagerDefault.instance(), new SimpleTestUnitProvider());
            allTestUnitNames.addAll(coverageItem.getTestUnits());
            if (coverageItem.getTestUnits().size() > 0) {
               fixCount++;
               Collection<String> testUnitNames = coverageItem.getTestUnits();
               coverageItem.setTestUnitProvider(DbTestUnitProvider.instance());
               coverageItem.setTestUnits(testUnitNames);
               String newXml = coverageItem.toXml();
               ((StringAttribute) attr).setValue(newXml);
               rd.log("Num Test Units " + testUnitNames.size() + " Pre-size " + xml.length() + " Post-size " + newXml.length());
               if (newXml.length() > DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH) {
                  rd.logError("Still too big " + newXml.length());
               }
               if (xml.length() > DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH && newXml.length() < DefaultAttributeDataProvider.MAX_VARCHAR_LENGTH) {
                  binaryMoveCount++;
               }
               artifact.persist(transaction);
            }
         }
      }
      transaction.execute();
      rd.log(Collections.toString(allTestUnitNames, "\n"));
      rd.log("Num Coverage Units " + totalCoverageUnits + " Num Coverage Items " + totalCoverageItems);
      rd.log("Fixed " + fixCount + " Binary Moved " + binaryMoveCount);
      rd.report("Test Unit Import");
   }

   @SuppressWarnings("unused")
   private void fixCoverageItemNames() throws Exception {
      Pattern linePattern = Pattern.compile("^[0-9]+ [0-9]+(.*?)$");
      // BlkII Code Coverage Branch
      Branch branch = BranchManager.getBranchByGuid("QyUb5GYLbDS3AmXKZWgA");
      Artifact msmCoveragePackageArt = ArtifactQuery.getArtifactFromId("AA6+z8QPbToPZSn9tAgA", branch);
      OseeCoveragePackageStore packageStore = new OseeCoveragePackageStore(msmCoveragePackageArt);
      // Don Coverage Branch
      PropertyStore store = new PropertyStore();
      //      Branch branch = BranchManager.getBranchByGuid("ANPixlmF+BNVrPJIUvQA");
      SkynetTransaction transaction = new SkynetTransaction(branch, "Fix coverage item names");

      boolean persist = true;
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(CoverageArtifactTypes.CoverageUnit, branch)) {
         System.out.println("Processing Item " + artifact);
         for (Attribute<?> attr : artifact.getAttributes(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            String attrStr = (String) attr.getValue();
            store.load(attrStr);
            CoverageItem item =
                  new CoverageItem(null, attrStr, packageStore.getCoverageOptionManager(), new SimpleTestUnitProvider());
            String name = store.get("name");
            if (!Strings.isValid(name)) {
               System.err.println(String.format("Invalid name [%s] for item [%s]", name, item));
               continue;
            }
            System.out.println(String.format("Old [%s]", name));

            Matcher m = linePattern.matcher(name);
            if (m.find()) {
               item.setName(m.group(1));
               System.out.println(String.format("New [%s]", item.getName()));
               attr.setFromString(item.toXml());
            } else {
               System.err.println(String.format("Error: name [%s] doesn't match", name));
            }
         }
         if (persist) artifact.persist(transaction);
      }

      transaction.execute();
   }

   @SuppressWarnings("unused")
   private void fixCoverageInformation() throws Exception {
      // BlkII Code Coverage Branch
      Branch branch = BranchManager.getBranchByGuid("QyUb5GYLbDS3AmXKZWgA");
      // Don Coverage Branch
      PropertyStore store = new PropertyStore();
      //      Branch branch = BranchManager.getBranchByGuid("ANPixlmF+BNVrPJIUvQA");
      SkynetTransaction transaction = new SkynetTransaction(branch, "Add missing Decision Reviews");

      boolean persist = true;
      Set<Artifact> coverageUnitsFixed = new HashSet<Artifact>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromType(CoverageArtifactTypes.CoverageUnit, branch)) {
         System.out.println("Processing Item " + artifact);
         for (Attribute<?> attr : artifact.getAttributes(CoverageAttributes.COVERAGE_ITEM.getStoreName())) {
            String str = (String) attr.getValue();
            store.load(str);
            CoverageItem item =
                  new CoverageItem(null, str, CoverageOptionManagerDefault.instance(), new SimpleTestUnitProvider());

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
