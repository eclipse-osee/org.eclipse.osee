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
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;

/**
 * @author Donald G. Dunne
 */
public class WorkWidgetDefinition extends WorkItemDefinition {

   public static final String tagName = DynamicXWidgetLayout.XWIDGET;
   private DynamicXWidgetLayoutData widgetLayoutData;

   public WorkWidgetDefinition(String name, String id) {
      super(name, id, null);
   }

   public WorkWidgetDefinition(DynamicXWidgetLayoutData xWidgetLayoutData) {
      super(xWidgetLayoutData.getName() + " - " + xWidgetLayoutData.getId(), xWidgetLayoutData.getId(), null);
      setXWidgetLayoutData(xWidgetLayoutData);
   }

   public void setXWidgetLayoutData(DynamicXWidgetLayoutData xWidgetLayoutData) {
      widgetLayoutData = xWidgetLayoutData;
   }

   public WorkWidgetDefinition(Artifact artifact) throws OseeCoreException {
      this(artifact.getName(), artifact.getSoleAttributeValue(CoreAttributeTypes.WorkId, artifact.getName()));
      setType(artifact.getSoleAttributeValue(CoreAttributeTypes.WorkType, (String) null));
      loadWorkDataKeyValueMap(artifact);

      DynamicXWidgetLayoutData data = getFromXml(getWorkDataValue(tagName));
      setXWidgetLayoutData(data);
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact art = super.toArtifact(writeType);
      art.setSoleAttributeFromString(CoreAttributeTypes.WorkData, tagName + "=" + XWidgetParser.toXml(get()));
      return art;
   }

   public DynamicXWidgetLayoutData get() {
      DynamicXWidgetLayoutData data = null;
      try {
         // Hand out an modifiable copy of the LayoutData to ensure widgets created off it aren't shared
         data = (DynamicXWidgetLayoutData) widgetLayoutData.clone();
      } catch (CloneNotSupportedException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return data;
   }

   public void set(DynamicXWidgetLayoutData xWidgetLayoutData) {
      setXWidgetLayoutData(xWidgetLayoutData);
   }

   public static boolean isWorkItemXWidget(String xml) {
      return xml.contains(tagName);
   }

   public String toXml() {
      throw new IllegalStateException("WorkItemXWidgetDefinition.toXml() Not implemented.");
   }

   /**
    * Create WorkItemXWidgetDefinition from xml
    * 
    * @param xml <XWidget displayName="Problem" storageName="ats.Problem" xwidgetType="XTextDam" fill="Vertically"/>
    */
   public static WorkWidgetDefinition createFromXml(String xml) throws OseeCoreException {
      DynamicXWidgetLayoutData data = getFromXml(xml);
      return new WorkWidgetDefinition(data);
   }

   public static DynamicXWidgetLayoutData getFromXml(String xml) throws OseeCoreException {
      DynamicXWidgetLayoutData data = XWidgetParser.extractlayoutData(null, xml);
      Conditions.checkNotNull(data, "DynamicXWidgetLayoutData",
         "Unable to create WorkItemXWidgetDefinition from xml [%s]", xml);
      return data;
   }

   @Override
   public IArtifactType getArtifactType() {
      return CoreArtifactTypes.WorkWidgetDefinition;
   }
}
