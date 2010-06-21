/*
 * Created on Jun 18, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.message.test.mocks;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.message.ArtifactChangeItem;
import org.eclipse.osee.framework.core.message.BranchCreationRequest;
import org.eclipse.osee.framework.core.message.BranchCreationResponse;
import org.eclipse.osee.framework.core.message.CacheUpdateRequest;
import org.eclipse.osee.framework.core.message.ChangeVersion;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.OseeModelFactoryService;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public final class MockRequestFactory {
   private MockRequestFactory() {
   }

   public static IOseeModelFactoryServiceProvider createFactoryProvider() {
      return new MockOseeModelFactoryServiceProvider(createFactoryService());
   }

   private static IOseeModelFactoryService createFactoryService() {
      return new OseeModelFactoryService(new BranchFactory(), new TransactionRecordFactory(),
            new ArtifactTypeFactory(), new AttributeTypeFactory(), new RelationTypeFactory(), new OseeEnumTypeFactory());
   }

   public static ArtifactChangeItem createArtifactChangeItem() throws OseeArgumentException {
      int artId = (int) Math.random();
      Long gammaIdNumber = Long.valueOf((int) Math.random());
      int artTypeId = artId * 10;
      ArtifactChangeItem changeItem =
            new ArtifactChangeItem(artId, artTypeId, gammaIdNumber, ModificationType.getMod(1));
      populateChangeVersion(changeItem.getDestinationVersion(), 22);
      populateChangeVersion(changeItem.getCurrentVersion(), 15);
      return changeItem;
   }

   public static ChangeVersion createChangeVersion(int index) {
      ModificationType modType = ModificationType.values()[index % ModificationType.values().length];
      return new ChangeVersion("change_version_value_" + index, (long) (index * Integer.MAX_VALUE), modType);
   }

   public static void populateChangeVersion(ChangeVersion changeVersion, int index) {
      ModificationType modType = ModificationType.values()[index % ModificationType.values().length];
      changeVersion.setGammaId((long) (index * Integer.MAX_VALUE));
      changeVersion.setModType(modType);
      changeVersion.setValue("change_version_value_" + index);
   }

   public static CacheUpdateRequest createRequest(int index) {
      OseeCacheEnum cacheEnum = OseeCacheEnum.values()[Math.abs(index % OseeCacheEnum.values().length)];
      List<Integer> guids = new ArrayList<Integer>();
      for (int j = 1; j <= index * 3; j++) {
         guids.add(j * index);
      }
      return new CacheUpdateRequest(cacheEnum, guids);
   }

   public static BranchCreationRequest createBranchCreateRequest(int index) {
      BranchType branchType = BranchType.values()[Math.abs(index % BranchType.values().length)];
      String branchName = "branch_" + index;
      int parentBranchId = index;
      int associatedArtifactId = index * 3;
      int sourceTransactionId = index * 7;
      String branchGuid = GUID.create();

      int authorId = index * 7;

      String creationComment = "creation_comment_" + index;

      int populateBaseTxFromAddressingQueryId = -1;
      int destinationBranchId = -1;

      return new BranchCreationRequest(branchType, sourceTransactionId, parentBranchId, branchGuid, branchName,
            associatedArtifactId, authorId, creationComment, populateBaseTxFromAddressingQueryId, destinationBranchId);
   }

   public static Object createBranchCreateResponse(int index) {
      return new BranchCreationResponse(index);
   }
}
