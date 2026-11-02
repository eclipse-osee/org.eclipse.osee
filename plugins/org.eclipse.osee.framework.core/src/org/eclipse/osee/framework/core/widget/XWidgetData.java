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
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.UserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

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
   private ArtifactToken artifact = ArtifactToken.SENTINEL;
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
   private Long id = 0L; // Temporary id so this widget can be indexed and found
   private OseeImage oseeImage;
   private RelationTypeSide relationTypeSide;
   private Object labelProvider; // ILabelProvider
   private Object defaultValue;
   private Object object;
   private Object formToolkit; // SWT FormToolkit
   private Object managedForm; // SWT ManagedForm
   private Object xWidget;
   private Object viewerComparator;
   private Object widgetDef; // WidgetDefinition creating this widget
   private final List<Object> xModListeners = new ArrayList<>();
   private String doubleClickText;
   private String groupName;
   private String keyedBranchName;
   private String label = "";
   private String toolTip;
   private String diagDescUrl;
   private String dateFormat = DateUtil.MMDDYY;
   private WidgetId widgetId = WidgetId.SENTINEL;
   private boolean border = false;
   private boolean endComposite, endGroupComposite; // indicated end of child composite
   private final List<String> selectable = new ArrayList<>();
   private final List<AttributeTypeToken> providerFor = new ArrayList<>();
   private final Map<String, Object> parameters = new HashMap<String, Object>();
   private final XOptionHandler xOptionHandler = new XOptionHandler();
   private int beginComposite = 0; // If >0, indicates new child composite with columns == value
   private int beginGroupComposite = 0; // If >0, indicates new child composite with columns == value
   private int height = DEFAULT_HEIGHT;
   private Integer swtFgLabelColor = null;
   private IValueValidator validator = XXNoOpValidator.instance;
   private final List<SwtWidgetListener> swtValueListeners = new ArrayList<>();
   private BranchToken branch = BranchToken.SENTINEL;

   public XWidgetData(XOption... xOption) {
      xOptionHandler.add(XOption.WRITE);
      xOptionHandler.add(XOption.ALIGN_LEFT);
      xOptionHandler.add(xOption);
   }

   public boolean isHeightSet() {
      return height != DEFAULT_HEIGHT;
   }

   @Override
   public String toString() {
      return String.format("Label: [%s] \n%s \nXOpts: %s \nAttr Type: %s", getLabel(), getWidgetId(),
         getXOptionHandler().getXOptions(), getAttributeType().toStringWithId());
   }

   public String getLabel() {
      return label;
   }

   public boolean isRequired() {
      if (xOptionHandler.contains(XOption.REQUIRED)) {
         return true;
      }
      return false;
   }

   public boolean isRequiredForCompletion() {
      if (xOptionHandler.contains(XOption.RFC)) {
         return true;
      }
      return false;
   }

   public boolean isFillVertically() {
      return xOptionHandler.contains(XOption.FILL_VERT);
   }

   public boolean isFillHorizontally() {
      return xOptionHandler.contains(XOption.FILL_HORZ);
   }

   public void setLabel(String label) {
      this.label = label;
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
      if (xOptionHandler.contains(XOption.COMP_10)) {
         return 10;
      }
      if (xOptionHandler.contains(XOption.COMP_8)) {
         return 8;
      }
      if (xOptionHandler.contains(XOption.COMP_6)) {
         return 6;
      }
      if (xOptionHandler.contains(XOption.COMP_4)) {
         return 4;
      }
      return beginComposite;
   }

   public int getBeginGroupComposite() {
      if (xOptionHandler.contains(XOption.GCOMP_10)) {
         return 10;
      }
      if (xOptionHandler.contains(XOption.GCOMP_8)) {
         return 8;
      }
      if (xOptionHandler.contains(XOption.GCOMP_6)) {
         return 6;
      }
      if (xOptionHandler.contains(XOption.GCOMP_4)) {
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
         xOptionHandler.add(XOption.FILL_HORZ);
      } else {
         xOptionHandler.remove(XOption.FILL_HORZ);
      }
   }

   public void setFillVertically(boolean fillVert) {
      if (fillVert) {
         xOptionHandler.add(XOption.FILL_VERT);
      } else {
         xOptionHandler.remove(XOption.FILL_VERT);
      }
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
      set(XOption.HORZ_LABEL);
   }

   public boolean isHorizontalLabel() {
      return is(XOption.HORZ_LABEL);
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
      if (this.attributeType.equals(attributeType)) {
         return;
      }
      if (this.attributeType.isValid() && attributeType.isInvalid()) {
         Conditions.assertTrue(attributeType.isValid(),
            "Valid attr type %s shouldn't be overwrittend by SENTINEL for %s", this.attributeType.toStringWithId());
      }
      this.attributeType = attributeType;
   }

   public ArtifactId getTeamId() {
      return teamId;
   }

   public void setTeamId(ArtifactId teamId) {
      this.teamId = teamId;
   }

   public AttributeTypeToken getAttributeType2() {
      return attributeType2;
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      Conditions.assertTrue(attributeType2.isValid(), "Attr Type 2 should be valid for %s",
         attributeType.toStringWithId());
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

   public IValueValidator getValidator() {
      return validator;
   }

   public void setValidator(IValueValidator validator) {
      this.validator = validator;
   }

   public Integer getFgLabelColor() {
      return swtFgLabelColor;
   }

   public void setFgLabelColor(Integer swtFgLabelColor) {
      this.swtFgLabelColor = swtFgLabelColor;
   }

   public void addSwtValueListener(int swtOp, Object listener) {
      swtValueListeners.add(new SwtWidgetListener(swtOp, listener));
   }

   public List<SwtWidgetListener> getSwtValueListeners() {
      return swtValueListeners;
   }

   public String getDateFormat() {
      return dateFormat;
   }

   public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;
   }

   public Object getxWidget() {
      return xWidget;
   }

   public void setXWidget(Object xWidget) {
      this.xWidget = xWidget;
   }

   public Object getWidgetDef() {
      return widgetDef;
   }

   public void setWidgetDef(Object widgetDef) {
      this.widgetDef = widgetDef;
   }

   // XOption convenience methods

   public void set(XOption... xOptions) {
      for (XOption opt : xOptions) {
         getXOptionHandler().add(opt);
      }
   }

   public void remove(XOption xOption) {
      getXOptionHandler().remove(xOption);
   }

   /**
    * @return true if any match
    */
   public boolean is(XOption... options) {
      for (XOption opt : options) {
         if (getXOptionHandler().contains(opt)) {
            return true;
         }
      }
      return false;
   }

   public boolean isNot(XOption options) {
      return !is(options);
   }

   public boolean isAll(XOption... xOptions) {
      for (XOption xOption : xOptions) {
         if (!getXOptionHandler().contains(xOption)) {
            return false;
         }
      }
      return true;
   }

   public boolean isAny(XOption... xOptions) {
      for (XOption xOption : xOptions) {
         if (getXOptionHandler().contains(xOption)) {
            return true;
         }
      }
      return false;
   }

   public String getDiagDescUrl() {
      return diagDescUrl;
   }

   /**
    * URL that describes enumerated values. If set, this will be displayed in dialog as a hyperlink that the users can
    * select to get more information.
    */
   public void setDiagDescUrl(String diagDescUrl) {
      this.diagDescUrl = diagDescUrl;
   }

   public BranchToken getBranch() {
      return branch;
   }

   public void setBranch(BranchToken branch) {
      this.branch = branch;
   }

   public void setProviderfor(AttributeTypeToken... attrTypes) {
      for (AttributeTypeToken attrType : attrTypes) {
         this.providerFor.add(attrType);
      }
   }

   @Override
   public XWidgetData clone() {
      XWidgetData copy = new XWidgetData();
      copy.artifact = this.artifact;
      copy.artifactType = this.artifactType;
      copy.attributeType = this.attributeType;
      copy.attributeType2 = this.attributeType2;
      copy.beginComposite = this.beginComposite;
      copy.beginGroupComposite = this.beginGroupComposite;
      copy.border = this.border;
      copy.branch = this.branch;
      copy.branchQuery = this.branchQuery;
      copy.computedCharacteristic = this.computedCharacteristic;
      copy.conditions = new ArrayList<>(this.conditions);
      copy.dateFormat = this.dateFormat;
      copy.defaultValue = this.defaultValue;
      copy.diagDescUrl = this.diagDescUrl;
      copy.doubleClickText = this.doubleClickText;
      copy.endComposite = this.endComposite;
      copy.endGroupComposite = this.endGroupComposite;
      copy.enumeratedArt = this.enumeratedArt;
      copy.formToolkit = this.formToolkit;
      copy.groupName = this.groupName;
      copy.height = this.height;
      copy.id = this.id;
      copy.keyedBranchName = this.keyedBranchName;
      copy.label = this.label;
      copy.labelProvider = this.labelProvider;
      copy.managedForm = this.managedForm;
      copy.max = this.max;
      copy.min = this.min;
      copy.object = this.object;
      copy.oseeImage = this.oseeImage;
      copy.parameters.putAll(this.parameters);
      copy.providerFor.addAll(this.providerFor);
      copy.relationTypeSide = this.relationTypeSide;
      copy.selectable.addAll(this.selectable);
      copy.swtFgLabelColor = this.swtFgLabelColor;
      copy.swtValueListeners.addAll(this.swtValueListeners);
      copy.teamId = this.teamId;
      copy.toolTip = this.toolTip;
      copy.userGroup = this.userGroup;
      copy.validator = this.validator;
      copy.valueProvider = this.valueProvider;
      copy.values = new ArrayList<>(this.values);
      copy.viewerComparator = this.viewerComparator;
      copy.widgetDef = this.widgetDef;
      copy.widgetId = this.widgetId;
      copy.xModListeners.addAll(this.xModListeners);
      copy.xOptionHandler.add(this.xOptionHandler.getXOptions());
      copy.xWidget = this.xWidget;
      return copy;
   }

   public List<AttributeTypeToken> getProviderFor() {
      return providerFor;
   }

   public Object getViewerComparator() {
      return viewerComparator;
   }

   public void setViewerComparator(Object viewerComparator) {
      this.viewerComparator = viewerComparator;
   }

   public void addXModListener(Object listener) {
      this.xModListeners.add(listener);
   }

   public List<Object> getxModListeners() {
      return xModListeners;
   }

   public boolean isCommon() {
      return getAttributeType().hasDisplayHint(DisplayHint.Common);
   }

}
