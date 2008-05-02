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

import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert A. Fisher
 */
public abstract class ChangeSummary<T extends RevisionChange> implements Serializable {
   private ArrayList<T> changes; // ArrayList was chosen since it is known to implement Serializable
   private T oldestChange;
   private T newestChange;
   private boolean conflicted;

   /**
    * Constructor for deserialization.
    */
   protected ChangeSummary() {

   }

   /**
    * @param changes
    */
   public ChangeSummary(Collection<T> changes) {
      if (changes == null) throw new IllegalArgumentException("changes can not be null");
      if (changes.size() < 2) throw new IllegalArgumentException("changes must have more than one change");

      this.changes = new ArrayList<T>(changes);
      this.oldestChange = this.newestChange = changes.iterator().next();

      boolean incoming = false;
      boolean outgoing = false;
      for (T change : changes) {
         if (change.getChangeType() == OUTGOING) {
            outgoing = true;
         } else if (change.getChangeType() == INCOMING) {
            incoming = true;
         }

         if (change.getGammaId() < oldestChange.getGammaId())
            oldestChange = change;
         else if (change.getGammaId() > newestChange.getGammaId()) newestChange = change;
      }

      this.conflicted = incoming && outgoing;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getGammaId()
    */
   public long getGammaId() {
      return newestChange.getGammaId();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.revision.IAttributeChange#getImage()
    */
   public Image getImage() {
      ChangeType changeType = conflicted ? CONFLICTING : newestChange.getChangeType();
      ModificationType modType;

      if (oldestChange.getModType() == ModificationType.NEW && newestChange.getModType() != ModificationType.DELETED)
         modType = oldestChange.getModType();
      else
         modType = newestChange.getModType();

      return getImage(changeType, modType);
   }

   protected abstract Image getImage(ChangeType changeType, ModificationType modType);

   /**
    * @return Returns the changes.
    */
   public Collection<T> getChanges() {
      return changes;
   }

   /**
    * @return Returns the newestChange.
    */
   protected final T getNewestChange() {
      return newestChange;
   }

   /**
    * @return Returns the oldestChange.
    */
   protected final T getOldestChange() {
      return oldestChange;
   }

   /**
    * @return Returns the conflicted.
    */
   public boolean isConflicted() {
      return conflicted;
   }
}
