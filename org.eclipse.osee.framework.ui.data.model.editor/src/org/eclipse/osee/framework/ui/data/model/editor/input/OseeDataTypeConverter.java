/*
 * Created on Jan 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor.input;

import java.util.Collection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class OseeDataTypeConverter implements IOseeDataTypeProcessor {

   private final DataTypeSource dataTypeSource;

   public OseeDataTypeConverter(String sourceId) {
      this.dataTypeSource = new DataTypeSource(sourceId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#doesArtifactSuperTypeExist(java.lang.String)
    */
   @Override
   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName) throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#onArtifactType(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void onArtifactType(String factoryName, String namespace, String name) throws OseeCoreException {
      ArtifactDataType artifactDataType = new ArtifactDataType(namespace, name, factoryName, factoryName, null);

      this.dataTypeSource.add(artifactDataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#onAttributeType(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, java.lang.String)
    */
   @Override
   public void onAttributeType(String baseAttributeClass, String providerAttributeClass, String fileTypeExtension, String namespace, String name, String defaultValue, String validityXml, int minOccurrence, int maxOccurrence, String toolTipText, String taggerId) throws OseeCoreException {
      AttributeDataType attributeDataType =
            new AttributeDataType(namespace, name, baseAttributeClass, defaultValue, fileTypeExtension, maxOccurrence,
                  minOccurrence, providerAttributeClass, taggerId, toolTipText, validityXml);

      // Create attribute provider and base attribute classes here ?? 

      this.dataTypeSource.add(attributeDataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#onAttributeValidity(java.lang.String, java.lang.String, java.util.Collection)
    */
   @Override
   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteTypes) throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#onRelationType(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void onRelationType(String namespace, String name, String sideAName, String sideBName, String abPhrasing, String baPhrasing, String shortName, String ordered) throws OseeCoreException {
      RelationDataType relationDataType =
            new RelationDataType(namespace, name, abPhrasing, baPhrasing, Boolean.valueOf(ordered), shortName,
                  sideAName, sideBName);

      this.dataTypeSource.add(relationDataType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.importing.IOseeDataTypeProcessor#onRelationValidity(java.lang.String, java.lang.String, int, int)
    */
   @Override
   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException {
   }

   public DataTypeSource getODMModel() {
      return dataTypeSource;
   }

}
