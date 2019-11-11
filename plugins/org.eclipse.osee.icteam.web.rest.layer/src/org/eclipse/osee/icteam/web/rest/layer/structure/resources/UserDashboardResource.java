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
package org.eclipse.osee.icteam.web.rest.layer.structure.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifactsContainer;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.InterfaceAdapter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * UserDashboard Resource to return Logged in User Specific Information
 * 
 * @author Ajay Chandrahasan
 */

@Path("UserDashboard")
public class UserDashboardResource extends AbstractConfigResource{

	public UserDashboardResource(AtsApi atsApi, OrcsApi orcsApi) {
		super(AtsArtifactTypes.Project, atsApi, orcsApi);
	}
	/**
	 * Function used to fetch the projects which are specific to logged in user
	 * 
	 * @param json {@link String} Uuid of the user
	 * @return serialize serialize user specific projects
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("getUserSpecificTasks")
	public String getUserSpecificTasks(final String json) {
		String serialize = null;
		try {
			OrcsApi orcsApi = OseeCoreData.getOrcsApi();
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(ITransferableArtifact.class, new InterfaceAdapter<TransferableArtifact>())
					.create();
			TransferableArtifact artifact = gson.fromJson(json, TransferableArtifact.class);
			String userGuid = artifact.getUuid();
			String userId= artifact.getName();
			
			if (userGuid != null) {
				List<ITransferableArtifact> listTras = new ArrayList<ITransferableArtifact>();
				ResultSet<ArtifactReadable> projects = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
						.andIsOfType(AtsArtifactTypes.Project).getResults();
				for (ArtifactReadable project : projects) {
					String shortname = project.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne()
							.toString();
					QueryBuilder query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON)
							.andIsOfType(AtsArtifactTypes.TeamWorkflow)
							.and(AtsAttributeTypes.CurrentStateType, "Working", QueryOption.EXACT_MATCH_OPTIONS)
							.andRelatedTo(AtsRelationTypes.ProjectToTeamWorkFlow_Project, project);
					ArtifactReadable userArtifact = CommonUtil.getUserFromGivenUserId(userId);;
					query = query.and(AtsAttributeTypes.CurrentState,
							userArtifact.getSoleAttributeAsString(CoreAttributeTypes.UserId),
							QueryOption.TOKEN_DELIMITER__ANY);
					ResultSet<ArtifactReadable> results = query.getResults();
					for (ArtifactReadable artifactReadable : results) {
						TransferableArtifact ar = new TransferableArtifact();
						ResultSet<ArtifactReadable> related = artifactReadable
								.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
						String workPackage = null;
						if (artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage).size() > 0) {
							workPackage = artifactReadable.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne()
									.toString();
							TranferableArtifactLoader
									.copyArtifactReadbleToTransferableArtifactWithoutRelations(artifactReadable, ar);
							String taskId = shortname + "-" + workPackage;
							if (related.size() == 0) {
								ar.putAttributes("Backlog", Arrays.asList("true"));
							} else {
								ar.putAttributes("Backlog", Arrays.asList("false"));
								ar.putAttributes("SprintName", Arrays.asList(related.getExactlyOne().getName()));
							}
							ar.putAttributes("TaskId", Arrays.asList(taskId));
							ar.putAttributes("ProjectName",
									Arrays.asList(
											artifactReadable.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project)
													.getExactlyOne().getName()));
							listTras.add(ar);
						}
					}
				}
				TransferableArtifactsContainer container = new TransferableArtifactsContainer();
				container.addAll(listTras);
				serialize = gson.toJson(container);
			}
			return serialize;
		} catch (OseeCoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}
