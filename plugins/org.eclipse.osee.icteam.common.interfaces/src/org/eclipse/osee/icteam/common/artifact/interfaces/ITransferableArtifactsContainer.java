/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.artifact.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An interface for TransferableArtifactsContainer
 *
 * @author Ajay Chandrahasan
 */
public interface ITransferableArtifactsContainer {

  /**
   * returns status
   *
   * @return the status
   */
  public String getStatus();


  /**
   * sets status
   *
   * @param status the status to set
   */
  public void setStatus(final String status);


  /**
   * @return the metaInfo. This map value cannot be mutated
   */
  public Map<String, ? extends Object> getMetaInfo();


  /**
   * @param metaInfo the metaInfo to set
   */
  public void setMetaInfo(final Map<String, ? extends Object> metaInfo);


  public String getProjectGUID();

  public void setProjectGUID(final String projectGUID);

  public HashMap<String, String> getAttributes();

  public void setAttributes(final HashMap<String, String> checkedAttributes);

  public boolean isInclude();

  public void setInclude(final boolean include);

  public ITransferableArtifact getParentArtifact();

  public void setParentArtifact(final ITransferableArtifact parentArtifact);


  public List<ITransferableArtifact> getArtifactList();

  public void addAll(final List<ITransferableArtifact> listTras);

}
