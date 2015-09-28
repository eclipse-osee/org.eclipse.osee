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

  /**
   * 
   */
  public String SelectionDialogUrl;

  /**
   * @return the delegated UI url
   */
  public String getSelectionDialogUrl() {
    return this.SelectionDialogUrl;
  }

  /**
   * @param selectionDialogUrl : Url of the delegated UI
   */
  public void setSelectionDialogUrl(final String selectionDialogUrl) {
    this.SelectionDialogUrl = selectionDialogUrl;
  }

  /**
   * 
   */
  public DoorsArtifact() {
    this.children = new ArrayList<>();
  }

  /**
   * Constructor which sets the name of the DoorsArtifact
   * 
   * @param name String
   */
  public DoorsArtifact(final String name) {
    this();
    this.name = name;
  }

  /**
   * Returns the URL path of this object
   * 
   * @return Path of the object obtained from DWA
   */
  public String getPath() {
    return this.path;
  }


  /**
   * Sets the URL path of this object
   * 
   * @param path URL of the object returned from DWA
   */
  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * Method to add children objects
   * 
   * @param child Object which is a child
   */
  public void addChild(final DoorsArtifact child) {
    this.children.add(child);
  }

  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Sets name of this object
   * 
   * @param name String
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Method to add children objects
   * 
   * @return Returns the children
   */
  public List<DoorsArtifact> getChildren() {
    return this.children;
  }
}
