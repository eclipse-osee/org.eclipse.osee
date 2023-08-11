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
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import {
	CurrentTransportTypeService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type { transportType } from '@osee/messaging/shared/types';
import {
	EditTransportTypeDialogComponent,
	NewTransportTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';

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
		MatTableModule,
		MatTooltipModule,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
		MatMenuModule,
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
					this.ui.typeValue = params.get('branchType') || '';
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
