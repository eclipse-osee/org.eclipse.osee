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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.revision.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.branch.SnapshotDescription;
import org.eclipse.osee.framework.ui.swt.TreeNode;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * This is a utility class for OSEE handlers
 * 
 * @author Jeff C. Phillips
 */
public class Handlers {

   /**
    * Populates a list of TransactionData from a IStructuredSelection. Returns an empty list if no TransactionData were
    * found.
    * 
    * @param selection
    * @return
    */
   public static List<TransactionData> getTransactionDatasFromStructuredSelection(IStructuredSelection structuredSelection) {
      return (List<TransactionData>) processSelectionObjects(TransactionData.class, structuredSelection);
   }

   /**
    * Populates a list of ArtifactChange from a IStructuredSelection. Returns an empty list if no ArtifactChange were
    * found.
    * 
    * @param selection
    * @return
    */
   public static List<ArtifactChange> getArtifactChangesFromStructuredSelection(IStructuredSelection structuredSelection) {
      return (List<ArtifactChange>) processSelectionObjects(ArtifactChange.class, structuredSelection);
   }

   /**
    * Populates a list of branches from a IStructuredSelection. Returns an empty list if no branches were found.
    * 
    * @param selection
    * @return
    */
   public static List<Branch> getBranchesFromStructuredSelection(IStructuredSelection structuredSelection) {
      return (List<Branch>) processSelectionObjects(Branch.class, structuredSelection);
   }

   /**
    * Populates a list of artifacts from a IStructuredSelection. Returns an empty list if no artifacts were found.
    * 
    * @param selection
    * @return
    */
   public static List<Artifact> getArtifactsFromStructuredSelection(IStructuredSelection structuredSelection) {
      return (List<Artifact>) processSelectionObjects(Artifact.class, structuredSelection);
   }

   /**
    * @param clazz
    * @param structuredSelection
    * @return Returns a list of objects from the sturctruedSelection that are an instance of the Class
    */
   private static List<?> processSelectionObjects(Class<?> clazz, IStructuredSelection structuredSelection) {
      List<Object> objects = new LinkedList<Object>();
      Iterator<?> iterator = structuredSelection.iterator();

      while (iterator.hasNext()) {
         Object object = iterator.next();
         Object targetObject = null;

         if (object instanceof IAdaptable) {
            targetObject = ((IAdaptable) object).getAdapter(clazz);
         } else if (object instanceof Match) {
            targetObject = ((Match) object).getElement();
         }

         if (clazz.isInstance(targetObject)) {
            objects.add(targetObject);
         }
      }
      return objects;
   }

   //////////////////////////////////////////////////////////

   /**
    * Populates a list of branches from a IStructuredSelection. Returns an empty list if no branches were found.
    * 
    * @param selection
    * @return
    */
   public static List<Object> getBranchesAndArtifactsFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Branch> myBranches = new LinkedList<Branch>();
      List<Artifact> myArtifacts = new LinkedList<Artifact>();
      List<Integer> myArtifactIDs = new LinkedList<Integer>();
      List<ArtifactChange> mySelectedArtifactChangeList = new LinkedList<ArtifactChange>();
      List<ChangeReportInput> myChangeReportInputNewList = new LinkedList<ChangeReportInput>();
      List<ChangeReportInput> myChangeReportInputOldList = new LinkedList<ChangeReportInput>();
      List<Object> myObjects = new LinkedList<Object>();

      myObjects.add(myBranches);
      myObjects.add(myArtifacts);
      myObjects.add(myArtifactIDs);
      myObjects.add(mySelectedArtifactChangeList);
      myObjects.add(myChangeReportInputNewList);
      myObjects.add(myChangeReportInputOldList);

