/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, signal } from '@angular/core';
import { ActionService } from '@osee/shared/services';
import { ArtifactExplorerExpansionPanelComponent } from '../shared/artifact-explorer-expansion-panel/artifact-explorer-expansion-panel.component';
import { UserDataAccountService } from '@osee/auth';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	switchMap,
	tap,
} from 'rxjs';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { teamWorkflowToken } from '@osee/shared/types/configuration-management';
import { PaginatedMatListComponent } from '../shared/paginated-mat-list/paginated-mat-list.component';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-actions-panel',
	standalone: true,
	imports: [
		ArtifactExplorerExpansionPanelComponent,
		PaginatedMatListComponent,
	],
	templateUrl: './actions-panel.component.html',
})
export class ActionsPanelComponent {
	pageNum = new BehaviorSubject<number>(1);
	pageSize = 20;

	teamWorkflows = signal<teamWorkflowToken[]>([]);

	paginatedTeamWorkflows = toSignal(
		combineLatest([this.userService.user, this.pageNum]).pipe(
			switchMap(([user, pageNum]) =>
				this.actionService
					.getTeamWorkflowsForUser(user.id, this.pageSize, pageNum)
					.pipe(
						tap((results) =>
							this.teamWorkflows.update((current) => [
								...current,
								...results,
							])
						)
					)
			)
		),
		{ initialValue: [] }
	);

	teamWorkflowCount = toSignal(
		this.userService.user.pipe(
			switchMap((user) =>
				this.actionService.getTeamWorkflowsForUserCount(user.id)
			)
		),
		{ initialValue: -1 }
	);

	constructor(
		private tabService: ArtifactExplorerTabService,
		private actionService: ActionService,
		private userService: UserDataAccountService
	) {}

	openInTab(teamwfId: `${number}`) {
		this.actionService
			.searchTeamWorkflows({ search: teamwfId, searchByArtId: true })
			.pipe(
				filter((teamWfs) => teamWfs.length === 1),
				tap((teamWfs) => this.tabService.addTeamWorkflowTab(teamWfs[0]))
			)
			.subscribe();
	}

	nextPage() {
		this.pageNum.next(this.pageNum.getValue() + 1);
	}
}
