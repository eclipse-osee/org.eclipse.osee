/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.io.IOException;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.xml.sax.SAXException;

/**
 * @author Donald G. Dunne
 */
public class WorkWidgetDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Widget Definition";
   public static String tagName = DynamicXWidgetLayout.XWIDGET;

   public WorkWidgetDefinition(String name, String id) {
      super(name, id, null);
   }

   public WorkWidgetDefinition(DynamicXWidgetLayoutData xWidgetLayoutData) {
      super(xWidgetLayoutData.getName() + " - " + xWidgetLayoutData.getId(), xWidgetLayoutData.getId(), null);
      setData(xWidgetLayoutData);
   }

   public WorkWidgetDefinition(Artifact artifact) throws OseeCoreException, SQLException {
      this(artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), artifact.getDescriptiveName()));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));

      DynamicXWidgetLayoutData data =
            getFromXml(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_DATA.getAttributeTypeName(), ""));
      setData(data);
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException, SQLException {
      Artifact art = super.toArtifact(writeType);
      try {
         art.setSoleAttributeFromString(WorkItemAttributes.WORK_DATA.getAttributeTypeName(), XWidgetParser.toXml(get()));
      } catch (ParserConfigurationException ex) {
         throw new OseeCoreException(ex);
      }
      return art;
   }

   public DynamicXWidgetLayoutData get() {
      // Hand out an modifiable copy of the LayoutData to ensure widgets created off it aren't shared
      try {
         return (DynamicXWidgetLayoutData) ((DynamicXWidgetLayoutData) getData()).clone();
      } catch (CloneNotSupportedException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      return null;
   }

   public void set(DynamicXWidgetLayoutData xWidgetLayoutData) {
      setData(xWidgetLayoutData);
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
    * @return
    * @throws Exception
    */
   public static WorkWidgetDefinition createFromXml(String xml) throws OseeCoreException, SQLException {
      DynamicXWidgetLayoutData data = getFromXml(xml);
      return new WorkWidgetDefinition(data);
   }

   public static DynamicXWidgetLayoutData getFromXml(String xml) throws OseeCoreException, SQLException {
      try {
         DynamicXWidgetLayoutData data = XWidgetParser.extractlayoutData(null, xml);
         if (data == null) throw new IllegalArgumentException(
               "Unable to create WorkItemXWidgetDefinition from xml\"" + xml + "\"");
         return data;
      } catch (ParserConfigurationException ex) {
         throw new OseeCoreException(ex);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      } catch (SAXException ex) {
         throw new OseeCoreException(ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#getArtifactTypeName()
    */
   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }
}
