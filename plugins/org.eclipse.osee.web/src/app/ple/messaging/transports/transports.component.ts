/*********************************************************************
 * Copyright (c) 2022 Boeing
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
	AsyncPipe,
	NgClass,
	NgFor,
	NgSwitch,
	NgSwitchCase,
	NgSwitchDefault,
} from '@angular/common';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatFabButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
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
import { ActivatedRoute } from '@angular/router';
import {
	EditTransportTypeDialogComponent,
	NewTransportTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import {
	CurrentTransportTypeService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type { transportType } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil, tap } from 'rxjs/operators';

@Component({
	selector: 'osee-transports',
	templateUrl: './transports.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [
		AsyncPipe,
		NgFor,
		NgSwitch,
		NgSwitchCase,
		NgSwitchDefault,
		NgClass,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatTooltip,
		MatFabButton,
		MatIcon,
		MatMenu,
		MatMenuContent,
		MatMenuItem,
		MatMenuTrigger,
		MessagingControlsComponent,
	],
})
export class TransportsComponent implements OnInit, OnDestroy {
	private _done = new Subject();
	menuPosition = {
		x: '0',
		y: '0',
	};
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;
	transports = this.transportTypesService.transportTypes.pipe(
		takeUntil(this._done)
	);
	headers = this.headerService.AllTransportTypeHeaders.pipe(
		takeUntil(this._done)
	);
	constructor(
		private transportTypesService: CurrentTransportTypeService,
		private headerService: HeaderService,
		private ui: UiService,
		private route: ActivatedRoute,
		public dialog: MatDialog
	) {}
	ngOnDestroy(): void {
		this._done.next(true);
	}

	valueTracker(index: any, item: any) {
		return index;
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'transportType');
	}
	ngOnInit(): void {
		this.route.paramMap
			.pipe(
				tap((params) => {
					this.ui.typeValue =
						(params.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this.ui.idValue = params.get('branchId') || '';
				})
			)
			.subscribe();
	}

	openAddDialog() {
		this.dialog
			.open(NewTransportTypeDialogComponent)
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is transportType => value !== undefined),
				switchMap((type) => this.transportTypesService.createType(type))
			)
			.subscribe();
	}
	openMenu(event: MouseEvent, type: transportType) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			transport: type,
		};
		this.matMenuTrigger.openMenu();
	}

	openEditDialog(type: transportType) {
		this.dialog
			.open(EditTransportTypeDialogComponent, {
				data: type,
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is transportType => value !== undefined),
				switchMap((type) => this.transportTypesService.modifyType(type))
			)
			.subscribe();
	}
}

export default TransportsComponent;
