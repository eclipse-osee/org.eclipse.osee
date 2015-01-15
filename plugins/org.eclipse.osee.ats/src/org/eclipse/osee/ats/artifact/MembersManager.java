/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.core.client.artifact.CollectorArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public abstract class MembersManager<T extends CollectorArtifact> {

   public MembersManager() {
   }

   public abstract IRelationTypeSide getMembersRelationTypeSide();

   public abstract String getItemName();

   public abstract IArtifactType getArtifactType();

   public boolean isHasMember(Artifact artifact) throws OseeCoreException {
      return artifact.getRelatedArtifactsCount(getMembersRelationTypeSide().getOpposite()) > 0;
   }

   /**
    * change member order for artifact within given member
    */
   public T promptChangeMemberOrder(T memberArt, Artifact artifact) throws OseeCoreException {
      return promptChangeMemberOrder(memberArt, Arrays.asList(artifact));
   }

   /**
    * change member order for artifacts within given member
    */
   public T promptChangeMemberOrder(T memberArt, List<Artifact> artifacts) throws OseeCoreException {
      StringBuilder currentOrder = new StringBuilder("Current Order: ");
      for (Artifact artifact : artifacts) {
         if (artifacts.size() == 1 && !isHasMember(artifact) || memberArt == null) {
            AWorkbench.popup(String.format("No %s set for artifact [%s]", getItemName(), artifact));
            return null;
         }
         String currIndexStr = getMemberOrder(memberArt, artifact);
         currentOrder.append(currIndexStr + ", ");
      }

      List<Artifact> members = memberArt.getMembers();
      EntryDialog ed =
         new EntryDialog(String.format("Change %S Order", getItemName()), String.format(
            "%s: %s\n\n%s\n\nEnter New Order Number from 1..%d or %d for last.", getItemName(), memberArt,
            currentOrder.toString().replaceFirst(", $", ""), members.size(), members.size() + 1));
      ed.setNumberFormat(NumberFormat.getIntegerInstance());

      if (ed.open() == Window.OK) {
         String newIndexStr = ed.getEntry();
         Integer enteredIndex = Integer.valueOf(newIndexStr);
         boolean insertLast = enteredIndex > members.size();
         Integer membersIndex = insertLast ? members.size() - 1 : enteredIndex - 1;
         if (membersIndex > members.size()) {
            AWorkbench.popup(String.format("New Order Number [%s] out of range 1..%d", newIndexStr, members.size()));
            return null;
         }
         List<Artifact> reversed = new LinkedList<Artifact>(artifacts);
         Collections.reverse(reversed);
         for (Artifact artifact : reversed) {
            int currentIdx = members.indexOf(artifact);
            Artifact insertTarget = members.get(membersIndex);
            boolean insertAfter = membersIndex > currentIdx;
            memberArt.setRelationOrder(getMembersRelationTypeSide(), insertTarget, insertAfter, artifact);
         }
         memberArt.persist("Prompt-Change " + getItemName());
         return memberArt;
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public String getMemberOrder(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(getArtifactType())) {
         return "";
      }
      if (!isHasMember(artifact)) {
         return "";
      }
      Collection<Artifact> members = getMembers(artifact, false);
      if (members.size() > 1) {
         List<Artifact> membersSorted = new ArrayList<Artifact>(members);
         Collections.sort(membersSorted);
         StringBuffer sb = new StringBuffer();
         for (Artifact member : membersSorted) {
            sb.append(String.format("%s-[%s] ", getMemberOrder((T) member, artifact), member));
         }
         return sb.toString();
      }
      Artifact member = members.iterator().next();
      return getMemberOrder((T) member, artifact);
   }

   public String getMemberOrder(T memberArt, Artifact member) throws OseeCoreException {
      List<Artifact> members = memberArt.getMembers();
      if (!members.contains(member)) {
         return "";
      }
      try {
         return String.valueOf(members.indexOf(member) + 1);
      } catch (Exception ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

   public Collection<Artifact> getMembers(Artifact artifact, boolean recurse) throws OseeCoreException {
      Set<Artifact> members = new HashSet<Artifact>();
      getMembers(artifact, members, recurse);
      return members;
   }

   public void getMembers(Artifact artifact, Set<Artifact> members, boolean recurse) throws OseeCoreException {
      getMembers(Arrays.asList(artifact), members, recurse);
   }

   public void getMembers(Collection<Artifact> artifacts, Set<Artifact> members, boolean recurse) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art.isOfType(getArtifactType())) {
            members.add(art);
         }
         for (Artifact art2 : art.getRelatedArtifacts(getMembersRelationTypeSide())) {
            if (art2.isOfType(getArtifactType())) {
               members.add(art2);
            }
         }
         if (recurse && art instanceof AbstractWorkflowArtifact && ((AbstractWorkflowArtifact) art).getParentAWA() != null) {
            getMembers(((AbstractWorkflowArtifact) art).getParentAWA(), members, recurse);
         }
      }
   }

}
