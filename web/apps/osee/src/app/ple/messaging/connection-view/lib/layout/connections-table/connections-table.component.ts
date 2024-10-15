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
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, OnDestroy, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ConnectionService } from '@osee/messaging/shared/services';
import { connectionHeaderDetails } from '@osee/messaging/shared/table-headers';
import { connection } from '@osee/messaging/shared/types';
import { HeaderService, UiService } from '@osee/shared/services';
import { Subject, switchMap, takeUntil } from 'rxjs';

@Component({
	selector: 'osee-connections-table',
	standalone: true,
	imports: [
		NgClass,
		AsyncPipe,
		MatTable,
		MatCell,
		MatHeaderCell,
		MatCellDef,
		MatRow,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatColumnDef,
		MatInput,
		MatIcon,
		MatTooltip,
		MatButton,
	],
	templateUrl: './connections-table.component.html',
	animations: [
		trigger('detailExpand', [
			state('collapsed', style({ maxHeight: '0vh' })),
			state('expanded', style({ maxHeight: '60vh' })),
			transition(
				'expanded <=> collapsed',
				animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')
			),
		]),
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(0)' })),
			state('open', style({ transform: 'rotate(-180deg)' })),
			transition(
				'open => closed',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
			transition(
				'closed => open',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
})
export class ConnectionsTableComponent implements OnDestroy {
	private connectionService = inject(ConnectionService);
	private uiService = inject(UiService);
	private headerService = inject(HeaderService);

	private _done = new Subject<void>();

	data = this.uiService.id.pipe(
		switchMap((id) =>
			this.connectionService
				.getConnections(id)
				.pipe(takeUntil(this._done))
		)
	);

	headers: (keyof connection)[] = [
		'name',
		'transportType',
		'description',
		'applicability',
	];

	expandedRows: string[] = [];

	rowIsExpanded(id: string) {
		return this.expandedRows.includes(id);
	}

	toggleExpanded(id: string, expanded: boolean) {
		if (!expanded && this.rowIsExpanded(id)) {
			this.expandedRows.splice(this.expandedRows.indexOf(id));
		} else {
			this.expandedRows.push(id);
		}
	}

	getTableHeaderByName(header: keyof connection) {
		return this.headerService.getHeaderByName(
			connectionHeaderDetails,
			header
		);
	}

	ngOnDestroy(): void {
		this._done.next();
		this._done.complete();
	}
}
