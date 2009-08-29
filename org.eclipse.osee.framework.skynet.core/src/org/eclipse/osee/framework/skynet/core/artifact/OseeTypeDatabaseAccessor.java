package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidInheritanceException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.SequenceManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

final class OseeTypeDatabaseAccessor implements IOseeTypeDataAccessor {
   private static final int ABSTRACT_TYPE_INDICATOR = 1;
   private static final int CONCRETE_TYPE_INDICATOR = 0;
   private static final int NULL_SUPER_ARTIFACT_TYPE = -1;

   private static final String SELECT_ARTIFACT_TYPES = "select * from osee_artifact_type";

   private static final String INSERT_ARTIFACT_TYPE =
         "insert into osee_artifact_type (art_type_id, name, is_abstract) VALUES (?,?,?)";

   private static final String SELECT_ARTIFACT_TYPE_INHERITANCE =
         "select * from osee_artifact_type_inheritance order by super_art_type_id, art_type_id";

   private static final String INSERT_ARTIFACT_TYPE_INHERITANCE =
         "insert into osee_artifact_type_inheritance (art_type_id, super_art_type_id) VALUES (?,?)";

   private static final String SELECT_ATTRIBUTE_VALIDITY = "SELECT * FROM osee_valid_attributes";
   private static final String INSERT_VALID_ATTRIBUTE =
         "INSERT INTO osee_valid_attributes (art_type_id, attr_type_id, branch_id) VALUES (?, ?, ?)";

   private final OseeTypeCache oseeTypeCache;
   private final IOseeTypeFactory artifactTypeFactory;

   public OseeTypeDatabaseAccessor(OseeTypeCache oseeTypeCache, IOseeTypeFactory artifactTypeFactory) {
      this.oseeTypeCache = oseeTypeCache;
      this.artifactTypeFactory = artifactTypeFactory;
   }

   @Override
   synchronized public void ensureArtifactTypePopulated() throws OseeCoreException {
      if (!oseeTypeCache.areArtifactTypesAvailable()) {
         loadArtifactTypeCache();
      }
   }

   @Override
   synchronized public void ensureTypeValidityPopulated() throws OseeCoreException {
      if (!oseeTypeCache.isTypeValidityAvailable()) {
         loadTypeValidityCache();
      }
   }

   @Override
   public void storeTypeInheritance(List<Object[]> datas) throws OseeCoreException {
      ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE_INHERITANCE, datas);
   }

   @Override
   public void storeValidity(List<Object[]> datas) throws OseeCoreException {
      ConnectionHandler.runBatchUpdate(INSERT_VALID_ATTRIBUTE, datas);
   }

   @Override
   public void storeArtifactType(ArtifactType... artifactTypes) throws OseeCoreException {
      if (artifactTypes != null) {
         if (artifactTypes.length == 1) {
            ArtifactType type = artifactTypes[0];
            ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_TYPE, type.getArtTypeId(), type.getName(),
                  type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR);
         } else {
            List<Object[]> types = new ArrayList<Object[]>();
            for (ArtifactType type : artifactTypes) {
               types.add(new Object[] {type.getArtTypeId(), type.getName(),
                     type.isAbstract() ? ABSTRACT_TYPE_INDICATOR : CONCRETE_TYPE_INDICATOR});
            }
            ConnectionHandler.runBatchUpdate(INSERT_ARTIFACT_TYPE, types);
         }
      }
   }

   private void loadTypeValidityCache() throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(2000, SELECT_ATTRIBUTE_VALIDITY);
         while (chStmt.next()) {
            try {
               ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
               AttributeType attributeType = AttributeTypeManager.getType(chStmt.getInt("attr_type_id"));
               Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
               oseeTypeCache.cacheTypeValidity(artifactType, attributeType, branch);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadArtifactTypeCache() throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_ARTIFACT_TYPES);

         while (chStmt.next()) {
            try {
               boolean isAbstract = chStmt.getInt("is_abstract") == ABSTRACT_TYPE_INDICATOR;
               artifactTypeFactory.createArtifactType(chStmt.getInt("art_type_id"), chStmt.getString("guid"),
                     isAbstract, chStmt.getString("name"));
            } catch (OseeDataStoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      } finally {
         chStmt.close();
      }

      ConnectionHandlerStatement chStmt2 = new ConnectionHandlerStatement();
      try {
         chStmt2.runPreparedQuery(SELECT_ARTIFACT_TYPE_INHERITANCE);
         while (chStmt2.next()) {
            int artTypeId = chStmt.getInt("art_type_id");
            int superArtTypeId = chStmt.getInt("super_art_type_id");

            ArtifactType superArtifactType = null;
            if (superArtTypeId != NULL_SUPER_ARTIFACT_TYPE) {
               superArtifactType = oseeTypeCache.getArtifactTypeById(artTypeId);
            }
            ArtifactType artifactType = oseeTypeCache.getArtifactTypeById(artTypeId);
            if (artifactType == null) {
               throw new OseeInvalidInheritanceException(String.format("ArtifactType [%s] inherit from [%s] is null",
                     artTypeId, superArtTypeId));
            }
            if (artTypeId == superArtTypeId) {
               throw new OseeInvalidInheritanceException(String.format(
                     "Circular inheritance detected artifact type [%s] inherits from [%s]", artTypeId, superArtTypeId));
            }
            oseeTypeCache.cacheArtifactTypeInheritance(artifactType, superArtifactType);
         }
      } finally {
         chStmt2.close();
      }
   }

   @Override
   public ArtifactType createArtifactType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException {
      int artTypeId = SequenceManager.getNextArtifactTypeId();
      return artifactTypeFactory.createArtifactType(artTypeId, guid, isAbstract, artifactTypeName);
   }

}
