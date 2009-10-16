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
package org.eclipse.osee.coverage.test.import1;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverageUnitProvider;
import org.eclipse.osee.coverage.test.SampleJavaFileParser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class CoverageImportTest1NavigateItem extends XNavigateItemAction implements ICoverageImporter {

   public static String PATH = "../../../../../../../src/org/eclipse/osee/coverage/test/import1/";

   public CoverageImportTest1NavigateItem() {
      this(null);
   }

   public CoverageImportTest1NavigateItem(XNavigateItem parent) {
      super(parent, "Open Coverage Import 1");
   }

   @Override
   public CoverageImport run() {

      CoverageImport coverageImport = new CoverageImport(getName());
      try {
         for (String filename : Arrays.asList(
         //
               "com/screenA/ComScrnAButton1.java", "com/screenA/ComScrnAButton2.java",
               //
               "com/screenB/ScreenBButton1.java", "com/screenB/ScreenBButton2.java", "com/screenB/ScreenBButton3.java",
               //
               "epu/PowerUnit1.java", "epu/PowerUnit1.java",
               //
               "nav/NavigationButton1.java", "nav/NavigationButton2.java", "nav/NavigationButton3.java"
         //
         )) {
            System.err.println(String.format("Importing [%s]", PATH + filename));
            URL url = CoverageImportTest1NavigateItem.class.getResource(PATH + filename);
            CoverageUnit coverageUnit = SampleJavaFileParser.createCodeUnit(url);
            String namespace = coverageUnit.getNamespace().replaceFirst("org.eclipse.osee.coverage.test.import1.", "");
            coverageUnit.setNamespace(namespace);
            CoverageUnit parentCoverageUnit = getOrCreateParent(coverageImport, namespace);
            if (parentCoverageUnit != null) {
               parentCoverageUnit.addCoverageUnit(coverageUnit);
            } else {
               coverageImport.addCoverageUnit(coverageUnit);
            }
         }
         coverageImport.setLocation(PATH);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return coverageImport;
   }

   private CoverageUnit getOrCreateParent(CoverageImport coverageImport, String namespace) {
      // Look for already existing CU
      for (ICoverageEditorItem item : new CopyOnWriteArrayList<ICoverageEditorItem>(
            coverageImport.getCoverageEditorItems(true))) {
         if (!(item instanceof CoverageUnit)) continue;
         CoverageUnit coverageUnit = (CoverageUnit) item;
         if (coverageUnit.getName().equals(namespace)) {
            return coverageUnit;
         }
      }
      // Create 
      String[] names = namespace.split("\\.");
      String nameStr = "";
      for (String name : names) {
         if (nameStr.equals("")) {
            nameStr = name;
         } else {
            nameStr = nameStr + "." + name;
         }
         if (coverageImport.getCoverageUnits().size() == 0) {
            CoverageUnit newCoverageUnit = new CoverageUnit(coverageImport, nameStr, "");
            newCoverageUnit.setNamespace(nameStr);
            coverageImport.addCoverageUnit(newCoverageUnit);
            if (nameStr.equals(namespace)) return newCoverageUnit;
            continue;
         }

         // Look for already existing CU
         boolean found = false;
         for (ICoverageEditorItem item : new CopyOnWriteArrayList<ICoverageEditorItem>(
               coverageImport.getCoverageEditorItems(true))) {
            if (!(item instanceof CoverageUnit)) continue;
            if (item.getName().equals(name)) {
               found = true;
               break;
            }
         }
         if (found) continue;

         // Create one if not exists

         // Find parent
         ICoverageEditorItem parent = null;
         if (nameStr.equals(name)) {
            parent = coverageImport;
         } else {
            parent = getOrCreateParent(coverageImport, nameStr.replaceFirst("\\." + name + ".*$", ""));
         }
         // Create new coverage unit
         CoverageUnit newCoverageUnit = new CoverageUnit(parent, nameStr, "");
         newCoverageUnit.setNamespace(nameStr);
         // Add to parent
         ((ICoverageUnitProvider) parent).addCoverageUnit(newCoverageUnit);
         // Return if this is our coverage unit
         if (nameStr.equals(namespace)) return newCoverageUnit;
      }
      return null;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoverageManager.importCoverage(new CoverageImportTest1NavigateItem());
   }

}