      if (myIStructuredSelection instanceof TreeSelection) {
         // System.out.println("myIStructuredSelection instanceof TreeSelection");
         TreeSelection myTreeSelection = (TreeSelection) myIStructuredSelection;
         TreePath[] myTreePaths = myTreeSelection.getPaths();
         for (TreePath treePath : myTreePaths) {
            for (int i = 0; i < treePath.getSegmentCount(); i++) {
               Object segmentObject = treePath.getSegment(i);
               if (segmentObject instanceof TreeNode) {
                  TreeNode myTreeNode = (TreeNode) segmentObject;
                  Object myBackingDataObject = myTreeNode.getBackingData();
                  if (myBackingDataObject instanceof SnapshotDescription) {
                     SnapshotDescription mySnapshotDescription = (SnapshotDescription) myBackingDataObject;
                     ChangeReportInput myChangeReportInputNew = mySnapshotDescription.getNewInput();
                     myChangeReportInputNew.getToTransaction();

                     // TransactionId myTransactionNewId =
                     // myChangeReportInputNew.getBaseTransaction();
                     myChangeReportInputNewList.add(myChangeReportInputNew);
                     ChangeReportInput myChangeReportInputOld = mySnapshotDescription.getOldInput();

                     myChangeReportInputOld.getBaseParentTransactionId();
                     // TransactionId myTransactionOldId =
                     // myChangeReportInputOld.getBaseTransaction();
                     myChangeReportInputOldList.add(myChangeReportInputOld);
                  }
                  if (myBackingDataObject instanceof ArtifactChange) {
                     // System.out.println("myBackingDataObject instanceof ArtifactChange");
                     ArtifactChange myArtifactChange = (ArtifactChange) myBackingDataObject;
                     // myArtifactChange.getArtId();
                     // System.out.println("myArtifactChange.getArtId " +
                     myArtifactIDs.add(new Integer(myArtifactChange.getArtId()));
                     mySelectedArtifactChangeList.add(myArtifactChange);
                     try {
                        myArtifacts.add(myArtifactChange.getArtifact());
                        myBranches.add(myArtifactChange.getArtifact().getBranch());
                     } catch (SQLException ex) {
                     }
                  }
                  if (myBackingDataObject instanceof Artifact) {
                     // System.out.println("myBackingDataObject instanceof ArtifactChange");
                     Artifact myArtifact = (Artifact) myBackingDataObject;
                     myArtifactIDs.add(new Integer(myArtifact.getArtId()));
                     myArtifacts.add(myArtifact);
                     myBranches.add(myArtifact.getBranch());

                  }

               }
            }
         }
      }
      Iterator<?> iterator = myIStructuredSelection.iterator();

      while (iterator.hasNext()) {
         Object object = iterator.next();
         Object selectionObject = null;

         if (object instanceof IAdaptable) {
            selectionObject = ((IAdaptable) object).getAdapter(Branch.class);
         } else if (object instanceof Match) {
            selectionObject = ((Match) object).getElement();
         }

         if (selectionObject instanceof Branch) {
            myBranches.add((Branch) selectionObject);
         }
      }
      Object selectionObject = null;

      // Iterator<?> iterator = myIStructuredSelection.iterator();
      while (iterator.hasNext()) {
         Object object = iterator.next();

         if (object instanceof IAdaptable) {
            selectionObject = ((IAdaptable) object).getAdapter(Branch.class);

            if (selectionObject == null) {
               selectionObject = ((IAdaptable) object).getAdapter(Artifact.class);
            }
         } else if (object instanceof Match) {
            selectionObject = ((Match) object).getElement();
         }

         if (selectionObject instanceof Branch) {
            myBranches.add(((Branch) selectionObject));
         } else if (selectionObject instanceof Artifact) {
            Artifact myArtifact = (Artifact) selectionObject;
            // names.add(artifact.getDescriptiveName());
            myArtifacts.add(myArtifact);
            myArtifactIDs.add(new Integer(myArtifact.getArtId()));

         }
      }

