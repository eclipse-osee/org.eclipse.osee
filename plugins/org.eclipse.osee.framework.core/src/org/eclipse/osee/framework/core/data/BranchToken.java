/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * {@link BranchToken} implementations need to minimally provide an identifier, name, and view for a branch.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

@JsonSerialize(using = BranchTokenSerializer.class)
@JsonDeserialize(using = BranchTokenDeserializer.class)
public interface BranchToken extends BranchId, NamedId {

   /**
    * The name assigned to a {@link BranchToken} when a name is not available.
    */

   static String NOT_LOADED_NAME = "Not Loaded";

   /**
    * Sentinel {@link BranchToken} implementation has the following:
    * <dl>
    * <dt>BranchId:</dt>
    * <dd>{@link Id#SENTINEL}</dd>
    * <dt>Name:</dt>
    * <dd>{@link Named#SENTINEL}</dd>
    * <dt>View ArtifactId:</dt>
    * <dd>{@link ArtifactId#SENTINEL}</dd>
    * </dl>
    */

   BranchToken SENTINEL = BranchToken.create(Id.SENTINEL, Named.SENTINEL);

   /**
    * Branch short names are truncated to {@link BranchToken#SHORT_NAME_LIMIT} number of characters.
    */

   static final int SHORT_NAME_LIMIT = 35;

   /**
    * When <code>id</code> is an instance of the interface {@link BranchToken}, it is just returned as a
    * {@link BranchToken}; otherwise, a new {@link BranchToken} implementation is created with the identifier from
    * <code>id</code>, the name specified by <code>name</code>, and the view {@link ArtifactId#SENTINEL}.
    * <p>
    * The only guaranty for the returned {@link BranchToken} is that it will have the {@link BranchId} specified by
    * <code>id</code>.
    *
    * @param id the {@link BranchId} for the returned {@link BranchToken}.
    * @param name when a new {@link BranchToken} is created this parameter specifies the name to be used. When a new
    * {@link BranchToken} is created and this parameter is <code>null</code> or blank, the name
    * {@link BranchToken#NOT_LOADED_NAME} will be used.
    * @return a {@link BranchToken} implementation with the {@link BranchId} specified by <code>id</code>.
    */

   public static BranchToken create(BranchId id, String name) {

      if (id instanceof BranchToken) {
         return (BranchToken) id;
      }

      return create(id.getId(), name, ArtifactId.SENTINEL, Collections.emptyList());
   }

   /**
    * Creates a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, and
    * the view {@link ArtifactId#SENTINEL}. When <code>id</code> is less than zero, {@link Id#SENTINEL} will be used for
    * the identifier.
    *
    * @param id the <code>long</code> to be used as the branch identifier.
    * @param name the name to be used for the created {@link BranchToken}.
    * @return a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, and
    * the view {@link ArtifactId#SENTINEL}.
    */

   public static BranchToken create(long id, String name) {

      return create(Long.valueOf(id), name, ArtifactId.SENTINEL, Collections.emptyList());
   }

   /**
    * Creates a new {@link BranchToken} with the specified <code>id</code>, the name
    * {@link BranchToken#NOT_LOADED_NAME}, and the specified <code>view</code>. When <code>id</code> is less than zero
    * or <code>null</code>, {@link Id#SENTINEL} will be used for the identifier. When <code>view</code> is
    * <code>null</code>, {@link ArtifactId#SENTINEL} will be used for the view.
    *
    * @param id the long value to be used as the branch identifier.
    * @param view the {@link ArtifactId} to use as the branch view.
    * @return a new {@link BranchToken} with the specified <code>id</code>, the name specified by
    * {@link BranchToken#NOT_LOADED_NAME}, and the specified <code>view</code>.
    */

   public static BranchToken create(Long id, ArtifactId view) {

      return create(id, BranchToken.NOT_LOADED_NAME, view, Collections.emptyList());
   }

   /**
    * Creates a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, and
    * the view {@link ArtifactId#SENTINEL}. When <code>id</code> is less than zero or <code>null</code>,
    * {@link Id#SENTINEL} will be used for the identifier.
    *
    * @param id the long value to be used as the branch identifier.
    * @param name the name to be used for the created {@link BranchToken}.
    * @return a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, and
    * the view {@link ArtifactId#SENTINEL}.
    */

   public static BranchToken create(Long id, String name) {

      return create(id, name, ArtifactId.SENTINEL, Collections.emptyList());
   }

