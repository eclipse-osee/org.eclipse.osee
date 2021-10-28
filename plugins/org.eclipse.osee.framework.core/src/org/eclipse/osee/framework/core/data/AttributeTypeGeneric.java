/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdDescription;

/**
 * @author Ryan D. Brooks
 */
public abstract class AttributeTypeGeneric<T> extends NamedIdDescription implements AttributeTypeToken {
   public static final AttributeTypeString SENTINEL =
      AttributeTypeToken.createString(Id.SENTINEL, NamespaceToken.OSEE, Named.SENTINEL, Named.SENTINEL, "", "");

   private final String mediaType;
   private final TaggerTypeToken taggerType;
   private final NamespaceToken namespace;
   private final String fileExtension;
   private final T defaultValue;
   protected final Set<DisplayHint> displayHints = new HashSet<DisplayHint>();

   public AttributeTypeGeneric(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, T defaultValue, Set<DisplayHint> displayHints) {
      super(id, name, description);
      this.namespace = namespace;
      this.mediaType = mediaType;
      this.taggerType = taggerType;
      this.fileExtension = fileExtension;
      this.defaultValue = defaultValue;
      this.displayHints.addAll(displayHints);
   }

   public AttributeTypeGeneric(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, T defaultValue) {
      this(id, namespace, name, mediaType, description, taggerType, fileExtension, defaultValue,
         Collections.emptySet());
   }

   public AttributeTypeGeneric(Long id, NamespaceToken namespace, String name, String mediaType, String description, TaggerTypeToken taggerType, String fileExtension, T defaultValue, DisplayHint... displayHints) {
      this(id, namespace, name, mediaType, description, taggerType, fileExtension, defaultValue,
         org.eclipse.osee.framework.jdk.core.util.Collections.asHashSet(displayHints));
   }

   public T getBaseAttributeTypeDefaultValue() {
      return defaultValue;
   }

   @Override
   public String getMediaType() {
      return mediaType;
   }

   @Override
   public String getFileExtension() {
      return fileExtension;
   }

   public T valueFromDouble(double value) {
      return null;
   }

   /**
    * @param storedValue is the raw String stored in the database
    * @return the attribute value in its native Java representation
    */
   public abstract T valueFromStorageString(String storedValue);

   public String storageStringFromValue(T value) {
      return value.toString();
   }

   /**
    * @param value the attribute value in its native Java representation (converted from the storedValue as needed)
    * @return a user friendly text representation of the attribute value
    */
   public String getDisplayableString(T value) {
      return storageStringFromValue(value);
   }

   @Override
   public TaggerTypeToken getTaggerType() {
      return taggerType;
   }

   @Override
   public NamespaceToken getNamespace() {
      return namespace;
   }

   @Override
   public boolean isTaggable() {
      return taggerType.isValid();
   }

   @Override
   public Set<DisplayHint> getDisplayHints() {
      return displayHints;
   }

   public void addDisplayHint(DisplayHint displayHint) {
      displayHints.add(displayHint);
   }

}