/*
 * Created on May 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.NetworkNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.LocalNewBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.remoteEvent.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.user.UserNotInDatabase;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;

/**
 * @author b1528444
 */
public class HttpBranchCreation {

   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final RemoteEventManager remoteEventManager = RemoteEventManager.getInstance();

   public static Branch createChildBranch(SkynetAuthentication skynetAuth, final TransactionId parentTransactionId, final String childBranchShortName, final String childBranchName, final Artifact associatedArtifact, boolean preserveMetaData, Collection<ArtifactSubtypeDescriptor> compressArtTypes, Collection<ArtifactSubtypeDescriptor> preserveArtTypes) throws Exception {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", childBranchName);
      parameters.put("function", "createChildBranch");
      parameters.put("authorId", Integer.toString(getAuthorId(skynetAuth)));
      parameters.put("associatedArtifactId", Integer.toString(getAssociatedArtifactId(skynetAuth, associatedArtifact)));
      parameters.put(
            "creationComment",
            BranchPersistenceManager.NEW_BRANCH_COMMENT + parentTransactionId.getBranch().getBranchName() + "(" + parentTransactionId.getTransactionNumber() + ")");
      if (childBranchShortName != null && childBranchShortName.length() > 0) {
         parameters.put("shortBranchName", childBranchShortName);
      }
      return commonServletBranchingCode(parameters);
   }

   /**
    * Creates a new root branch. Should NOT be used outside BranchPersistenceManager. If programatic access is
    * necessary, setting the staticBranchName will add a key for this branch and allow access to the branch through
    * getKeyedBranch(staticBranchName).
    * 
    * @param shortBranchName
    * @param branchName
    * @param staticBranchName null if no static key is desired
    * @return branch object
    * @throws SQLException
    * @throws UserNotInDatabase
    * @throws MultipleArtifactsExist
    * @see BranchPersistenceManager#createRootBranch(String, String, int)
    * @see BranchPersistenceManager#getKeyedBranch(String)
    */
   public static Branch createRootBranch(SkynetAuthentication skynetAuth, String shortBranchName, String branchName, String staticBranchName) throws SQLException, MultipleAttributesExist, IllegalArgumentException, UserNotInDatabase, MultipleArtifactsExist {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("branchName", branchName);
      parameters.put("function", "createRootBranch");
      parameters.put("authorId", "-1");
      parameters.put("associatedArtifactId", Integer.toString(getAssociatedArtifactId(skynetAuth, null)));
      parameters.put("creationComment", String.format("Root Branch [%s] Creation", branchName));
      if (shortBranchName != null && shortBranchName.length() > 0) {
         parameters.put("shortBranchName", shortBranchName);
      }
      if (staticBranchName != null && staticBranchName.length() > 0) {
         parameters.put("staticBranchName", staticBranchName);
      }
      return commonServletBranchingCode(parameters);
   }

   private static Branch commonServletBranchingCode(Map<String, String> parameters) {
      Branch branch = null;
      try {
         String response =
               HttpProcessor.post(new URL(HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("branch", parameters)));
         try {
            int branchId = Integer.parseInt(response);
            branch = BranchPersistenceManager.getInstance().getBranch(branchId);
         } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Unable to create branch. Error msg [%s]", response), ex);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class.getName(), Level.SEVERE, ex.toString(), ex);
         throw new IllegalArgumentException(ex);
      }
      eventManager.kick(new LocalNewBranchEvent(new Object(), branch.getBranchId()));
      remoteEventManager.kick(new NetworkNewBranchEvent(branch.getBranchId(),
            SkynetAuthentication.getInstance().getAuthenticatedUser().getArtId()));
      return branch;
   }

   private static int getAssociatedArtifactId(SkynetAuthentication skynetAuth, Artifact associatedArtifact) throws MultipleAttributesExist, UserNotInDatabase, MultipleArtifactsExist, SQLException {
      int associatedArtifactId = -1;
      if (associatedArtifact == null && !SkynetDbInit.isDbInit()) {
         associatedArtifact = skynetAuth.getUser(UserEnum.NoOne);
      }
      if (associatedArtifact != null) {
         associatedArtifactId = associatedArtifact.getArtId();
      }
      return associatedArtifactId;
   }

   private static int getAuthorId(SkynetAuthentication skynetAuth) throws MultipleAttributesExist, UserNotInDatabase, MultipleArtifactsExist, SQLException {
      if (SkynetDbInit.isDbInit()) {
         return -1;
      }
      User userToBlame = skynetAuth.getAuthenticatedUser();
      return (userToBlame == null) ? skynetAuth.getUser(UserEnum.NoOne).getArtId() : userToBlame.getArtId();
   }
}