   /**
    * Creates a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, the
    * view {@link ArtifactId} specified by <code>viewId</code>, and the list of {@link BranchCategoryToken} specified by
    * <code>categories</code>. When <code>id</code> is less than zero or <code>null</code>, {@link Id#SENTINEL} will be
    * used for the identifier. When <code>name</code> is <code>null</code> or blank, {@link BranchToken#NOT_LOADED_NAME}
    * will be used. When <code>viewId</code> is <code>null</code>, {@link ArtifactId#SENTINEL} will be used.
    *
    * @param id the long value to be used as the branch identifier.
    * @param name the name to be used for the created {@link BranchToken}.
    * @param viewId the view {@link ArtifactId} for the branch.
    * @param categories the list of {@link BranchCategoryToken} for the branch.
    * @return a new {@link BranchToken} with the specified <code>id</code>, the name specified by <code>name</code>, and
    * the view {@link ArtifactId#SENTINEL}.
    */
   public static BranchToken create(Long id, String name, ArtifactId viewId, List<BranchCategoryToken> categories) {

      final class BranchTokenImpl extends NamedIdBase implements BranchToken {

         private final ArtifactId viewId;
         private final List<BranchCategoryToken> categories;

         public BranchTokenImpl(Long id, String name, ArtifactId viewId, List<BranchCategoryToken> categories) {
            super(id, name);
            this.viewId = viewId;
            this.categories = categories;
         }

         /**
          * Compares both the branch identifier and view artifact identifier of this {@link BranchId} implementation and
          * of <code>obj</code>. The {@link BranchToken}'s names are not a part of the comparison.
          *
          * @param obj the {@link Object} to be compared to.
          * @return <code>true</code> when <code>obj</code> is an instance of <code>BranchId</code>, the branch
          * identifier of this {@link BranchId} equals the {@link BranchId} of <code>obj</code>, and the view
          * {@link ArtifactId} of this {@link BranchId} equals the view {@link ArtifactId} of <code>obj</code>;
          * otherwise, <code>false</code>.
          * @implNote This implementation requires the implementation of {@link BaseId} to never allow a
          * <code>null</code> value for the member {@link #id}.
          * @implNote The value of the member {@link #view} may be <code>null</code>.
          */
         @SuppressWarnings("unlikely-arg-type")
         @Override
         public boolean equals(Object obj) {
            if (!(obj instanceof BranchId)) {
               return false;
            }

            final var otherBranchId = (BranchId) obj;

            if (!super.equals(otherBranchId)) {
               return false;
            }

            final var otherViewId = otherBranchId.getViewId();

            if (Objects.isNull(this.viewId) && Objects.isNull(otherViewId)) {
               return true;
            }

            if (Objects.isNull(this.viewId)) {
               return false;
            }

            return this.viewId.equals(otherViewId);
         }

         @Override
         public ArtifactId getViewId() {
            return viewId;
         }

         @Override
         public List<BranchCategoryToken> getCategories() {
            return categories;
         }

         @Override
         public String toStringWithId() {
            return String.format("[%s]-[%s]", getName(), getId());
         }
      }

      var safeId = (Objects.nonNull(id) && id >= -1) ? id : Id.SENTINEL;
      var safeName = Strings.isValidAndNonBlank(name) ? name : BranchToken.NOT_LOADED_NAME;
      var safeViewId = Objects.nonNull(viewId) ? viewId : ArtifactId.SENTINEL;

      return new BranchTokenImpl(safeId, safeName, safeViewId, categories);
   }

   public static BranchToken create(Long id, String name, ArtifactId viewId) {
      return create(id, name, viewId, Collections.emptyList());
   }

   public static BranchToken create(long id, String name, List<BranchCategoryToken> categories) {
      return create(id, name, ArtifactId.SENTINEL, categories);
   }

   /**
    * Creates a new {@link BranchToken} with a random identifier, the name specified by <code>name</code>, and the view
    * {@link ArtifactId#SENTINEL}.
    *
    * @param name the name to be used for the created {@link BranchToken}.
    * @return a new {@link BranchToken} with a random identifier, the name specified by <code>name</code>, and the view
    * {@link ArtifactId#SENTINEL}.
    */

   public static BranchToken create(String name) {

      return create(Lib.generateUuid(), name, ArtifactId.SENTINEL, Collections.emptyList());
   }

   /**
    * When <code>id</code> is an instance of the interface {@link BranchToken}, it is just returned as a
    * {@link BranchToken}; otherwise, a new {@link BranchToken} is created with the identifier from <code>id</code>, the
    * name {@link BranchToken#NOT_LOADED_NAME}, and the view {@link ArtifactId#SENTINEL}.
    * <p>
    * The only guaranty for the returned {@link BranchToken} is that it will have the {@link BranchId} specified by
    * <code>id</code>.
    *
    * @param id the {@link BranchId} for the returned {@link BranchToken}.
    * @return a {@link BranchToken} implementation with the {@link BranchId} specified by <code>id</code>.
    */

   public static BranchToken valueOf(BranchId id) {

      if (id instanceof BranchToken) {
         return (BranchToken) id;
      }

      return create(id.getId(), BranchToken.NOT_LOADED_NAME, ArtifactId.SENTINEL, Collections.emptyList());
   }

   /**
    * Gets the represented branch name truncated to {@link BranchToken#SHORT_NAME_LIMIT} number of characters.
    *
    * @return the branch name truncated to {@link BranchToken#SHORT_NAME_LIMIT} number of characters.
    */

   default String getShortName() {

      return getShortName(SHORT_NAME_LIMIT);
   }

   /**
    * Gets the represented branch name truncated to <code>length</code> number of characters.
    *
    * @param length the maximum number of characters for the returned string.
    * @return the branch name truncated to <code>length</code> characters.
    * @throws IndexOutOfBounds when <code>length</code> is less than zero.
    */

   default String getShortName(int length) {

      return Strings.truncate(this.getName(), length);
   }

   /**
    * Gets the provided branch name truncated to {@link BranchToken#SHORT_NAME_LIMIT} number of characters.
    *
    * @return the provided branch name truncated to {@link BranchToken#SHORT_NAME_LIMIT} number of characters.
    */
   public static String getShortName(String name) {
      return Strings.truncate(name, SHORT_NAME_LIMIT);
   }

   List<BranchCategoryToken> getCategories();
}

/* EOF */
