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

package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.IAttributeChange;
import org.eclipse.osee.framework.skynet.core.revision.IRevisionChange;
import org.eclipse.osee.framework.skynet.core.revision.RelationLinkChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ShowAttributeAction;
import org.eclipse.osee.framework.ui.swt.ITreeNode;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class BranchLabelProvider implements ITableLabelProvider, ITableColorProvider, ILabelProvider {
   private static Image branchImage = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static Image changeManagedBranchImage = SkynetGuiPlugin.getInstance().getImage("change_managed_branch.gif");

   private static Image favoriteBranchImage = null;
   private static Image defaultBranchImage = null;
   private static Image favoriteDefaultBranchImage = null;

   private static Image favoriteDefaultChangedManagedBranchImage = null;
   private static Image defaultChangeManagedBranchImage = null;
   private static Image favoriteChangeManagedBranchImage = null;
   private boolean showChangeType = false;

   private final ShowAttributeAction attributeAction;
   private Collection<Integer> attributeModifiedArtifactIds;

   public BranchLabelProvider() {
      this(null);
   }

   public BranchLabelProvider(ShowAttributeAction attributeAction) {
      this.attributeAction = attributeAction;
   }

   @SuppressWarnings("unchecked")
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof ITreeNode) {
         element = ((ITreeNode) element).getBackingData();
      }

      // Seek down through aggregation lists to the lowest level to get an actual element
      while (element instanceof List && !((List) element).isEmpty()) {
         element = ((List) element).get(0);
      }

      if (element instanceof Branch && columnIndex == 0) {
         try {
            checkImages();
            Branch branch = (Branch) element;
            boolean favorite = UserManager.getUser().isFavoriteBranch(branch);
            boolean action = branch.isChangeManaged();
            boolean isDefault = element.equals(BranchManager.getDefaultBranch());

            if (favorite && action) {
               return isDefault ? favoriteDefaultChangedManagedBranchImage : favoriteChangeManagedBranchImage;
            } else if (favorite) {
               return isDefault ? favoriteDefaultBranchImage : favoriteBranchImage;
            } else if (action) {
               return isDefault ? defaultChangeManagedBranchImage : changeManagedBranchImage;
            } else {
               return isDefault ? defaultBranchImage : branchImage;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }

      } else if (element instanceof TransactionId && columnIndex == 0) {
         return SkynetGuiPlugin.getInstance().getImage("DBiconBlue.GIF");

      } else if (element instanceof RelationLinkChange && columnIndex == 2) {
         ArtifactType descriptor = ((RelationLinkChange) element).getOtherArtifactDescriptor();
         if (descriptor == null)
            return null;
         else
            return descriptor.getImage();

      } else if (element instanceof IRevisionChange && columnIndex == 0) {
         return ((IRevisionChange) element).getImage();
      }

      return null;
   }

   private static synchronized void checkImages() {
      if (defaultBranchImage == null) {
         favoriteBranchImage =
               new OverlayImage(branchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("star_9_9.gif"), 0, 7).createImage();
         defaultBranchImage =
               new OverlayImage(branchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("black_check.gif"), 8, 0).createImage();
         favoriteDefaultBranchImage =
               new OverlayImage(defaultBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor("star_9_9.gif"),
                     0, 7).createImage();

         defaultChangeManagedBranchImage =
               new OverlayImage(changeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "black_check.gif"), 8, 0).createImage();
         favoriteChangeManagedBranchImage =
               new OverlayImage(changeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "star_9_9.gif"), 0, 7).createImage();
         favoriteDefaultChangedManagedBranchImage =
               new OverlayImage(defaultChangeManagedBranchImage, SkynetGuiPlugin.getInstance().getImageDescriptor(
                     "star_9_9.gif"), 0, 7).createImage();

      }
   }

   /**
    * @return Returns the text for a specific column
    */
   public String getColumnText(Object element, int columnIndex) {
      return getColumnTextLabel(element, columnIndex);
   }

   @SuppressWarnings("unchecked")
   public String getColumnTextLabel(Object element, int columnIndex) {
      if (element instanceof ITreeNode) element = ((ITreeNode) element).getBackingData();

      if (element instanceof Branch) {
         Branch branch = (Branch) element;

         if (columnIndex == 0) {
            try {
               if (AccessControlManager.isOseeAdmin()) {
                  return "(" + branch.getBranchId() + ") " + branch.getBranchName();
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
            return branch.getBranchName();
         } else if (columnIndex == 1) {
            return branch.getDisplayName();
         } else if (columnIndex == 2) {
            return String.valueOf(branch.getCreationDate());
         } else if (columnIndex == 3) {
            try {
               return UserManager.getUserNameById(branch.getAuthorId());
            } catch (Exception ex) {
               return "";
            }
         } else if (columnIndex == 4) {
            return branch.getCreationComment();
         }
      } else if (element instanceof TransactionId) {
         TransactionId transactionData = (TransactionId) element;

         if (columnIndex == 0) {
            return String.valueOf(transactionData.getTransactionNumber());
         } else if (columnIndex == 2) {
            return String.valueOf(transactionData.getTime());
         } else if (columnIndex == 3) {
            return String.valueOf(UserManager.getUserNameById(transactionData.getAuthorArtId()));
         } else if (columnIndex == 4) {
            return transactionData.getComment();
         }
      } else if (element instanceof Collection) {
         Object headCursor = element;
         Object tailCursor = element;

         while (headCursor instanceof List && !((List) headCursor).isEmpty()) {
            headCursor = ((List) headCursor).get(0);
         }
         while (tailCursor instanceof List && !((List) tailCursor).isEmpty()) {
            List list = (List) tailCursor;
            tailCursor = list.get(list.size() - 1);
         }

         if (headCursor instanceof TransactionId && tailCursor instanceof TransactionId) {
            TransactionId headTransaction = (TransactionId) headCursor;
            TransactionId tailTransaction = (TransactionId) tailCursor;

            if (columnIndex == 0) {
               return String.valueOf(headTransaction.getTransactionNumber() + "..." + tailTransaction.getTransactionNumber());
            } else if (columnIndex == 2) {
               return String.valueOf(headTransaction.getTime());
            }
         } else {
            return "Unexpected aggregation of " + headCursor.getClass().getSimpleName() + " and " + tailCursor.getClass().getSimpleName();
         }

      } else if (element instanceof ArtifactChange) {
         if (columnIndex == 0) {
            ArtifactChange artifactChange = (ArtifactChange) element;
            try {
               String txt =
                     artifactChange.getName() + (showChangeType ? " (" + getChangeType(artifactChange) + ")" : "");
               if (artifactChange.getModType() == ModificationType.DELETED && artifactChange.getChangeType() == ChangeType.INCOMING) {
                  txt = "Artifact Deleted";
               } else {
                  if (attributeAction != null && !attributeAction.noneSelected()) {
                     String attributeText = "";
                     try {
                        attributeText = attributeAction.getSelectedAttributeData(artifactChange.getArtifact());
                        if (attributeText != null) {
                           return txt + attributeText;
                        }
                     } catch (Exception ex) {
                        OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                        return txt + ex.getLocalizedMessage();
                     }
                  }
               }
               return txt;
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               return ex.getLocalizedMessage();
            }
         }
      } else if (element instanceof IAttributeChange) {
         IAttributeChange change = (IAttributeChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getName();
         } else if (columnIndex == 2) {
            return "was:" + change.getWasValue();
         } else if (columnIndex == 3) {
            return "is:" + change.getChange();
         }
      } else if (element instanceof RelationLinkChange) {
         RelationLinkChange change = (RelationLinkChange) element;

         if (columnIndex == 0) {
            return String.valueOf(change.getGammaId());
         } else if (columnIndex == 1) {
            return change.getRelTypeName();
         } else if (columnIndex == 2) {
            return change.getOtherArtifactName();
         } else if (columnIndex == 3) {
            return change.getRationale();
         }
      } else if (columnIndex == 0) {
         return element.toString();
      }
      return "";
   }

   private String getChangeType(ArtifactChange artifactChange) throws OseeCoreException {
      // Compare using artids cause a historical artifact is NOT equal to a current artifact
      if ((artifactChange.getModType() == ModificationType.CHANGE) && attributeModifiedArtifactIds != null && !attributeModifiedArtifactIds.contains(artifactChange.getArtifact().getArtId())) return artifactChange.getModType().getDisplayName() + " Relation Only";
      return artifactChange.getModType().getDisplayName();
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      return getColumnImage(element, 0);
   }

   /**
    * @return returns the text for the first column as default
    */
   public String getText(Object element) {
      return getColumnText(element, 0);
   }

   public Color getForeground(Object element, int columnIndex) {
      return null;
   }

   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

   /**
    * @param branch
    * @return Returns the image for a branch.
    */
   public static Image getBranchImage(Branch branch) {
      if (branch == null) {
         throw new IllegalArgumentException("The branch can not be null.");
      }

      return branch.isChangeManaged() ? changeManagedBranchImage : branchImage;
   }

   /**
    * @return the showChangeType
    */
   public boolean isShowChangeType() {
      return showChangeType;
   }

   /**
    * @param showChangeType the showChangeType to set
    */
   public void setShowChangeType(boolean showChangeType, Collection<Integer> attributeModifiedArtifactIds) {
      this.showChangeType = showChangeType;
      this.attributeModifiedArtifactIds = attributeModifiedArtifactIds;
   }
}
