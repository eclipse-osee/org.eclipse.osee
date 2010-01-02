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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemCoverage extends XNavigateItemAction {

   public DoesNotWorkItemCoverage() {
      super(null, "Does Not Work - Coverage - Load ", FrameworkImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
         return;
      }

      Artifact artifact = ArtifactQuery.getArtifactFromId("AFLY_zvqoHPNSwfetyQA", BranchManager.getBranch(3308));
      System.out.println("print got it " + artifact);
      try {
         //         fixCoverageInformation();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      AWorkbench.popup("Completed", "Complete");
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
