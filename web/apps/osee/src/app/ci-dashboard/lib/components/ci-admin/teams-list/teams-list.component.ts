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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { PageEvent } from '@angular/material/paginator';
import { NamedId } from '@osee/shared/types';
import { combineLatest, debounceTime, switchMap } from 'rxjs';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { DashboardService } from '../../../services/dashboard.service';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { NamedIdListEditorComponent } from '@osee/shared/components';
import { AsyncPipe } from '@angular/common';

@Component({
	selector: 'osee-teams-list',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [NamedIdListEditorComponent, AsyncPipe],
	template: `
		<osee-named-id-list-editor
			[name]="'Teams'"
			[allowedToEdit]="branchType() === 'working'"
			[dataToDisplay]="(teams | async) || []"
			[pageIndex]="teamsPageNum()"
			[pageSize]="teamsPageSize()"
			[count]="(teamsCount | async) || 0"
			(pageEvent)="updateTeamsPage($event)"
			(filterChange)="updateTeamFilter($event)"
			(namedIdEdit)="updateTeam($event)"
			(createNew)="createTeam($event)" />
	`,
})
export class TeamsListComponent {
	uiService = inject(CiDashboardUiService);
	dashboardService = inject(DashboardService);

	branchType = toSignal(this.uiService.branchType);

	teamsFilter = signal('');
	teamsPageSize = signal(100);
	teamsPageNum = signal(0);

	teamsFilter$ = toObservable(this.teamsFilter).pipe(debounceTime(250));

	teams = combineLatest([
		this.teamsFilter$,
		toObservable(this.teamsPageSize),
		toObservable(this.teamsPageNum),
	]).pipe(
		switchMap(([filter, pageSize, pageNum]) =>
			this.dashboardService.getTeamsPaginated(
				filter,
				pageNum,
				pageSize,
				ATTRIBUTETYPEIDENUM.NAME
			)
		)
	);

	teamsCount = this.teamsFilter$.pipe(
		switchMap((filter) => this.dashboardService.getTeamsCount(filter))
	);

	createTeam(name: string) {
		this.dashboardService.createTeam(name).subscribe();
	}

	updateTeam(value: NamedId) {
		this.dashboardService.updateArtifact(value).subscribe();
	}

	updateTeamFilter(value: string) {
		this.teamsFilter.set(value);
	}

	updateTeamsPage(pg: PageEvent) {
		this.teamsPageNum.set(pg.pageIndex);
		this.teamsPageSize.set(pg.pageSize);
	}
}
