/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class to store the name, path and children to doorsArtifact
 *
 * @author Chandan Bandemutt
 */
public class DoorsArtifact implements INamedElement {

   String path;
   List<DoorsArtifact> children;
   String name;
   public String SelectionDialogUrl;

   public String getSelectionDialogUrl() {
      return this.SelectionDialogUrl;
   }

   public void setSelectionDialogUrl(final String selectionDialogUrl) {
      this.SelectionDialogUrl = selectionDialogUrl;
   }

   public DoorsArtifact() {
      this.children = new ArrayList<>();
      this.path = "";
      this.name = "";
   }

   public DoorsArtifact(final String name) {
      this();
      this.name = name;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void addChild(final DoorsArtifact child) {
      // don't add duplicate children
      boolean addable = true;
      for (DoorsArtifact sibling : children) {
         if (sibling.getName().equals(child.getName())) {
            addable = false;
         }
      }
      if (addable) {
         this.children.add(child);
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public List<DoorsArtifact> getChildren() {
      return this.children;
   }
}
