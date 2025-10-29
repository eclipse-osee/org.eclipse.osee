/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component, computed, inject, input, signal } from '@angular/core';
import { teamWorkflowDetailsImpl } from '@osee/shared/types/configuration-management';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { WorkflowAttachmentsComponent } from '../../../../../../actra/components/workflow-attachments/workflow-attachments.component';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { filter, map, repeat, switchMap, tap } from 'rxjs';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { AttributesEditorComponent } from '@osee/shared/components';
import { WorkflowService } from '../../../../../../actra/services/workflow.service';
import { MatIcon } from '@angular/material/icon';
import { NgClass } from '@angular/common';
import { TransactionService } from '@osee/transactions/services';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import {
	CommitManagerButtonComponent,
	CreateActionWorkingBranchButtonComponent,
} from '@osee/configuration-management/components';
import { attribute } from '@osee/shared/types';
import {
	legacyAttributeType,
	legacyModifyArtifact,
	legacyTransaction,
} from '@osee/transactions/types';
import { ActionDropDownComponent } from '@osee/configuration-management/components';
import { ActionService } from '@osee/configuration-management/services';
import { UpdateFromParentButtonComponent } from '@osee/commit/components';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
	selector: 'osee-team-workflow-tab',
	imports: [
		NgClass,
		ExpansionPanelComponent,
		WorkflowAttachmentsComponent,
		CreateActionWorkingBranchButtonComponent,
		ActionDropDownComponent,
		AttributesEditorComponent,
		UpdateFromParentButtonComponent,
		MatButton,
		MatIcon,
		MatTooltip,
		NgClass,
		CommitManagerButtonComponent,
	],
	templateUrl: './team-workflow-tab.component.html',
})
export class TeamWorkflowTabComponent {
	teamWorkflowId = input.required<`${number}`>();
	teamWorkflowId$ = toObservable(this.teamWorkflowId);

	teamWorkflow = toSignal(
		this.teamWorkflowId$.pipe(
			switchMap((id) =>
				this.actionService.getTeamWorkflowDetails(id).pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter(
									(updatedId) => updatedId === id.toString()
								)
							),
					})
				)
			)
		),
		{ initialValue: new teamWorkflowDetailsImpl() }
	);

	teamWorkflow$ = toObservable(this.teamWorkflow);

	actionService = inject(ActionService);
	artifactService = inject(ArtifactExplorerHttpService);
	twService = inject(WorkflowService);
	txService = inject(TransactionService);
	uiService = inject(UiService);
	routeUrl = inject(ActivatedRoute);
	router = inject(Router);
	branchedRouter = inject(BranchRoutedUIService);

	assigneesString = computed(() =>
		this.teamWorkflow()
			.Assignees.map((assignee) => assignee.name)
			.join(', ')
	);

	updatedAttributes = signal<attribute[]>([]);
	hasChanges = computed(() => this.updatedAttributes().length > 0);

	workDef = toSignal(
		this.teamWorkflow$.pipe(
			filter((teamwf) => teamwf.id !== 0),
			switchMap((teamwf) =>
				this.actionService.getWorkDefinition(teamwf.id)
			)
		)
	);

	previousStates = computed(() => this.teamWorkflow().previousStates);

	twAttributeTypes = toSignal(
		this.teamWorkflow$.pipe(
			switchMap((_) =>
				this.twService.allTeamWorkflowAttributes.pipe(
					map((attrs) => structuredClone(attrs))
				)
			)
		)
	);

	stateAttributes = computed(() => {
		const states = new Map<string, attribute[]>();
		if (!this.twAttributeTypes()) {
			return states;
		}
		this.teamWorkflow().previousStates.forEach((state) => {
			const attrIds = this.workDef()
				?.states.find((s) => s.name === state.state)
				?.layoutItems.filter(
					(item) =>
						item.attributeType !== null &&
						item.attributeType !== '-1'
				)
				.map((item) => item.attributeType);

			if (!attrIds) {
				return;
			}

			const attributes: attribute[] = [];
			attrIds.forEach((attrId) => {
				let attr = this.teamWorkflow().artifact.attributes.find(
					(a) => a.typeId === attrId
				);
				if (attr) {
					attributes.push(attr);
					return;
				}
				attr = this.twAttributeTypes()?.find(
					(a) => a.typeId === attrId
				);
				if (attr) {
					attributes.push(attr);
				}
			});
			states.set(state.state, attributes);
			return;
		});

		return states;
	});

	handleUpdatedAttributes(updatedAttributes: attribute[]) {
		updatedAttributes.forEach((attr) => {
			const index = this.updatedAttributes().findIndex(
				(a) => a.typeId === attr.typeId
			);
			if (index >= 0) {
				this.updatedAttributes.update((current) => {
					current[index] = attr;
					return current;
				});
			} else {
				this.updatedAttributes.update((current) => [...current, attr]);
			}
		});
	}

	saveChanges() {
		if (!this.hasChanges()) {
			return;
		}
		const tx: legacyTransaction = {
			branch: '570',
			txComment:
				'Attribute changes for team workflow: ' +
				this.teamWorkflow().AtsId,
		};
		const attributes: legacyAttributeType[] = this.updatedAttributes().map(
			(attr) => {
				return { typeId: attr.typeId, value: attr.value };
			}
		);
		const modifyArtifact: legacyModifyArtifact = {
			id: `${this.teamWorkflow().id}`,
			setAttributes: attributes,
		};
		tx.modifyArtifacts = [modifyArtifact];
		this.txService
			.performMutation(tx)
			.pipe(
				tap((res) => {
					if (res.results.success) {
						this.updatedAttributes.set([]);
						this.updateTeamWorkflow();
					}
				})
			)
			.subscribe();
	}

	updateTeamWorkflow() {
		this.uiService.updatedArtifact = `${this.teamWorkflowId()}`;
	}

	openInArtifactExplorer() {
		this.router.navigate([], {
			queryParams: { panel: 'Artifacts' },
			relativeTo: this.routeUrl,
		});
		this.branchedRouter.position = {
			id: this.teamWorkflow().workingBranch.id,
			type: 'working',
		};
	}
}
