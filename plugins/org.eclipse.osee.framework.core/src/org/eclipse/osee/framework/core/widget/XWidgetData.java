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

package org.eclipse.osee.framework.core.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchQueryData;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * This is the non-UI representation of what should become a widget. It has no tie to any specific UI and could be used
 * by SWT, Web or other widget toolkits.<br/>
 * <br/>
 * It can be created from xml, dot-notation builders, json or any other input.<br/>
 * <br/>
 * If used on the Eclipse client, it would be rendered into SWT widgets for things like BLAMS, Workflow Editors,
 * Artifact Editor and the like. <br/>
 * <br/>
 * This should remain serializable so it can be transported for different UI purposes
 *
 * @author Donald G. Dunne
 */
public class XWidgetData {

   private static final int DEFAULT_HEIGHT = 9999;
   private ArtifactId teamId = ArtifactId.SENTINEL;
   private ArtifactToken artifact;
   private ArtifactToken enumeratedArt = ArtifactToken.SENTINEL;
   private ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   private AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
   private AttributeTypeToken attributeType2 = AttributeTypeToken.SENTINEL;
   private BranchQueryData branchQuery = new BranchQueryData();
   private Collection<Object> values = new ArrayList<Object>();
   private ComputedCharacteristicToken<?> computedCharacteristic;
   private Double max;
   private Double min;
   private ISelectableValueProvider valueProvider;
   private IUserGroupArtifactToken userGroup = UserGroupArtifactToken.SENTINEL;
   private List<ConditionalRule> conditions = new ArrayList<>();
   private List<WidgetHint> widgetHints = new ArrayList<>();
   private Long id = 0L; // Temporary id so this widget can be indexed and found
   private Long storeId = -1L;
   private Long storeId2 = -1L;
   private OseeImage oseeImage;
   private RelationTypeSide relationTypeSide;
   private Object labelProvider; // ILabelProvider
   private Object defaultValue;
   private Object object;
   private Object formToolkit; // SWT FormToolkit
   private Object managedForm; // SWT ManagedForm
   private String doubleClickText;
   private String groupName;
   private String keyedBranchName;
   private String name = "";
   private String storeName = "";
   private String storeName2 = "";
   private String toolTip;
   private WidgetId widgetId = WidgetId.SENTINEL;
   private boolean border = false;
   private boolean endComposite, endGroupComposite; // indicated end of child composite
   private final List<String> selectable = new ArrayList<>();
   private final Map<String, Object> parameters = new HashMap<String, Object>();
   private final XOptionHandler xOptionHandler = new XOptionHandler();
   private int beginComposite = 0; // If >0, indicates new child composite with columns == value
   private int beginGroupComposite = 0; // If >0, indicates new child composite with columns == value
   private int height = DEFAULT_HEIGHT;

   public XWidgetData(XOption... xOption) {
      xOptionHandler.add(XOption.EDITABLE);
      xOptionHandler.add(XOption.ALIGN_LEFT);
      xOptionHandler.add(XOption.CLEARABLE);
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

   public void setName(String name) {
      this.name = name;
   }

   public void setDefaultValue(Object defaultValue) {
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

   public Object getDefaultValue() {
      return defaultValue;
   }

   public String getDefaultValueStr() {
      if (defaultValue == null) {
         return "";
      }
      return String.valueOf(defaultValue);
   }

   public void setKeyedBranchName(String keyedBranchName) {
      this.keyedBranchName = keyedBranchName;
   }

   public String getKeyedBranchName() {
      return keyedBranchName;
   }

   public XOptionHandler getXOptionHandler() {
      return xOptionHandler;
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public void setFillHorzontally(boolean fillHoriz) {
      if (fillHoriz) {
         xOptionHandler.add(XOption.FILL_HORIZONTALLY);
      } else {
         xOptionHandler.remove(XOption.FILL_HORIZONTALLY);
      }
   }

   public void setFillVertically(boolean fillVert) {
      if (fillVert) {
         xOptionHandler.add(XOption.FILL_VERTICALLY);
      } else {
         xOptionHandler.remove(XOption.FILL_VERTICALLY);
      }
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

   public void setHorizontalLabel(boolean set) {
      add(XOption.HORIZONTAL_LABEL);
   }

   public boolean isHorizontalLabel() {
      return is(XOption.HORIZONTAL_LABEL);
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

   public Collection<Object> getValues() {
      return values;
   }

   public void setValues(Collection<Object> values) {
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
      Conditions.requireNonNull(enumeratedArt, "Enumerated Art");
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

   public void addWidgetHint(WidgetHint hint) {
      widgetHints.add(hint);
   }

   public List<WidgetHint> getWidgetHints() {
      return widgetHints;
   }

   public void setWidgetHints(List<WidgetHint> widgetHints) {
      this.widgetHints = widgetHints;
   }

   public String getStoreName2() {
      return storeName2;
   }

   public void setStoreName2(String storeName2) {
      this.storeName2 = storeName2;
   }

   public Long getStoreId2() {
      return storeId2;
   }

   public void setStoreId2(Long storeId2) {
      this.storeId2 = storeId2;
   }

   public AttributeTypeToken getAttributeType2() {
      return attributeType2;
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      this.attributeType2 = attributeType2;
   }

   public OseeImage getOseeImage() {
      return oseeImage;
   }

   public void setOseeImage(OseeImage oseeImage) {
      this.oseeImage = oseeImage;
   }

   public IUserGroupArtifactToken getUserGroup() {
      return userGroup;
   }

   public void setUserGroup(IUserGroupArtifactToken userGroup) {
      this.userGroup = userGroup;
   }

   public BranchQueryData getBranchQuery() {
      return branchQuery;
   }

   public void setBranchQuery(BranchQueryData branchQuery) {
      this.branchQuery = branchQuery;
   }

   public WidgetId getWidgetId() {
      return widgetId;
   }

   public void setWidgetId(WidgetId widgetId) {
      this.widgetId = widgetId;
   }

   public List<String> getSelectable() {
      return selectable;
   }

   public void setSelectable(List<? extends Object> selectable) {
      for (Object obj : selectable) {
         this.selectable.add(obj.toString());
      }
   }

   public Double getMin() {
      return min;
   }

   public void setMin(Double min) {
      this.min = min;
   }

   public Double getMax() {
      return max;
   }

   public void setMax(Double max) {
      this.max = max;
   }

   public void add(XOption xOption) {
      getXOptionHandler().add(xOption);
   }

   public boolean is(XOption xOption) {
      return getXOptionHandler().contains(xOption);
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public Object getObject() {
      return object;
   }

   public void setObject(Object object) {
      this.object = object;
   }

   public Object getFormToolkit() {
      return formToolkit;
   }

   public void setFormToolkit(Object formToolkit) {
      this.formToolkit = formToolkit;
   }

   public Object getManagedForm() {
      return managedForm;
   }

   public void setManagedForm(Object managedForm) {
      this.managedForm = managedForm;
   }

   public Object getLabelProvider() {
      return labelProvider;
   }

   public void setLabelProvider(Object labelProvider) {
      this.labelProvider = labelProvider;
   }

}
