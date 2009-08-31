package org.eclipse.osee.framework.types.bridge.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.AttributeTypeRef;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

public class XTextToOseeTypeOperation extends AbstractOperation {
   private final java.net.URI resource;

   public XTextToOseeTypeOperation(java.net.URI resource) {
      super("OSEE Text Model to OSEE", Activator.PLUGIN_ID);
      this.resource = resource;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      OseeTypeModel model = OseeTypeModelUtil.loadModel(resource);
      //      for (Import importEntry : model.getImports()) {
      //         System.out.println("Import: " + importEntry.getImportURI());
      //         OseeTypeModel importedModel = OseeTypeModelUtil.loadModel(new URI(importEntry.getImportURI()));
      //      }
      if (!model.getTypes().isEmpty()) {
         double workPercentage = 1.0 / model.getTypes().size() * 2.0;

         for (OseeType type : model.getTypes()) {
            if (type instanceof ArtifactType) {
               handleArtifactType((ArtifactType) type);
            } else if (type instanceof AttributeType) {
               handleAttributeType((AttributeType) type);
            }
            monitor.worked(calculateWork(workPercentage));
         }

         // second pass to handle cross references
         for (OseeType type1 : model.getTypes()) {
            if (type1 instanceof ArtifactType) {
               handleArtifactTypeCrossRef((ArtifactType) type1);
            } else if (type1 instanceof RelationType) {
               handleRelationType((RelationType) type1);
            }
            monitor.worked(calculateWork(workPercentage));
         }
      }
      ArtifactTypeManager.persist();
   }

   /**
    * @param type
    * @throws OseeCoreException
    */
   private void handleArtifactTypeCrossRef(ArtifactType artifactType) throws OseeCoreException {
      Set<org.eclipse.osee.framework.skynet.core.artifact.ArtifactType> superTypes =
            new HashSet<org.eclipse.osee.framework.skynet.core.artifact.ArtifactType>();
      org.eclipse.osee.framework.skynet.core.artifact.ArtifactType targetArtifactType =
            ArtifactTypeManager.getTypeByGuid(artifactType.getTypeGuid());

      for (ArtifactType superType : artifactType.getSuperArtifactTypes()) {
         superTypes.add(ArtifactTypeManager.getTypeByGuid(superType.getTypeGuid()));
      }
      targetArtifactType.addSuperType(superTypes);

      HashCollection<Branch, org.eclipse.osee.framework.skynet.core.attribute.AttributeType> items =
            new HashCollection<Branch, org.eclipse.osee.framework.skynet.core.attribute.AttributeType>();
      for (AttributeTypeRef attributeTypeRef : artifactType.getValidAttributeTypes()) {
         AttributeType attributeType = attributeTypeRef.getValidAttributeType();
         Branch branch;
         String branchGuid = attributeTypeRef.getBranchGuid();
         if (branchGuid == null) {
            branch = BranchManager.getSystemRootBranch();
         } else {
            branch = BranchManager.getBranchByGuid(branchGuid);
         }
         items.put(branch, AttributeTypeManager.getTypeByGuid(attributeType.getTypeGuid()));
      }

      for (Branch branch : items.keySet()) {
         ArtifactTypeManager.setAttributeTypes(targetArtifactType, items.getValues(), branch);
      }
   }

   private void handleArtifactType(ArtifactType artifactType) throws OseeCoreException {
      String artifactTypeName = artifactType.getName().substring(1, artifactType.getName().length() - 1); // strip off enclosing quotes
      artifactType.setTypeGuid(ArtifactTypeManager.createType(artifactType.getTypeGuid(), artifactType.isAbstract(),
            artifactTypeName).getGuid());
   }

   private org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType getOseeEnumTypes(OseeEnumType enumType) throws OseeCoreException {
      org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType toReturn = null;
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
         toReturn = OseeEnumTypeManager.createEnumType(enumType.getTypeGuid(), enumType.getName());
         if (toReturn != null) {
            toReturn.addEntries(entries);
            enumType.setTypeGuid(toReturn.getGuid());
         }
      }
      return toReturn;
   }

   private void handleAttributeType(AttributeType attributeType) throws OseeCoreException {
      int max = Integer.MAX_VALUE;
      if (!attributeType.getMax().equals("unlimited")) {
         max = Integer.parseInt(attributeType.getMax());
      }
      attributeType.setTypeGuid(AttributeTypeManager.createType(attributeType.getTypeGuid(), //
            attributeType.getName(), //
            attributeType.getBaseAttributeType(), // 
            attributeType.getDataProvider(), // 
            attributeType.getFileExtension(), //
            attributeType.getDefaultValue(), //
            getOseeEnumTypes(attributeType.getEnumType()), //
            Integer.parseInt(attributeType.getMin()), //
            max, //
            attributeType.getDescription(), //
            attributeType.getTaggerId()).getGuid());
   }

   private void handleRelationType(RelationType relationType) throws OseeCoreException {
      RelationTypeMultiplicity multiplicity =
            RelationTypeMultiplicity.getFromString(relationType.getMultiplicity().name());
      relationType.setTypeGuid(RelationTypeManager.createRelationType(relationType.getTypeGuid(),
            relationType.getName(), //
            relationType.getSideAName(), //
            relationType.getSideBName(), //
            ArtifactTypeManager.getType(relationType.getSideAArtifactType().getName()), //
            ArtifactTypeManager.getType(relationType.getSideBArtifactType().getName()), //
            multiplicity, //
            isOrdered(relationType.getDefaultOrderType()),//
            ""// TODO missing defaultOrderGUID
      ).getGuid());
   }

   private boolean isOrdered(String orderType) {
      return "Unordered".equalsIgnoreCase(orderType);
   }
}
