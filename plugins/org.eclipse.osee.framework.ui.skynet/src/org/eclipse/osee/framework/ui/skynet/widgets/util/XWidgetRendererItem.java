/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ISelectableValueProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XOptionHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class XWidgetRendererItem implements Cloneable {

   private static final FrameworkXWidgetProvider xWidgetFactory = FrameworkXWidgetProvider.getInstance();
   private static final String UNKNOWN = "Unknown";
   private static final int DEFAULT_HEIGHT = 9999;
   private final Map<String, Object> parameters = new HashMap<String, Object>();
   private final XOptionHandler xOptionHandler = new XOptionHandler();
   private String name = "Unknown";
   private String id = "";
   private String storeName = "";
   private Long storeId = -1L;
   private String xWidgetName = UNKNOWN;
   private XWidget xWidget;
   private int beginComposite = 0; // If >0, indicates new child composite with columns == value
   private boolean border = false;
   private int beginGroupComposite = 0; // If >0, indicates new child composite with columns == value
   private boolean endComposite, endGroupComposite; // indicated end of child composite
   private String groupName;
   private int height = DEFAULT_HEIGHT;
   private boolean noSelect;
   private String toolTip;
   private SwtXWidgetRenderer dynamicXWidgetLayout;
   private String defaultValue;
   private String keyedBranchName;
   private Artifact artifact;
   private Object object;
   private String doubleClickText;
   private ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   private AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
   private RelationTypeSide relationTypeSide;
   private ComputedCharacteristicToken<?> computedCharacteristic;
   private boolean horizontalLabel;
   private Object defaultValueObj;
   private ISelectableValueProvider valueProvider;
   private Collection<? extends Object> values = new ArrayList<Object>();
   private List<ConditionalRule> conditions = new ArrayList<>();
   private ArtifactToken enumeratedArt;
   private ArtifactId teamId = ArtifactId.SENTINEL;

   public XWidgetRendererItem(SwtXWidgetRenderer dynamicXWidgetLayout, XOption... xOption) {
      this.dynamicXWidgetLayout = dynamicXWidgetLayout;
      xOptionHandler.add(XOption.EDITABLE);
      xOptionHandler.add(XOption.ALIGN_LEFT);
      xOptionHandler.add(xOption);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public boolean isHeightSet() {
      return height != DEFAULT_HEIGHT;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getName() {
      return name;
   }

   public String getStoreName() {
      return storeName;
   }

   public void setStoreName(String storeName) {
      this.storeName = storeName;
   }

   public boolean isRequired() {
      if (xOptionHandler.contains(XOption.REQUIRED)) {
         return true;
      }
      if (dynamicXWidgetLayout != null) {
         return dynamicXWidgetLayout.isOrRequired(getStoreName()) || //
            dynamicXWidgetLayout.isXOrRequired(getStoreName());
      }
      return false;
   }

   public boolean isRequiredForCompletion() {
      if (xOptionHandler.contains(XOption.REQUIRED_FOR_COMPLETION)) {
         return true;
      }
      return false;
   }

   public boolean isFillVertically() {
      return xOptionHandler.contains(XOption.FILL_VERTICALLY);
   }

   public boolean isFillHorizontally() {
      return xOptionHandler.contains(XOption.FILL_HORIZONTALLY);
   }

   public String getXWidgetName() {
      return xWidgetName;
   }

   public void setXWidgetName(String widget) {
      xWidgetName = widget;
   }

   public void setName(String name) {
      this.name = name;
   }

   // TODO This method will need to be removed
   public XWidget getXWidget() {
      if (xWidget == null) {
         xWidget = xWidgetFactory.createXWidget(this);
      }
      return xWidget;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getBeginComposite() {
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_10)) {
         return 10;
      }
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_8)) {
         return 8;
      }
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_6)) {
         return 6;
      }
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_4)) {
         return 4;
      }
      return beginComposite;
   }

   public int getBeginGroupComposite() {
      if (xOptionHandler.contains(XOption.BEGIN_GROUP_COMPOSITE_10)) {
         return 10;
      }
      if (xOptionHandler.contains(XOption.BEGIN_GROUP_COMPOSITE_8)) {
         return 8;
      }
      if (xOptionHandler.contains(XOption.BEGIN_GROUP_COMPOSITE_6)) {
         return 6;
      }
      if (xOptionHandler.contains(XOption.BEGIN_GROUP_COMPOSITE_4)) {
         return 4;
      }
      return beginGroupComposite;
   }

   public void setBeginComposite(int beginComposite) {
      this.beginComposite = beginComposite;
   }

   public void setBeginComposite(int columns, boolean border) {
      setBeginComposite(columns);
      this.border = border;
   }

   public void setBeginGroupComposite(int beginGroupComposite) {
      this.beginGroupComposite = beginGroupComposite;
   }

   public boolean isEndComposite() {
      return endComposite;
   }

   public boolean isEndGroupComposite() {
      return endGroupComposite;
   }

   public void setEndComposite(boolean endComposite) {
      this.endComposite = endComposite;
   }

   public void setEndGroupComposite(boolean endGroupComposite) {
      this.endGroupComposite = endGroupComposite;
   }

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public SwtXWidgetRenderer getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setKeyedBranchName(String keyedBranchName) {
      this.keyedBranchName = keyedBranchName;
   }

   public String getKeyedBranchName() {
      return keyedBranchName;
   }

   public void setDynamicXWidgetLayout(SwtXWidgetRenderer dynamicXWidgetLayout) {
      this.dynamicXWidgetLayout = dynamicXWidgetLayout;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public XOptionHandler getXOptionHandler() {
      return xOptionHandler;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   /**
    * Generic object passed along to the created XWidget
    */
   public void setObject(Object object) {
      this.object = object;
   }

   public Object getObject() {
      return object;
   }

   public void setFillVertically(boolean b) {
      xOptionHandler.add(XOption.FILL_VERTICALLY);
   }

   public Long getStoreId() {
      return storeId;
   }

   public void setStoreId(Long storeId) {
      this.storeId = storeId;
   }

   public void setDoubleClickText(String doubleClickText) {
      this.doubleClickText = doubleClickText;
   }

   public String getDoubleClickText() {
      return doubleClickText;
   }

   public RelationTypeSide getRelationTypeSide() {
      return relationTypeSide;
   }

   public void setRelationTypeSide(RelationTypeSide relationTypeSide) {
      this.relationTypeSide = relationTypeSide;
   }

   public ComputedCharacteristicToken<?> getComputedCharacteristic() {
      return computedCharacteristic;
   }

   public void setComputedCharacteristic(ComputedCharacteristicToken<?> computedCharacteristicToken) {
      this.computedCharacteristic = computedCharacteristicToken;
   }

   /**
    * @return artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   /**
    * @param artifactType that may or may not be the storage artifact type. Can be used by any widget and only the
    * widget knows what to do with this value.
    */
   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   public void addParameter(String key, Object value) {
      parameters.put(key, value);
   }

   public Map<String, Object> getParameters() {
      return parameters;
   }

   public boolean isNoSelect() {
      return noSelect;
   }

   public void setNoSelect(boolean noSelect) {
      this.noSelect = noSelect;
   }

   public void setHorizontalLabel(boolean set) {
      this.horizontalLabel = set;
   }

   public boolean isHorizontalLabel() {
      return horizontalLabel;
   }

   public void setDefaultValueObj(Object value) {
      this.defaultValueObj = value;
   }

   public Object getDefaultValueObj() {
      return defaultValueObj;
   }

   public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }

   public ISelectableValueProvider getValueProvider() {
      return valueProvider;
   }

   public void setValueProvider(ISelectableValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

   public Collection<? extends Object> getValues() {
      return values;
   }

   public void setValues(Collection<? extends Object> values) {
      this.values = values;
   }

   public List<ConditionalRule> getConditions() {
      return conditions;
   }

   public void setConditions(List<ConditionalRule> conditions) {
      this.conditions = conditions;
   }

   public ArtifactToken getEnumeratedArt() {
      return enumeratedArt;
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      this.enumeratedArt = enumeratedArt;
   }

   public boolean isBorder() {
      return border;
   }

   public void setBorder(boolean border) {
      this.border = border;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   public ArtifactId getTeamId() {
      return teamId;
   }

   public void setTeamId(ArtifactId teamId) {
      this.teamId = teamId;
   }

}
