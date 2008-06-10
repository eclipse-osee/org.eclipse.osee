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
package org.eclipse.osee.framework.skynet.core.revision;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.ui.IMemento;

/**
 * An input set of information for the content provider used for Change Reports.
 * 
 * @author Robert A. Fisher
 */
public class ChangeReportInput implements Serializable {
   private static final long serialVersionUID = 2195524675406713624L;
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ChangeReportInput.class);
   private static final String BASE_PARENT_NUMBER = "baseParentNumber";
   private static final String BASE_NUMBER = "baseNumber";
   private static final String BRANCH_INPUT = "branchInput";
   private static final String NAME = "name";
   private static final String TO_NUMBER = "toNumber";
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private TransactionId baseParentTransactionId;
   private TransactionId baseTransaction;
   private TransactionId toTransaction;
   private boolean forceRefresh;
   private final String name;
   private boolean branchInput;
   private long checksum;

   /**
    * @param branch
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public ChangeReportInput(Branch branch) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this("Change Report: " + branch.getDisplayName(), transactionIdManager.getStartEndPoint(branch),
            branch.getParentBranch() != null);
   }

   /**
    * @param transactionToFrom
    * @param detectConflicts
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public ChangeReportInput(String name, Pair<TransactionId, TransactionId> transactionToFrom, boolean detectConflicts) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this(name, transactionToFrom.getKey(), transactionToFrom.getValue(), detectConflicts, true);
   }

   /**
    * @param transactionId
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public ChangeReportInput(String name, TransactionId transactionId) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this(name, transactionIdManager.getPriorTransaction(transactionId), transactionId);
   }

   /**
    * @param baseTransactionId
    * @param toTransactionId
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   public ChangeReportInput(String name, TransactionId baseTransactionId, TransactionId toTransactionId) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this(name, baseTransactionId, toTransactionId, false, false);
   }

   /**
    * @param baseTransaction
    * @param toTransaction
    * @param detectConflicts
    * @throws SQLException
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    */
   private ChangeReportInput(String name, TransactionId baseTransaction, TransactionId toTransaction, boolean detectConflicts, boolean branchInput) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this(name, null, baseTransaction, toTransaction, branchInput);

      if (baseTransaction.equals(toTransaction)) {
         throw new IllegalArgumentException("The base and to transactions must not be the same transaction");
      }

      if (baseTransaction.getBranch().getParentBranch() == null && detectConflicts) throw new IllegalArgumentException(
            "The transactions must be from a branch that has a parent branch.");

      // Attempt to get the branched transaction number for the parent branch
      if (detectConflicts) {
         this.baseParentTransactionId = transactionIdManager.getParentBaseTransaction(baseTransaction.getBranch());

         if (this.baseParentTransactionId == null) {
            logger.log(Level.SEVERE,
                  "Unable to determine the base parent transaction for " + baseTransaction.getBranch());
         }
      }
   }

   /**
    * @param baseParentTransactionId
    * @param baseTransaction
    * @param toTransaction
    * @throws SQLException
    */
   private ChangeReportInput(String name, TransactionId baseParentTransactionId, TransactionId baseTransaction, TransactionId toTransaction, boolean branchInput) throws SQLException {
      if (baseTransaction == null) throw new IllegalArgumentException("baseTransaction can not be null.");
      if (toTransaction == null) throw new IllegalArgumentException("toTransaction can not be null.");
      if (!baseTransaction.getBranch().equals(toTransaction.getBranch())) throw new IllegalArgumentException(
            "The baseTransaction and toTransaction must be on the same branch.");
      if (baseTransaction.getTransactionNumber() > toTransaction.getTransactionNumber()) throw new IllegalArgumentException(
            "The toTransaction must be a point at or after the baseTransaction.");

      this.baseParentTransactionId = baseParentTransactionId;
      this.baseTransaction = baseTransaction;
      this.toTransaction = toTransaction;
      this.forceRefresh = false;
      this.checksum = transactionIdManager.getTransactionRangeChecksum(baseTransaction, toTransaction).getValue();

      this.name = name;

      this.branchInput = branchInput;
   }

   /**
    * @return Returns the baseParentTransactionId.
    */
   public TransactionId getBaseParentTransactionId() {
      return baseParentTransactionId;
   }

   /**
    * @return Returns the baseTransaction.
    */
   public TransactionId getBaseTransaction() {
      return baseTransaction;
   }

   /**
    * @return Returns the toTransaction.
    */
   public TransactionId getToTransaction() {
      return toTransaction;
   }

   /**
    * @return Returns the forceRefresh.
    */
   public boolean isForceRefresh() {
      return forceRefresh;
   }

   /**
    * @param forceRefresh The forceRefresh to set.
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    * @throws IllegalStateException
    */
   public void setForceRefresh(boolean forceRefresh) throws SQLException, BranchDoesNotExist, TransactionDoesNotExist {
      this.forceRefresh = forceRefresh;
      if (branchInput && this.forceRefresh == true) {
         toTransaction = transactionIdManager.getNonEditableStartEndPoint(baseTransaction.getBranch()).getValue();
      }
   }

   public boolean isEmptyChange() {
      return baseTransaction == null || baseTransaction == toTransaction;
   }

   public Branch getBranch() {
      return toTransaction.getBranch();
   }

   public String getName() {
      return name;
   }

   public void saveToMemento(IMemento memento) {
      if (memento == null) throw new IllegalArgumentException("memento can not be null");

      if (baseParentTransactionId != null) {
         memento.putInteger(BASE_PARENT_NUMBER, baseParentTransactionId.getTransactionNumber());
      }
      if (baseTransaction != null) {
         memento.putInteger(BASE_NUMBER, baseTransaction.getTransactionNumber());
      }
      if (toTransaction != null) {
         memento.putInteger(TO_NUMBER, toTransaction.getTransactionNumber());
      }
      if (branchInput) {
         memento.putString(BRANCH_INPUT, Boolean.TRUE.toString());
      }
      memento.putString(NAME, name);
   }

   public static ChangeReportInput loadFromMemento(IMemento memento) throws SQLException {
      if (memento == null) throw new IllegalArgumentException("memento can not be null");

      TransactionIdManager transactionManager = TransactionIdManager.getInstance();
      Integer transactionNumber;
      TransactionId priorBaseParentTransactionId = null;
      TransactionId priorBaseTransactionId = null;
      TransactionId priorToTransactionId = null;

      try {
         transactionNumber = memento.getInteger(BASE_PARENT_NUMBER);
         if (transactionNumber != null) {
            priorBaseParentTransactionId = transactionManager.getNonEditableTransactionId(transactionNumber);
         }
         transactionNumber = memento.getInteger(BASE_NUMBER);
         if (transactionNumber != null) {
            priorBaseTransactionId = transactionManager.getNonEditableTransactionId(transactionNumber);
         }

         boolean priorBranchInput = Boolean.parseBoolean(memento.getString(BRANCH_INPUT));
         transactionNumber = memento.getInteger(TO_NUMBER);
         if (transactionNumber != null) {
            priorToTransactionId = transactionManager.getNonEditableTransactionId(transactionNumber);

            if (priorBranchInput) {
               priorToTransactionId =
                     transactionManager.getNonEditableStartEndPoint(priorToTransactionId.getBranch()).getValue();
            }
         }

         String priorName = memento.getString(NAME);

         return new ChangeReportInput(priorName, priorBaseParentTransactionId, priorBaseTransactionId,
               priorToTransactionId, priorBranchInput);
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   public long getChecksum() {
      return checksum;
   }
}
