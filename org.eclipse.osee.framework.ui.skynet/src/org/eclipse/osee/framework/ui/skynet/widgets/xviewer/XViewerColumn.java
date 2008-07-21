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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumn {

   private XViewer xViewer;
   private String id;
   private String name = "";
   private String alternateName = "";
   private String description;
   private boolean multiColumnEditable = false;
   private int width;
   private int defaultWidth;
   private int align;
   private boolean sortForward = true; // if true, sort alphabetically; else reverse
   private boolean show = true;
   private TreeColumn treeColumn;
   private SortDataType sortDataType = SortDataType.String;
   private static ArrayList<XViewerColumn> registeredColumns = new ArrayList<XViewerColumn>();
   private String toolTip = "";
   public enum SortDataType {
      Date, Float, Percent, String, String_MultiLine, Boolean, Integer
   };

   public XViewerColumn(XViewer xViewer, String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super();
      this.xViewer = xViewer;
      this.id = id;
      this.name = name;
      this.defaultWidth = defaultWidth;
      this.width = defaultWidth;
      this.align = align;
      this.show = show;
      this.sortDataType = sortDataType;
      this.multiColumnEditable = multiColumnEditable;
      this.description = description;
      this.toolTip = this.name;
   }

   public XViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(null, id, name, width, width, align, show, sortDataType, false, description);
   }

   public XViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(null, id, name, width, width, align, show, sortDataType, false, null);
   }

   public XViewerColumn(XViewer xViewer, String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      this(xViewer, id, name, width, defaultWidth, align, show, sortDataType, false, null);
   }

   public XViewerColumn(String id, String name, int width, int defaultWidth, int align, boolean show, SortDataType sortDataType) {
      this(null, id, name, width, defaultWidth, align, show, sortDataType);
   }

   public XViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType) {
      this(null, id, name, width, width, align, show, sortDataType);
   }

   public XViewerColumn(XViewer xViewer, String id, String name, int width, int defaultWidth, int align) {
      this(xViewer, id, name, width, defaultWidth, align, true, SortDataType.String);
   }

   public XViewerColumn(XViewer xViewer, String xml) {
      this.xViewer = xViewer;
      setFromXml(xml);
   }

   public boolean equals(Object obj) {
      if (obj instanceof XViewerColumn) {
         return ((XViewerColumn) obj).getId().equals(id);
      }
      return super.equals(obj);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   public static String ID = "id";
   public static String NAME = "name";
   public static String ALTERNATE_NAME = "alt";
   public static String WIDTH = "wdth";
   public static String DEFAULT_WIDTH = "defWdth";
   public static String ALIGN = "algn";
   public static String SORT_FORWARD = "srtFwd";
   public static String SHOW = "show";
   public static String ORDER_NUM = "order";
   public static String XTREECOLUMN_TAG = "xCol";

   public String toXml() {
      StringBuffer sb = new StringBuffer("<" + XTREECOLUMN_TAG + ">");
      sb.append(AXml.addTagData(ID, id));
      sb.append(AXml.addTagData(NAME, name));
      sb.append(AXml.addTagData(ALTERNATE_NAME, alternateName));
      sb.append(AXml.addTagData(DEFAULT_WIDTH, defaultWidth + ""));
      sb.append(AXml.addTagData(WIDTH, (xViewer == null) ? width + "" : xViewer.getCurrentColumnWidth(this) + ""));
      sb.append(AXml.addTagData(ALIGN, getAlignStoreName(align)));
      sb.append(AXml.addTagData(SORT_FORWARD, sortForward + ""));
      sb.append(AXml.addTagData(SHOW, show + ""));
      sb.append("</" + XTREECOLUMN_TAG + ">");
      return sb.toString();
   }

   public void setFromXml(String xml) {
      id = AXml.getTagData(xml, ID);
      name = AXml.getTagData(xml, NAME);
      alternateName = AXml.getTagData(xml, ALTERNATE_NAME);
      width = AXml.getTagIntData(xml, WIDTH);
      defaultWidth = AXml.getTagIntData(xml, DEFAULT_WIDTH);
      // Handle old widths stored before defaultWidth addition
      if (defaultWidth == 0) defaultWidth = width;
      align = getAlignStoreValue(AXml.getTagData(xml, ALIGN));
      sortForward = AXml.getTagBooleanData(xml, SORT_FORWARD);
      show = AXml.getTagBooleanData(xml, SHOW);
   }

   public static String getColumnId(String xml) {
      return AXml.getTagData(xml, ID);
   }

   public String getAlignStoreName(int align) {
      if (align == SWT.CENTER)
         return "center";
      else if (align == SWT.RIGHT)
         return "right";
      else
         return "left";
   }

   public int getAlignStoreValue(String str) {
      if (str.equals("center"))
         return SWT.CENTER;
      else if (str.equals("right"))
         return SWT.RIGHT;
      else
         return SWT.LEFT;
   }

   public String toString() {
      return name + (alternateName != null && !alternateName.equals("") ? " (" + alternateName + ")" : "") + " - " + id + "";
   }

   public int getAlign() {
      return align;
   }

   public String getNameAlternate() {
      return id + (alternateName.equals("") ? "" : " (" + (alternateName + ")"));
   }

   public void setAlign(int align) {
      this.align = align;
   }

   public String getId() {
      return id;
   }

   public void setId(String name) {
      this.id = name;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public XViewer getTreeViewer() {
      return xViewer;
   }

   public boolean isSortForward() {
      return sortForward;
   }

   public void setSortForward(boolean sortForward) {
      this.sortForward = sortForward;
   }

   public void reverseSort() {
      setSortForward(!sortForward);
   }

   public boolean isShow() {
      return show;
   }

   public void setShow(boolean show) {
      this.show = show;
   }

   public TreeColumn getTreeColumn() {
      return treeColumn;
   }

   public void setTreeColumn(TreeColumn treeColumn) {
      this.treeColumn = treeColumn;
   }

   public String getAlternateName() {
      return alternateName;
   }

   public void setAlternateName(String showName) {
      this.alternateName = showName;
   }

   /**
    * @return alternateName if exists, otherwise systemName
    */
   public String getDisplayName() {
      if (!alternateName.equals(""))
         return alternateName;
      else
         return name;
   }

   /**
    * @return the sortDataType
    */
   public SortDataType getSortDataType() {
      return sortDataType;
   }

   /**
    * @param sortDataType the sortDataType to set
    */
   public void setSortDataType(SortDataType sortDataType) {
      this.sortDataType = sortDataType;
   }

   /**
    * @return the registeredColumns
    */
   public static ArrayList<XViewerColumn> getRegisteredColumns() {
      return registeredColumns;
   }

   /**
    * @param treeViewer the treeViewer to set
    */
   public void setXViewer(XViewer treeViewer) {
      this.xViewer = treeViewer;
   }

   /**
    * @return the toolTip
    */
   public String getToolTip() {
      return toolTip;
   }

   /**
    * @param toolTip the toolTip to set
    */
   public void setToolTip(String toolTip) {
      if (toolTip != null) this.toolTip = toolTip;
   }

   /**
    * @return the defaultWidth
    */
   public int getDefaultWidth() {
      return defaultWidth;
   }

   /**
    * @param defaultWidth the defaultWidth to set
    */
   public void setDefaultWidth(int defaultWidth) {
      this.defaultWidth = defaultWidth;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
      this.toolTip = getName() + ":\n" + getDescription();
   }

   public boolean isMultiColumnEditable() {
      return multiColumnEditable;
   }

   public void setMultiColumnEditable(boolean multiColumnEditable) {
      this.multiColumnEditable = multiColumnEditable;
   }

}
