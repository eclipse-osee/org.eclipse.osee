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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage.Location;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class CustomizeData {

   private String guid = GUID.generateGuidStr();
   private String name;
   private String nameSpace;
   private boolean personal = false;
   protected SortingData sortingData = new SortingData(this);
   protected FilterData filterData = new FilterData();
   protected ColumnFilterData columnFilterData = new ColumnFilterData();

   /**
    * @return the columnFilterData
    */
   public ColumnFilterData getColumnFilterData() {
      return columnFilterData;
   }

   protected ColumnData columnData = new ColumnData();
   private final Map<String, Image> imageMap = new HashMap<String, Image>();
   private boolean isWorkbench = false;

   public CustomizeData() {
      isWorkbench = Platform.isRunning();
   }

   public boolean isTableDefaultCustData() {
      return name.equals(CustomizeManager.TABLE_DEFAULT_LABEL);
   }

   public boolean isCurrentTableCustData() {
      return name.equals(CustomizeManager.CURRENT_LABEL);
   }

   public CustomizeData(String xml) {
      this();
      setFromXml(xml);
   }

   public void resetGuid() {
      guid = GUID.generateGuidStr();
   }

   public Image getImage(boolean isDefault) {
      if (!isWorkbench) return null;
      if (name.equals(CustomizeManager.TABLE_DEFAULT_LABEL) || name.equals(CustomizeManager.CURRENT_LABEL)) {
         return SkynetGuiPlugin.getInstance().getImage("customize.gif");
      }
      String index = "" + personal + isDefault;
      if (imageMap.containsKey(index)) return imageMap.get(index);
      Image image = SkynetGuiPlugin.getInstance().getImage("customize.gif");
      if (!personal) image =
            new OverlayImage(image, SkynetGuiPlugin.getInstance().getImageDescriptor("customizeG.gif"),
                  Location.BOT_RIGHT).createImage();
      if (isDefault) image =
            new OverlayImage(image, SkynetGuiPlugin.getInstance().getImageDescriptor("customizeD.gif"),
                  Location.TOP_RIGHT).createImage();
      imageMap.put(index, image);
      return image;
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuffer sb =
            new StringBuffer(
                  "<XTreeProperties name=\"" + name + "\" namespace=\"" + nameSpace + "\" guid=\"" + guid + "\">");
      sb.append(sortingData.getXml());
      sb.append(filterData.getXml());
      sb.append(columnFilterData.getXml());
      sb.append(columnData.getXml(visibleColumnsOnly));
      sb.append("</XTreeProperties>");
      return sb.toString();
   }

   public void setFromXml(String xml) {
      Matcher m = Pattern.compile("name=\"(.*?)\".*?namespace=\"(.*?)\".*?guid=\"(.*?)\"").matcher(xml);
      if (m.find()) {
         name = m.group(1);
         nameSpace = m.group(2);
         guid = m.group(3);
      } else {
         name = "Invalid customize format for " + xml.substring(0, 50);
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,  new IllegalStateException(name));
         return;
      }
      sortingData.setFromXml(xml);
      filterData.setFromXml(xml);
      columnData.setFromXml(xml);
      columnFilterData.setFromXml(xml);
   }

   @Override
   public String toString() {
      return "[" + name + "][" + nameSpace + "][" + guid + "][" + columnData + "][" + filterData + "][" + columnFilterData + "][" + sortingData + "]";
   }

   /**
    * @return the personal
    */
   public boolean isPersonal() {
      return personal;
   }

   /**
    * @param personal the personal to set
    */
   public void setPersonal(boolean personal) {
      this.personal = personal;
   }

   /**
    * @return the columnData
    */
   public ColumnData getColumnData() {
      return columnData;
   }

   /**
    * @return the filterData
    */
   public FilterData getFilterData() {
      return filterData;
   }

   /**
    * @return the sortingData
    */
   public SortingData getSortingData() {
      return sortingData;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the nameSpace
    */
   public String getNameSpace() {
      return nameSpace;
   }

   /**
    * @param nameSpace the nameSpace to set
    */
   public void setNameSpace(String nameSpace) {
      this.nameSpace = nameSpace;
   }

   public String getGuid() {
      return guid;
   }

   /**
    * @param guid the guid to set
    */
   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public boolean equals(Object obj) {
      return (obj instanceof CustomizeData) && ((CustomizeData) obj).getGuid().equals(guid);
   }

   @Override
   public int hashCode() {
      return guid.hashCode();
   }
}
