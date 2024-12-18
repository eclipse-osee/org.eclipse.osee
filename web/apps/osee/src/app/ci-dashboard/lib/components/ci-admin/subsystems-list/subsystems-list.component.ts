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
	selector: 'osee-subsystems-list',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [NamedIdListEditorComponent, AsyncPipe],
	template: `
		<osee-named-id-list-editor
			[name]="'Subsystems'"
			[allowedToEdit]="branchType() === 'working'"
			[dataToDisplay]="(subsystems | async) || []"
			[pageIndex]="subsystemsPageNum()"
			[pageSize]="subsystemsPageSize()"
			[count]="(subsystemsCount | async) || 0"
			(pageEvent)="updateSubsystemsPage($event)"
			(filterChange)="updateSubsystemFilter($event)"
			(namedIdEdit)="updateSubsystem($event)"
			(createNew)="createSubsystem($event)" />
	`,
})
export class SubsystemsListComponent {
	uiService = inject(CiDashboardUiService);
	dashboardService = inject(DashboardService);

	branchType = toSignal(this.uiService.branchType);

	subsystemsFilter = signal('');
	subsystemsPageSize = signal(100);
	subsystemsPageNum = signal(0);

	subsystemsFilter$ = toObservable(this.subsystemsFilter).pipe(
		debounceTime(250)
	);

	subsystems = combineLatest([
		this.subsystemsFilter$,
		toObservable(this.subsystemsPageSize),
		toObservable(this.subsystemsPageNum),
	]).pipe(
		switchMap(([filter, pageSize, pageNum]) =>
			this.dashboardService.getSubsystemsPaginated(
				filter,
				pageNum,
				pageSize,
				ATTRIBUTETYPEIDENUM.NAME
			)
		)
	);

	subsystemsCount = this.subsystemsFilter$.pipe(
		switchMap((filter) => this.dashboardService.getSubsystemsCount(filter))
	);

	createSubsystem(name: string) {
		this.dashboardService.createSubsystem(name).subscribe();
	}

	updateSubsystem(value: NamedId) {
		this.dashboardService.updateArtifact(value).subscribe();
	}

	updateSubsystemFilter(value: string) {
		this.subsystemsFilter.set(value);
	}

	updateSubsystemsPage(pg: PageEvent) {
		this.subsystemsPageNum.set(pg.pageIndex);
		this.subsystemsPageSize.set(pg.pageSize);
	}
}
