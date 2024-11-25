/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { CdkDropList } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import {
	BranchPickerComponent,
	CurrentViewSelectorComponent,
} from '@osee/shared/components';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { concatMap, filter, from, map, take, tap } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactHierarchyOptionsComponent } from '../artifact-hierarchy-options/artifact-hierarchy-options.component';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { ArtifactSearchPanelComponent } from '../artifact-search-panel/artifact-search-panel.component';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { MatButton } from '@angular/material/button';
import { CurrentActionDropDownComponent } from '@osee/configuration-management/components';
import {
	ActionService,
	CreateActionService,
	CurrentActionService,
} from '@osee/configuration-management/services';

@Component({
	selector: 'osee-artifact-hierarchy-panel',
	imports: [
		AsyncPipe,
		BranchPickerComponent,
		ArtifactHierarchyComponent,
		ArtifactHierarchyOptionsComponent,
		CurrentViewSelectorComponent,
		CurrentActionDropDownComponent,
		ArtifactSearchPanelComponent,
		ExpansionPanelComponent,
		MatTooltip,
		MatIcon,
		MatButton,
		CdkDropList,
	],
	templateUrl: './artifact-hierarchy-panel.component.html',
})
export class ArtifactHierarchyPanelComponent {
	private artHierPathService = inject(ArtifactHierarchyPathService);
	private tabService = inject(ArtifactExplorerTabService);
	private currentBranchService = inject(CurrentBranchInfoService);
	private currentActionService = inject(CurrentActionService);
	private createActionService = inject(CreateActionService);
	private actionService = inject(ActionService);

	private uiService = inject(UiService);
	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });
	protected paths = this.artHierPathService.getPaths();

	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch.name)
		)
	);

	branchIdValid = computed(
		() =>
			this.branchId() !== '' &&
			this.branchId() !== '-1' &&
			this.branchId() !== '0'
	);

	// This signal listens for new actions created using the create action button,
	// and opens new tabs for those actions.
	private _openNewActionTab = toSignal(
		this.createActionService.createdTeamWorkflows.pipe(
			concatMap((tws) =>
				from(tws).pipe(
					concatMap((tw) =>
						this.actionService
							.searchTeamWorkflows({
								search: tw,
								searchByArtId: true,
							})
							.pipe(
								filter((tokens) => tokens.length === 1),
								tap((tokens) =>
									this.tabService.addTeamWorkflowTab(
										tokens[0]
									)
								)
							)
					)
				)
			)
		)
	);

	openChangeReport() {
		this.tabService.addChangeReportTab(
			'Change Report - ' + this.branchName()
		);
	}

	openTeamWorkflowTab() {
		this.currentActionService.branchWorkflowToken
			.pipe(
				take(1),
				tap((teamwf) => this.tabService.addTeamWorkflowTab(teamwf))
			)
			.subscribe();
	}

	displayWorkflowButtons = toSignal(
		this.currentActionService.branchWorkflowToken.pipe(
			map(
				(branchWorkflowToken) =>
					branchWorkflowToken.id !== undefined &&
					branchWorkflowToken.id !== '-1'
			)
		),
		{
			initialValue: false,
		}
	);
}
