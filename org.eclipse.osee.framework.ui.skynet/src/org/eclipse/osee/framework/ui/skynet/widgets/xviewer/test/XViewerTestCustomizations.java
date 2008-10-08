/*
 * Created on Jul 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.MatchFilter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations;

/**
 * For testing purposes only, just save customizations as files at C:/UserData
 * 
 * @author Donald G. Dunne
 */
public class XViewerTestCustomizations implements IXViewerCustomizations {

   public XViewerTestCustomizations() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#deleteCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   @Override
   public void deleteCustomization(CustomizeData custData) throws Exception {
      File file = new File(getFilename(custData));
      if (file.exists()) file.delete();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getSavedCustDatas()
    */
   @Override
   public List<CustomizeData> getSavedCustDatas() {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      for (String filename : Lib.readListFromDir(new File("C:/UserData/"), new MatchFilter("CustData_.*\\.xml"), true)) {
         custDatas.add(new CustomizeData(AFile.readFile("C:/UserData/" + filename)));
      }
      return custDatas;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#getUserDefaultCustData()
    */
   @Override
   public CustomizeData getUserDefaultCustData() {
      File file = new File(getDefaultFilename());
      if (!file.exists()) return null;
      String defaultGuid = AFile.readFile(file).replaceAll("\\s", "");
      if (defaultGuid != null) {
         for (CustomizeData custData : getSavedCustDatas()) {
            if (custData.getGuid().equals(defaultGuid)) {
               return custData;
            }
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#isCustomizationPersistAvailable()
    */
   @Override
   public boolean isCustomizationPersistAvailable() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#isCustomizationUserDefault(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) {
      File file = new File(getDefaultFilename());
      if (!file.exists()) return false;
      String defaultGuid = AFile.readFile(getDefaultFilename()).replaceAll("\\s", "");
      return custData.getGuid().equals(defaultGuid);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#saveCustomization(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData)
    */
   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      AFile.writeFile(getFilename(custData), custData.getXml(true));
      Thread.sleep(2000);
   }

   private String getFilename(CustomizeData custData) {
      return "C:/UserData/CustData_" + custData.getGuid() + ".xml";
   }

   private String getDefaultFilename() {
      return "C:/UserData/CustDataUserDefault.txt";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.IXViewerCustomizations#setUserDefaultCustData(org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData, boolean)
    */
   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
      if (set)
         AFile.writeFile(getDefaultFilename(), newCustData.getGuid());
      else {
         File file = new File(getDefaultFilename());
         if (file.exists()) file.delete();
      }
   }

}
