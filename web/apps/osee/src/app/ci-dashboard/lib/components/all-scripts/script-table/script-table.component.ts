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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	viewChild,
} from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { scriptDefHeaderDetails } from '../../../table-headers/script-headers';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
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
import { MatTooltip } from '@angular/material/tooltip';
import { HeaderService } from '@osee/shared/services';
import { CiDetailsService } from '../../../services/ci-details.service';
import type { DefReference } from '../../../types/tmo';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SubsystemSelectorComponent } from '../../subsystem-selector/subsystem-selector.component';
import { TeamSelectorComponent } from '../../team-selector/team-selector.component';

@Component({
	selector: 'osee-script-table',
	template: `<div
		class="mat-elevation-z8 tw-h-[76vh] tw-w-screen tw-overflow-auto">
		@if (scriptDefs | async; as _defs) {
			<mat-table [dataSource]="_defs">
				@for (header of headers; track $index) {
					<ng-container [matColumnDef]="header">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-center tw-align-middle tw-font-medium tw-text-primary-600"
							[matTooltip]="
								(getTableHeaderByName(header) | async)
									?.description || ''
							">
							{{
								(getTableHeaderByName(header) | async)
									?.humanReadable || ''
							}}
						</th>
						<td
							mat-cell
							*matCellDef="let def"
							class="tw-align-middle">
							@if (header === 'name') {
								<button
									mat-list-item
									(click)="resultList(def.id)"
									class="tw-text-primary">
									{{ def[header] }}
								</button>
							} @else if (header === 'subsystem') {
								<osee-subsystem-selector [script]="def" />
							} @else if (header === 'team') {
								<osee-team-selector [script]="def" />
							} @else {
								{{ def[header] }}
							}
						</td>
					</ng-container>
				}
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button even:tw-bg-background-background"
					[attr.data-cy]="'script-def-table-row-' + row.name"></tr>
			</mat-table>
		}
	</div>`,
	imports: [
		AsyncPipe,
		SubsystemSelectorComponent,
		TeamSelectorComponent,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScriptTableComponent {
	ciDetailsService = inject(CiDetailsService);
	ciDashboardService = inject(CiDashboardUiService);
	headerService = inject(HeaderService);
	dialog = inject(MatDialog);
	router = inject(Router);

	matMenuTrigger = viewChild.required(MatMenuTrigger);

	scriptDefs = this.ciDetailsService.scriptDefs.pipe(takeUntilDestroyed());
	ciSetId = this.ciDashboardService.ciSetId;

	protected filter = this.ciDetailsService.filter;

	resultList(defId: string) {
		let url = this.router.url;
		url = url.replace('allScripts', 'details');
		this.ciDetailsService.CiDefId = defId;
		this.router.navigateByUrl(url);
	}

	applyFilter(_event: Event) {
		//TODO: stephen,ryan finish this?
		// const filterValue = (event.target as HTMLInputElement).value;
	}

	menuPosition = {
		x: '0',
		y: '0',
	};

	openMenu(event: MouseEvent, defRef: DefReference) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger().menuData = {
			defRef: defRef,
		};
		this.matMenuTrigger().openMenu();
	}

	getTableHeaderByName(header: keyof DefReference) {
		return this.headerService.getHeaderByName(
			scriptDefHeaderDetails,
			header
		);
	}

	headers: (keyof DefReference)[] = [
		'name',
		'team',
		'subsystem',
		'safety',
		'statusBy',
		'statusDate',
		'latestResult',
		'latestPassedCount',
		'latestFailedCount',
		'latestScriptAborted',
		'machineName',
		'latestElapsedTime',
		'fullScriptName',
		'notes',
	];
}
