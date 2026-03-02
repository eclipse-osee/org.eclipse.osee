/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetOptionHandler;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristic;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinition extends LayoutItem {

   @JsonIgnore
   private final WidgetOptionHandler options = new WidgetOptionHandler();

   private final XWidgetData widData = new XWidgetData();

   public WidgetDefinition(String name) {
      this(name, WidgetId.SENTINEL);
   }

   public WidgetDefinition(String name, AttributeTypeToken attributeType, WidgetId widgetId, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, attributeType, ComputedCharacteristicToken.SENTINEL, widgetId,
         widgetOptions);
   }

   public WidgetDefinition(WidgetId widgetId) {
      this(widgetId.getName(), widgetId);
   }

   public WidgetDefinition(String name, WidgetId widgetId, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, AttributeTypeToken.SENTINEL, ComputedCharacteristicToken.SENTINEL, widgetId,
         widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, WidgetId widgetId, WidgetOption... widgetOptions) {
      this(name, relationTypeSide, AttributeTypeToken.SENTINEL, ComputedCharacteristicToken.SENTINEL, widgetId,
         widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, AttributeTypeToken attributeType, ComputedCharacteristic<?> computedCharacteristic, WidgetId widgetId, WidgetOption... widgetOptions) {
      super(name);
      this.widData.setName(name);
      this.widData.setRelationTypeSide(relationTypeSide);
      Conditions.assertNotNull(attributeType, "attribute type can not be null for WidgetDefinition [%s]", name);
      this.widData.setAttributeType(attributeType);
      this.widData.setStoreName(attributeType.getName());
      this.widData.setStoreId(attributeType.getId());
      this.widData.setComputedCharacteristic(computedCharacteristic);
      this.widData.setWidgetId(widgetId);
      for (WidgetOption opt : widgetOptions) {
         options.add(opt);
      }
   }

   public WidgetDefinition(AttributeTypeToken attrType, WidgetId widgetId, List<ConditionalRule> conditions, WidgetOption... widgetOptions) {
      this(attrType, widgetId, widgetOptions);
      setConditions(conditions);
   }

   public WidgetDefinition(AttributeTypeToken attrType, WidgetId widgetId, WidgetOption... widgetOptions) {
      this(attrType.getUnqualifiedName(), RelationTypeSide.SENTINEL, attrType, ComputedCharacteristicToken.SENTINEL,
         widgetId, widgetOptions);
   }

   public WidgetDefinition(ComputedCharacteristic<?> computedCharacteristic, WidgetId widgetId, WidgetOption... widgetOptions) {
      this(computedCharacteristic.getName(), RelationTypeSide.SENTINEL, AttributeTypeToken.SENTINEL,
         computedCharacteristic, widgetId, widgetOptions);
   }

   public WidgetDefinition(String name, AttributeTypeToken attrType, WidgetId widgetId, List<ConditionalRule> conditions, WidgetOption... widgetOptions) {
      this(name, attrType, widgetId, widgetOptions);
      setConditions(conditions);
   }

   public String getToolTip() {
      return widData.getToolTip();
   }

   public void setToolTip(String toolTip) {
      this.widData.setToolTip(toolTip);
   }

   public boolean is(WidgetOption widgetOption) {
      return options.contains(widgetOption);
   }

   public void set(WidgetOption widgetOption) {
      options.add(widgetOption);
   }

   public Object getDefaultValue() {
      return widData.getDefaultValue();
   }

   public void setDefaultValue(String defaultValue) {
      this.widData.setDefaultValue(defaultValue);
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(),
         getAttributeType().isValid() ? getAttributeType().toStringWithId() : "");
   }

   public IAtsWidgetOptionHandler getOptions() {
      return options;
   }

   public void setConstraint(double min, double max) {
      widData.setMin(min);
      widData.setMax(max);
   }

   public Double getMin() {
      return widData.getMin();
   }

   public Double getMax() {
      return widData.getMax();
   }

   public AttributeTypeToken getAttributeType() {
      return widData.getAttributeType();
   }

   public ComputedCharacteristicToken<?> getComputedCharacteristic() {
      return widData.getComputedCharacteristic();
   }

   public RelationTypeSide getRelationTypeSide() {
      return widData.getRelationTypeSide();
   }

   public void addParameter(String key, Object obj) {
      widData.addParameter(key, obj);
   }

   public Object getParameter(String key) {
      return widData.getParameters().get(key);
   }

   public Map<String, Object> getParameters() {
      return widData.getParameters();
   }

   public List<ConditionalRule> getConditions() {
      return widData.getConditions();
   }

   public void setConditions(List<ConditionalRule> conditions) {
      this.widData.setConditions(conditions);
   }

   public WidgetDefinition andEnumeratedArt(ArtifactToken enumeratedArt) {
      Conditions.requireNonNull(enumeratedArt, "Enumerated Art");
      this.widData.setEnumeratedArt(enumeratedArt);
      return this;
   }

   public ArtifactToken getEnumeratedArt() {
      return widData.getEnumeratedArt();
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      andEnumeratedArt(enumeratedArt);
   }

   public OseeImage getOseeImage() {
      return widData.getOseeImage();
   }

   public void setOseeImage(OseeImage oseeImage) {
      widData.setOseeImage(oseeImage);
   }

   public AttributeTypeToken getAttributeType2() {
      return widData.getAttributeType2();
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      this.widData.setAttributeType2(attributeType2);
      this.widData.setStoreName(attributeType2.getName());
      this.widData.setStoreId(attributeType2.getId());
   }

   public List<WidgetHint> getWidgetHints() {
      return widData.getWidgetHints();
   }

   public IUserGroupArtifactToken getUserGroup() {
      return widData.getUserGroup();
   }

   public void setUserGroup(IUserGroupArtifactToken userGroup) {
      this.widData.setUserGroup(userGroup);
   }

   public LayoutItem andCondition(ConditionalRule... rules) {
      for (ConditionalRule rule : rules) {
         widData.getConditions().add(rule);
      }
      return this;
   }

   public LayoutItem andWidgetHint(WidgetHint widgetHint) {
      getWidgetHints().add(widgetHint);
      return this;
   }

   public LayoutItem andRequiredByUserGroup(IUserGroupArtifactToken userGroup) {
      setUserGroup(userGroup);
      return this;
   }

   public WidgetId getWidgetId() {
      return widData.getWidgetId();
   }

   public void setWidgetId(WidgetId widgetId) {
      this.widData.setWidgetId(widgetId);
   }

   public XWidgetData getWidData() {
      return widData;
   }

}
