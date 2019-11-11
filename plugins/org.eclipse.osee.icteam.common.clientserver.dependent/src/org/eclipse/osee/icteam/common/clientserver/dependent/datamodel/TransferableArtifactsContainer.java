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
package org.eclipse.osee.icteam.common.clientserver.dependent.datamodel;

import com.google.common.collect.ImmutableMap;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifactsContainer;
import org.eclipse.osee.icteam.common.clientserver.dependent.util.CurrentUserID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class if for list of transferable artifact
 *
 * @author Ajay Chandrahasan
 */
public class TransferableArtifactsContainer extends CurrentUserID
    implements ITransferableArtifactsContainer {
    List<ITransferableArtifact> list = new ArrayList<ITransferableArtifact>();
    List<AttributeTypeToken> attributeTypes = new ArrayList<AttributeTypeToken>();
    ITransferableArtifact parentArtifact;
    String status;
    public Map<String, ?extends Object> metaInfo = new HashMap<String, Object>();
    List<TeamWorkFlowArtifact> listTeamWorkFlow = new ArrayList<TeamWorkFlowArtifact>();
    HashMap<String, String> attributes = new HashMap<String, String>();
    String projectGUID;
    boolean include = false;
    String excelName = "";
    String filePath = "";

    /**
     * Constructor
     */
    public TransferableArtifactsContainer() {
    }

    /**
     * @param list the list to set
     */
    public void setArtifactList(final List<ITransferableArtifact> list) {
        this.list.addAll(list);
    }

    /**
     * get list team workflows
     *
     * @return
     */
    public List<TeamWorkFlowArtifact> getListTeamWorkFlow() {
        return this.listTeamWorkFlow;
    }

    /**
     * sets team workflow list
     *
     * @param listTeamWorkFlow
     */
    public void setListTeamWorkFlow(
        final List<TeamWorkFlowArtifact> listTeamWorkFlow) {
        this.listTeamWorkFlow.addAll(listTeamWorkFlow);
    }

    /**
     * @return the status
     */
    @Override
    public String getStatus() {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    @Override
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * @return the metaInfo. This map value cannot be mutated
     */
    @Override
    public Map<String, ?extends Object> getMetaInfo() {
        return ImmutableMap.copyOf(this.metaInfo);
    }

    /**
     * @param metaInfo the metaInfo to set
     */
    @Override
    public void setMetaInfo(final Map<String, ?extends Object> metaInfo) {
        this.metaInfo = metaInfo;
    }

    @Override
    public String getProjectGUID() {
        return this.projectGUID;
    }

    @Override
    public void setProjectGUID(final String projectGUID) {
        this.projectGUID = projectGUID;
    }

    @Override
    public HashMap<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final HashMap<String, String> checkedAttributes) {
        this.attributes = checkedAttributes;
    }

    @Override
    public boolean isInclude() {
        return this.include;
    }

    @Override
    public void setInclude(final boolean include) {
        this.include = include;
    }

    @Override
    public ITransferableArtifact getParentArtifact() {
        return this.parentArtifact;
    }

    @Override
    public void setParentArtifact(final ITransferableArtifact parentArtifact) {
        this.parentArtifact = parentArtifact;
    }

    @Override
    public List<ITransferableArtifact> getArtifactList() {
        return this.list;
    }

    @Override
    public void addAll(final List<ITransferableArtifact> listTras) {
        this.list.addAll(listTras);
    }

    public List<AttributeTypeToken> getallAttributeTypes() {
        return this.attributeTypes;
    }

    public void addallAttributeTypes(
        final Collection<?extends AttributeTypeToken> all) {
        this.attributeTypes.addAll(all);
    }
}
