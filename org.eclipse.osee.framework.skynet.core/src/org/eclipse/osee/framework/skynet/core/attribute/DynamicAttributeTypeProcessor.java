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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;

/**
 * @author Ryan D. Brooks
 */
public class DynamicAttributeTypeProcessor implements RsetProcessor<DynamicAttributeDescriptor> {
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private TransactionId transactionId;

   public DynamicAttributeTypeProcessor(Branch branch) {
      this(transactionIdManager.getEditableTransactionId(branch));
   }

   public DynamicAttributeTypeProcessor(TransactionId transactionId) {
      this.transactionId = transactionId;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.db.RsetProcessor#process(java.sql.ResultSet)
    */
   public DynamicAttributeDescriptor process(ResultSet set) throws SQLException {
      DynamicAttributeDescriptor descriptor = null;
      Class<? extends Attribute> baseClass;
      try {
         String baseClassString = set.getString("attribute_class");
         baseClass = Class.forName(baseClassString).asSubclass(Attribute.class);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }

      descriptor =
            new DynamicAttributeDescriptor(baseClass, set.getString("name"), set.getString("default_value"),
                  set.getString("validity_xml"), set.getInt("min_occurence"), set.getInt("max_occurence"),
                  set.getString("tip_text"), set.getInt("attr_type_id"), transactionId);

      return descriptor;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.util.db.RsetProcessor#validate(T)
    */
   public boolean validate(DynamicAttributeDescriptor item) {
      return item != null;
   }
}