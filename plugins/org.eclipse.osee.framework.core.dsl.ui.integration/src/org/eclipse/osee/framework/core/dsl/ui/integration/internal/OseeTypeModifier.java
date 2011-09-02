package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.AttributeModifier;

public class OseeTypeModifier implements AttributeModifier {

   @Override
   public String modifyForSave(Artifact owner, String value) throws OseeCoreException {
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.OseeTypeDefinition, BranchManager.getCommonBranch());
      StringBuilder combinedSheets = new StringBuilder();
      for (Artifact art : artifacts) {
         String sheetData;
         if (art.equals(owner)) {
            sheetData = value;
         } else {
            sheetData = art.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
         }
         combinedSheets.append(sheetData.replaceAll("import\\s+\"", "// import \""));
      }
      OseeDsl oseeDsl = ModelUtil.loadModel("osee:/TypeModel.osee", combinedSheets.toString());

      Set<Long> uuids = new HashSet<Long>();
      for (EObject object : oseeDsl.eContents()) {
         if (object instanceof OseeType) {
            addUuid(uuids, (OseeType) object);
         }
      }
      Conditions.checkExpressionFailOnTrue(uuids.contains(0L), "Uuid of 0L is not allowed");
      return value;
   }

   private void addUuid(Set<Long> set, OseeType type) throws OseeCoreException {
      Long uuid = HexUtil.toLong(type.getUuid());
      boolean wasAdded = set.add(uuid);
      Conditions.checkExpressionFailOnTrue(!wasAdded, "Duplicate uuid found: [0x%X]", uuid);
   }
}