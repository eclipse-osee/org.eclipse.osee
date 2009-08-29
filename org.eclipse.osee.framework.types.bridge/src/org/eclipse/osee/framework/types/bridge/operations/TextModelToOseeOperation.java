package org.eclipse.osee.framework.types.bridge.operations;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

public class TextModelToOseeOperation extends AbstractOperation {
   private final java.net.URI resource;

   public TextModelToOseeOperation(java.net.URI resource) {
      super("OSEE Text Model to OSEE", Activator.PLUGIN_ID);
      this.resource = resource;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeTypeModel model = OseeTypeModelUtil.loadModel(resource);
      EcoreUtil.getAllContents(model, true);

      //      for (Import importEntry : model.getImports()) {
      //         System.out.println("Import: " + importEntry.getImportURI());
      //         OseeTypeModel importedModel = OseeTypeModelUtil.loadModel(new URI(importEntry.getImportURI()));
      //      }
      if (!model.getTypes().isEmpty()) {
         double workPercentage = 1.0 / model.getTypes().size();
         for (OseeType type : model.getTypes()) {
            if (type instanceof ArtifactType) {
               handleArtifactType((ArtifactType) type);
            } else if (type instanceof AttributeType) {
               handleAttributeType((AttributeType) type);
            } else if (type instanceof RelationType) {
               handleRelationType((RelationType) type);
            }
            monitor.worked(calculateWork(workPercentage));
         }
      }
   }

   private void handleArtifactType(ArtifactType artifactType) throws OseeCoreException {
      String superTypeId = "";
      ArtifactType superType = artifactType.getSuperArtifactType();
      if (superType != null) {
         superTypeId = superType.getName();
      }
      String guid = "";
      // TODO: Figure out abstract setting in model
      ArtifactTypeManager.createType(guid, false, artifactType.getName());
   }

   private void handleAttributeType(AttributeType attributeType) throws OseeCoreException {
      OseeEnumType enumType = attributeType.getEnumType();

      int enumTypeId = OseeEnumTypeManager.getDefaultEnumTypeId();
      if (enumType != null) {
         List<Pair<String, Integer>> entries = new ArrayList<Pair<String, Integer>>();
         int lastOrdinal = 0;
         for (OseeEnumEntry enumEntry : enumType.getEnumEntries()) {
            String ordinal = enumEntry.getOrdinal();
            if (Strings.isValid(ordinal)) {
               lastOrdinal = Integer.parseInt(ordinal);
            }
            entries.add(new Pair<String, Integer>(enumEntry.getName(), lastOrdinal));
            lastOrdinal++;
         }
         enumTypeId = OseeEnumTypeManager.createEnumType(enumType.getName(), entries).getEnumTypeId();
      }

      AttributeTypeManager.createType(//
            attributeType.getBaseAttributeType(), // 
            attributeType.getDataProvider(), // 
            attributeType.getFileExtension(), //
            attributeType.getName(), //
            attributeType.getDefaultValue(), //
            String.valueOf(enumTypeId), //
            Integer.parseInt(attributeType.getMin()), //
            Integer.parseInt(attributeType.getMax()), //
            attributeType.getDescription(), //
            attributeType.getTaggerId());
   }

   private void handleRelationType(RelationType relationType) throws OseeCoreException {
      RelationTypeManager.createRelationType(relationType.getName(), //
            relationType.getSideAName(), //
            relationType.getSideBName(), //
            relationType.getSideAArtifactType().getName(), //
            relationType.getSideBArtifactType().getName(), //
            relationType.getMultiplicity().name(), //
            isOrdered(relationType.getDefaultOrderType()),//
            "");
   }

   private String isOrdered(String orderType) {
      String result = "No";
      if ("Unordered".equals(orderType)) {
         result = "Yes";
      }
      return result;
   }
}
