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
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { filter, map, repeat, switchMap, take, tap } from 'rxjs';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { AttributesEditorComponent } from '@osee/shared/components';
import { TeamWorkflowService } from '../ple/artifact-explorer/lib/services/team-workflow.service';
import { MatIcon } from '@angular/material/icon';
import { NgClass } from '@angular/common';
import { TransactionService } from '@osee/transactions/services';
import { ArtifactExplorerHttpService } from '../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { CreateActionWorkingBranchButtonComponent } from '@osee/configuration-management/components';
import { attribute } from '@osee/shared/types';
import {
	legacyAttributeType,
	legacyModifyArtifact,
	legacyTransaction,
} from '@osee/transactions/types';
import { ActionDropDownComponent } from '@osee/configuration-management/components';
import { ActionService } from '@osee/configuration-management/services';
import {
	CommitManagerDialogComponent,
	UpdateFromParentButtonComponent,
} from '@osee/commit/components';
import { MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ViewEncapsulation } from '@angular/core';

@Component({
	selector: 'osee-wfe',
	/** should this be stand-alone? error if not! these should be included in app and not have to be re-imported? */
	standalone: true,
	imports: [
		NgClass,
		ExpansionPanelComponent,
		CreateActionWorkingBranchButtonComponent,
		ActionDropDownComponent,
		AttributesEditorComponent,
		UpdateFromParentButtonComponent,
		MatButton,
		MatIcon,
		MatTooltip,
		NgClass,
	],
	templateUrl: './actra-teamwf.component.html',
	styleUrls: ['./actra-teamwf.component.css'],
	encapsulation: ViewEncapsulation.None, // ← required to override Material CSS from component styles
})
export class ActraTeamwfComponent {
	actionService = inject(ActionService);
	artifactService = inject(ArtifactExplorerHttpService);
	twService = inject(TeamWorkflowService);
	txService = inject(TransactionService);
	uiService = inject(UiService);
	routeUrl = inject(ActivatedRoute);
	router = inject(Router);
	branchedRouter = inject(BranchRoutedUIService);
	dialog = inject(MatDialog);

	teamWorkflowId = input.required<`${number}`>();
	teamWorkflowId$ = this.routeUrl.queryParamMap.pipe(
		map((params) => params.get('id')),
		filter((id): id is string => !!id) // filters out null
	);

	get wf() {
		return this.teamWorkflow();
	}

	teamWorkflow = toSignal(
		this.teamWorkflowId$.pipe(
			switchMap((id) => {
				console.log('Fetching for ID:', id); // debug log
				return this.actionService.getTeamWorkflowDetails(id).pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter((updatedId) => updatedId === id)
							),
					})
				);
			})
		),
		{ initialValue: new teamWorkflowDetailsImpl() }
	);

	teamWorkflow$ = toObservable(this.teamWorkflow);

	allBranchesCommitted = computed(
		() => this.wf.branchesToCommitTo.length === 0
	);

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

	openCommitManager() {
		this.teamWorkflow$
			.pipe(
				take(1),
				switchMap((teamWf) =>
					this.dialog
						.open(CommitManagerDialogComponent, {
							data: teamWf,
							minWidth: '60%',
							width: '60%',
						})
						.afterClosed()
				)
			)
			.subscribe();
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
