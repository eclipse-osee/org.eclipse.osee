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
import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { UserDataAccountService } from '@osee/auth';
import { ActionService } from '@osee/configuration-management/services';
import { teamWorkflowToken } from '@osee/shared/types/configuration-management';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	switchMap,
	tap,
} from 'rxjs';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { PaginatedMatListComponent } from '../shared/paginated-mat-list/paginated-mat-list.component';

@Component({
	selector: 'osee-actions-panel',
	standalone: true,
	imports: [
		ExpansionPanelComponent,
		PaginatedMatListComponent,
		MatFormField,
		FormsModule,
		MatLabel,
		MatInput,
		AsyncPipe,
	],
	templateUrl: './actions-panel.component.html',
})
export class ActionsPanelComponent {
	private tabService = inject(ArtifactExplorerTabService);
	private actionService = inject(ActionService);
	private userService = inject(UserDataAccountService);

	pageNum = new BehaviorSubject<number>(1);
	pageSize = 20;

	searchText = new BehaviorSubject<string>('');

	teamWorkflows = signal<teamWorkflowToken[]>([]);

	paginatedTeamWorkflows = toSignal(
		combineLatest([
			this.userService.user,
			this.pageNum,
			this.searchText,
		]).pipe(
			debounceTime(250),
			switchMap(([user, pageNum, search]) =>
				this.actionService
					.getTeamWorkflowsForUser(
						search,
						user.id,
						this.pageSize,
						pageNum
					)
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
		combineLatest([this.userService.user, this.searchText]).pipe(
			debounceTime(250),
			switchMap(([user, search]) =>
				this.actionService.getTeamWorkflowsForUserCount(search, user.id)
			)
		),
		{ initialValue: -1 }
	);

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

	updateFilter(event: KeyboardEvent) {
		this.teamWorkflows.set([]);
		this.pageNum.next(1);
		const filterValue = (event.target as HTMLInputElement).value;
		this.searchText.next(filterValue);
	}
}
