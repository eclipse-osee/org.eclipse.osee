/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.synchronization.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * Container used to group {@link SpecTypeGroveThing} and {@link SpecObjecType} objects that share a common set of attribute
 * definition.
 *
 * @author Loren K. Ashley
 */

class CommonObjectTypeContainer {

   /**
    * Stores a {@link SpecObjectTypeGroveThing} object, may be <code>null</code>.
    */

   private CommonObjectTypeGroveThing specObjectTypeGroveThing;

   /**
    * Stores a {@link SpecTypeGroveThing} object, may be <code>null</code>.
    */

   private CommonObjectTypeGroveThing specTypeGroveThing;

   /**
    * Creates a new container and saves the provided object as either a {@link SpecTypeGroveThing} or {@link SpecObjectTypeGroveThing}. If
    * the provided object is of another type, it is ignored. When assertions are enabled and the provided object is of
    * another type an assertion error is thrown.
    *
    * @param commonObjectTypeGroveThing any object extending the {@link CommonObjectTypeGroveThing} class.
    */

   CommonObjectTypeContainer(CommonObjectTypeGroveThing commonObjectTypeGroveThing) {

      if (commonObjectTypeGroveThing instanceof SpecObjectTypeGroveThing) {
         this.specObjectTypeGroveThing = commonObjectTypeGroveThing;
         this.specTypeGroveThing = null;
         return;
      }

      if (commonObjectTypeGroveThing instanceof SpecTypeGroveThing) {
         this.specTypeGroveThing = commonObjectTypeGroveThing;
         this.specObjectTypeGroveThing = null;
         return;
      }

      assert (false);
      this.specTypeGroveThing = null;
      this.specObjectTypeGroveThing = null;
   }

   /**
    * Adds an object of the opposite type to the container. When assertions are enabled an error is thrown when a
    * {@link SpecObjectTypeGroveThing} object is added to a container that already contains a {@link SpecObjectTypeGroveThing} object or
    * when a {@link SpecTypeGroveThing} object is added to a container that already contains a {@link SpecTypeGroveThing} object.
    *
    * @param commonObjectTypeGroveThing the {@link SpecObjecType} or {@link SpecTypeGroveThing} object to be added to the container.
    */

   void add(CommonObjectTypeGroveThing commonObjectTypeGroveThing) {

      if (commonObjectTypeGroveThing instanceof SpecObjectTypeGroveThing) {
         assert Objects.isNull(this.specObjectTypeGroveThing);
         this.specObjectTypeGroveThing = commonObjectTypeGroveThing;
         return;
      }

      if (commonObjectTypeGroveThing instanceof SpecTypeGroveThing) {
         assert Objects.isNull(this.specTypeGroveThing);
         this.specTypeGroveThing = commonObjectTypeGroveThing;
      }
   }

   /**
    * Gets a list containing all the {@link CommonObjectTypeGroveThing} objects in the container. When assertions are enable an
    * assertion error will be thrown if the container is empty.
    *
    * @return a list with all the {@link CommonObjectTypeGroveThing} objects in the container.
    */

   List<CommonObjectTypeGroveThing> get() {
      assert ((this.specObjectTypeGroveThing != null) || (this.specTypeGroveThing != null));

      var list = new ArrayList<CommonObjectTypeGroveThing>(2);

      if (this.specTypeGroveThing != null) {
         list.add(this.specTypeGroveThing);
      }
      if (this.specObjectTypeGroveThing != null) {
         list.add(this.specObjectTypeGroveThing);
      }

      return list;
   }

   /**
    * Gets the contained {@link CommonObjectTypeGroveThing} object of the kind specified by <code>identifierType</code>. Should
    * only be called when the method <code>hasType</code> returns <code>true</code> for the same
    * <code>identifierType</code>. When assertions are enabled an assertion error will be thrown when:
    * <ul>
    * <li>the {@link CommonObjectTypeGroveThing} object is not of the correct super class for the
    * <code>identifierType</code>,</li>
    * <li>the {@link CommonObjectTypeContainer} does not contain a {@CommonObjectTypeGroveThing} of the kind specified by
    * <code>identifierType</code>, or</li>
    * <li>the specified <code>identifierType</code> is not {@link IdentifierType.SPEC_OBJECT_TYPE} or
    * {@link IdentifierType.SPECIFICATION_TYPE}.</li>
    * </ul>
    *
    * @return the {@link SpecObjectTypeGroveThing} object.
    */

   CommonObjectTypeGroveThing get(IdentifierType identifierType) {
      if (identifierType == IdentifierType.SPEC_OBJECT_TYPE) {
         assert (this.specObjectTypeGroveThing instanceof SpecObjectTypeGroveThing);
         return this.specObjectTypeGroveThing;
      }

      if (identifierType == IdentifierType.SPECIFICATION_TYPE) {
         assert (this.specTypeGroveThing instanceof SpecTypeGroveThing);
         return this.specTypeGroveThing;
      }

      assert (false);
      return null;
   }

   /**
    * Returns the {@link ArtifactTypeToken} associated with the objects in the container. When assertions are enabled an
    * assertion error will be thrown when:<br>
    * <ul>
    * <li>the container is empty,</li>
    * <li>an object in the container does not have an associated {@link ArtifactTypeToken}, or</li>
    * <li>there are two objects in the container with different {@link ArtifactTypeToken} associations.</li>
    * </ul>
    *
    * @return the {@link ArtifactTypeToken} associated with the objects in the container.
    */

   ArtifactTypeToken getArtifactTypeToken() {
      assert (((this.specObjectTypeGroveThing != null) && (this.specObjectTypeGroveThing.getNativeThing() != null)) || ((this.specTypeGroveThing != null) && (this.specTypeGroveThing.getNativeThing() != null)));
      assert (((this.specObjectTypeGroveThing == null) || (this.specTypeGroveThing == null)) || ((this.specObjectTypeGroveThing.getNativeThing() != null) && (this.specTypeGroveThing.getNativeThing() != null) && (this.specObjectTypeGroveThing.getNativeThing() == this.specTypeGroveThing.getNativeThing())));

      var commonObjectType = this.specTypeGroveThing != null ? this.specTypeGroveThing : this.specObjectTypeGroveThing;

      var artifactTypeToken = (ArtifactTypeToken) commonObjectType.getNativeThing();

      return artifactTypeToken;
   }

   /**
    * Predicate to determine if the container contains a {@link CommonObjectTypeGroveThing} of the kind specified by
    * <code>identifierType</code>.
    *
    * @return <code>true</code>, when the container contains a {@link CommonObjectTypeGroveThing} of the kind specified by the
    * {@link IdentifierType}; otherwise, <code>false</code>.
    */

   boolean hasType(IdentifierType identifierType) {
      //@formatter:off
      return
            ( ( identifierType == IdentifierType.SPEC_OBJECT_TYPE   ) && ( this.specObjectTypeGroveThing != null ) )
         || ( ( identifierType == IdentifierType.SPECIFICATION_TYPE ) && ( this.specTypeGroveThing       != null ) );
      //@formatter:on
   }

}

/* EOF */