      return myObjects;
   }

   public static List<TransactionData> getTransactionDataNeededFromStructuredSelection(IStructuredSelection selection) {
      List<TransactionData> myTransactionDataList = new LinkedList<TransactionData>();
      Iterator<?> iterator = selection.iterator();

      while (iterator.hasNext()) {
         Object object = iterator.next();
         if (object instanceof TreeNode) {
            TreeNode myTreeNode = (TreeNode) object;
            object = myTreeNode.getBackingData();
         }
         Object selectionObject = null;

         if (object instanceof IAdaptable) {
            selectionObject = ((IAdaptable) object).getAdapter(TransactionData.class);
         } else if (object instanceof Match) {
            selectionObject = ((Match) object).getElement();
         }

         if (selectionObject instanceof TransactionData) {
            myTransactionDataList.add((TransactionData) selectionObject);
         }
      }
      return myTransactionDataList;
   }

   public static List<ArtifactChange> getArtifactChangeFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<ArtifactChange> myArtifactChangeList = new LinkedList<ArtifactChange>();
      if (myIStructuredSelection instanceof TreeSelection) {
         // System.out.println("myIStructuredSelection instanceof TreeSelection");
         TreeSelection myTreeSelection = (TreeSelection) myIStructuredSelection;
         TreePath[] myTreePaths = myTreeSelection.getPaths();
         for (TreePath treePath : myTreePaths) {
            for (int i = 0; i < treePath.getSegmentCount(); i++) {
               Object segmentObject = treePath.getSegment(i);
               if (segmentObject instanceof TreeNode) {
                  TreeNode myTreeNode = (TreeNode) segmentObject;
                  Object myBackingDataObject = myTreeNode.getBackingData();
                  if (myBackingDataObject instanceof ArtifactChange) {
                     // System.out.println("myBackingDataObject instanceof ArtifactChange");
                     ArtifactChange myArtifactChange = (ArtifactChange) myBackingDataObject;
                     myArtifactChange.getArtId();
                     // System.out.println("myArtifactChange.getArtId " +
                     // myArtifactChange.getArtId());
                     myArtifactChangeList.add(myArtifactChange);
                  }
               }
            }
         }

      }

      Iterator<?> iterator = myIStructuredSelection.iterator();

      while (iterator.hasNext()) {
         Object object = iterator.next();
         if (object instanceof TreeNode) {
            TreeNode myTreeNode = (TreeNode) object;
            object = myTreeNode.getBackingData();
         }
         Object selectionObject = null;

         if (object instanceof IAdaptable) {
            selectionObject = ((IAdaptable) object).getAdapter(TransactionData.class);
         } else if (object instanceof Match) {
            selectionObject = ((Match) object).getElement();
         }

         if (selectionObject instanceof ArtifactChange) {
            myArtifactChangeList.add((ArtifactChange) selectionObject);
         }
      }
      return myArtifactChangeList;
   }

   @SuppressWarnings("unchecked")
   public static List<ArtifactChange> getArtifactChangeListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<ArtifactChange> myArtifactChangeList = (List<ArtifactChange>) myObjects.get(3);
      return myArtifactChangeList;
   }

   @SuppressWarnings("unchecked")
   public static List<Artifact> getArtifactListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<Artifact> mySelectedArtifactList = (List<Artifact>) myObjects.get(1);
      return mySelectedArtifactList;
   }

   @SuppressWarnings("unchecked")
   public static List<Branch> getBranchListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<Branch> mySelectedBranchList = (List<Branch>) myObjects.get(0);
      return mySelectedBranchList;
   }

   @SuppressWarnings("unchecked")
   public static List<Integer> getArtifactIDListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<Integer> mySelectedArtifactIDList = (List<Integer>) myObjects.get(2);
      return mySelectedArtifactIDList;
   }

   @SuppressWarnings("unchecked")
   public static List<ChangeReportInput> getChangeReportInputNewListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<ChangeReportInput> myChangeReportInputNewList = (List<ChangeReportInput>) myObjects.get(4);
      return myChangeReportInputNewList;
   }

   @SuppressWarnings("unchecked")
   public static List<ChangeReportInput> getChangeReportInputOldListFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Object> myObjects = getSelectedListsFromStructuredSelection(myIStructuredSelection);
      List<ChangeReportInput> myChangeReportInputOldList = (List<ChangeReportInput>) myObjects.get(5);
      return myChangeReportInputOldList;
   }

   public static IWorkbenchPartSite getIWorkbenchPartSite() {
      IWorkbenchPart myIWorkbenchPart = AWorkbench.getActivePage().getActivePart();
      IWorkbenchPartSite myIWorkbenchPartSite = myIWorkbenchPart.getSite();
      return myIWorkbenchPartSite;
   }

   //      IWorkbenchPart myIWorkbenchPart = AWorkbench.getActivePage().getActivePart();
   //      IWorkbenchPartSite myIWorkbenchPartSite = myIWorkbenchPart.getSite();

   public static List<Object> getSelectedListsFromStructuredSelection(IStructuredSelection myIStructuredSelection) {
      List<Branch> myBranches = new LinkedList<Branch>();
      List<Artifact> myArtifacts = new LinkedList<Artifact>();
      List<Integer> myArtifactIDs = new LinkedList<Integer>();
      List<ArtifactChange> mySelectedArtifactChangeList = new LinkedList<ArtifactChange>();
      List<ChangeReportInput> myChangeReportInputNewList = new LinkedList<ChangeReportInput>();
      List<ChangeReportInput> myChangeReportInputOldList = new LinkedList<ChangeReportInput>();
      List<Object> myObjects = new LinkedList<Object>();
      myObjects.add(myBranches);
      myObjects.add(myArtifacts);
      myObjects.add(myArtifactIDs);
      myObjects.add(mySelectedArtifactChangeList);
      myObjects.add(myChangeReportInputNewList);
      myObjects.add(myChangeReportInputOldList);
      if (myIStructuredSelection instanceof TreeSelection) {
         // System.out.println("myIStructuredSelection instanceof TreeSelection");
         TreeSelection myTreeSelection = (TreeSelection) myIStructuredSelection;
         TreePath[] myTreePaths = myTreeSelection.getPaths();
         for (TreePath treePath : myTreePaths) {
            for (int i = 0; i < treePath.getSegmentCount(); i++) {
               Object segmentObject = treePath.getSegment(i);
               if (segmentObject instanceof TreeNode) {
                  TreeNode myTreeNode = (TreeNode) segmentObject;
                  Object myBackingDataObject = myTreeNode.getBackingData();
                  if (myBackingDataObject instanceof SnapshotDescription) {
                     SnapshotDescription mySnapshotDescription = (SnapshotDescription) myBackingDataObject;
                     ChangeReportInput myChangeReportInputNew = mySnapshotDescription.getNewInput();
                     myChangeReportInputNew.getToTransaction();
                     myChangeReportInputNewList.add(myChangeReportInputNew);
                     ChangeReportInput myChangeReportInputOld = mySnapshotDescription.getOldInput();
                     myChangeReportInputOld.getBaseParentTransactionId();
                     myChangeReportInputOldList.add(myChangeReportInputOld);
                  }
                  if (myBackingDataObject instanceof ArtifactChange) {
                     ArtifactChange myArtifactChange = (ArtifactChange) myBackingDataObject;
                     myArtifactIDs.add(new Integer(myArtifactChange.getArtId()));
                     mySelectedArtifactChangeList.add(myArtifactChange);
                     try {
                        myArtifacts.add(myArtifactChange.getArtifact());
                        myBranches.add(myArtifactChange.getArtifact().getBranch());
                     } catch (SQLException ex) {
                     }
                  }
                  if (myBackingDataObject instanceof Artifact) {
                     Artifact myArtifact = (Artifact) myBackingDataObject;
                     myArtifactIDs.add(new Integer(myArtifact.getArtId()));
                     myArtifacts.add(myArtifact);
                     myBranches.add(myArtifact.getBranch());

                  }
                  if (myBackingDataObject instanceof Branch) {
                     myBranches.add((Branch) myBackingDataObject);
                  }
               }
            }
         }
      }
      if (false) {
         Iterator<?> iterator = myIStructuredSelection.iterator();

         while (iterator.hasNext()) {
            Object object = iterator.next();
            Object selectionObject = null;

            if (object instanceof IAdaptable) {
               selectionObject = ((IAdaptable) object).getAdapter(Branch.class);
            } else if (object instanceof Match) {
               selectionObject = ((Match) object).getElement();
            }

            if (selectionObject instanceof Branch) {
               myBranches.add((Branch) selectionObject);
            }
         }
         Object selectionObject = null;

         // Iterator<?> iterator = myIStructuredSelection.iterator();
         while (iterator.hasNext()) {
            Object object = iterator.next();

            if (object instanceof IAdaptable) {
               selectionObject = ((IAdaptable) object).getAdapter(Branch.class);

               if (selectionObject == null) {
                  selectionObject = ((IAdaptable) object).getAdapter(Artifact.class);
               }
            } else if (object instanceof Match) {
               selectionObject = ((Match) object).getElement();
            }

            if (selectionObject instanceof Branch) {
               myBranches.add(((Branch) selectionObject));
            } else if (selectionObject instanceof Artifact) {
               Artifact myArtifact = (Artifact) selectionObject;
               // names.add(artifact.getDescriptiveName());
               myArtifacts.add(myArtifact);
               myArtifactIDs.add(new Integer(myArtifact.getArtId()));
            }
         }
      }
      return myObjects;
   }

}