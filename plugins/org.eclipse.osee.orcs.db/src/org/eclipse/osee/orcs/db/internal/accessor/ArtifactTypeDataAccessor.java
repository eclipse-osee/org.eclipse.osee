/*
 * Created on Aug 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.internal.accessor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.services.IOseeModelingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OseeApi;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.ResultSet;

public class ArtifactTypeDataAccessor<T extends AbstractOseeType<Long>> implements IOseeDataAccessor<Long, T> {

   private static volatile boolean wasLoaded;

   private final IOseeModelingService modelingService;
   private final IdentityService identityService;

   private OseeApi oseeApi;

   public ArtifactTypeDataAccessor(IOseeModelingService modelingService, IdentityService identityService) {
      this.modelingService = modelingService;
      this.identityService = identityService;
   }

   @Override
   public synchronized void load(IOseeCache<Long, T> cache) throws OseeCoreException {
      if (!wasLoaded) {
         wasLoaded = true;
         String modelData = getTypeData();
         if (Strings.isValid(modelData)) {
            String modelName = String.format("osee.types.%s.osee", Lib.getDateTimeString());

            OseeImportModelRequest request = new OseeImportModelRequest(modelName, modelData, false, false, true);
            OseeImportModelResponse response = new OseeImportModelResponse();
            modelingService.importOseeTypes(new NullProgressMonitor(), true, request, response);
         }
      }
   }

   @Override
   public void store(Collection<T> types) throws OseeCoreException {
      Collection<Long> remoteIds = new ArrayList<Long>();
      for (T type : types) {
         remoteIds.add(type.getGuid());
      }
      identityService.store(remoteIds);
      for (T type : types) {
         type.setId(identityService.getLocalId(type.getGuid()));
         type.clearDirty();
      }
   }

   private String getTypeData() throws OseeCoreException {
      QueryFactory factory = oseeApi.getQueryFactory(null);
      ResultSet<ReadableArtifact> result =
         factory.fromBranch(CoreBranches.COMMON).and(CoreArtifactTypes.OseeTypeDefinition).build(LoadLevel.ATTRIBUTE);

      StringWriter writer = new StringWriter();
      for (ReadableArtifact artifact : result.getList()) {
         String oseeTypeFragment = artifact.getSoleAttributeAsString(CoreAttributeTypes.UriGeneralStringData);
         oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
         writer.write("\n");
         writer.write("//////////////     ");
         writer.write(artifact.getName());
         writer.write("\n");
         writer.write("\n");
         writer.write(oseeTypeFragment);
      }
      return writer.toString();
   }
}
