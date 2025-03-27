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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	signal,
} from '@angular/core';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { ScriptTeam } from '../../../types/tmo';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { applicabilitySentinel } from '@osee/applicability/types';
import { FormsModule } from '@angular/forms';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton, MatMiniFabButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, debounceTime, filter, first, switchMap } from 'rxjs';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { UiService } from '@osee/shared/services';
import { DashboardService } from '../../../services/dashboard.service';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { CreateTeamDialogComponent } from './create-team-dialog/create-team-dialog.component';

@Component({
	selector: 'osee-teams-table',
	imports: [
		FormsModule,
		MatTable,
		MatColumnDef,
		MatCell,
		MatCellDef,
		MatRow,
		MatRowDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatTooltip,
		MatIcon,
		MatIconButton,
		MatMiniFabButton,
		MatFormField,
		MatInput,
		MatPaginator,
		PersistedStringAttributeInputComponent,
	],
	templateUrl: './teams-table.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeamsTableComponent {
	private dashboardService = inject(DashboardService);
	private dialog = inject(MatDialog);
	private uiService = inject(UiService);

	branchType = toSignal(this.uiService.type);
	editable = computed(() => this.branchType() === 'working');

	applic = applicabilitySentinel;
	headers = ['Name', ' '];

	pageNum = signal(0);
	pageSize = signal(25);
	private _pageNum$ = toObservable(this.pageNum);
	private _pageSize$ = toObservable(this.pageSize);

	filter = signal('');
	private _filter$ = toObservable(this.filter);

	teams = toSignal(
		combineLatest([this._filter$, this._pageNum$, this._pageSize$]).pipe(
			debounceTime(250),
			switchMap(([filter, pageNum, pageSize]) =>
				this.dashboardService.getTeamsPaginated(
					filter,
					pageNum + 1,
					pageSize,
					ATTRIBUTETYPEIDENUM.NAME
				)
			)
		),
		{ initialValue: [] }
	);

	teamsCount = toSignal(
		this._filter$.pipe(
			switchMap((filter) => this.dashboardService.getTeamsCount(filter))
		)
	);

	setPage(event: PageEvent) {
		this.pageSize.set(event.pageSize);
		this.pageNum.set(event.pageIndex);
	}

	openNewTeamDialog() {
		const dialogData: ScriptTeam = {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				gammaId: '-1',
				typeId: '1152921504606847088',
				value: '',
			},
		};
		const dialogRef = this.dialog.open(CreateTeamDialogComponent, {
			data: dialogData,
			minWidth: '80vw',
		});
		dialogRef
			.afterClosed()
			.pipe(
				first(),
				filter((val) => val !== undefined),
				switchMap((val) =>
					this.dashboardService.createTeam(val.name.value)
				)
			)
			.subscribe();
	}

	deleteTeam(team: ScriptTeam) {
		this.dashboardService.deleteTeam(team);
	}
}
