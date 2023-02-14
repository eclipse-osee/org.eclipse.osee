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
import { AsyncPipe, NgClass, NgFor } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { UiService } from '../../../ple-services/ui/ui.service';
import {
	CurrentTransportTypeService,
	HeaderService,
	NewTransportTypeDialogComponent,
} from '@osee/messaging/shared';
import type { transportType } from '@osee/messaging/shared';
import {
	BranchPickerComponent,
	ActionDropDownComponent,
} from '@osee/shared/components';

@Component({
	selector: 'osee-transports',
	templateUrl: './transports.component.html',
	styleUrls: ['./transports.component.sass'],
	standalone: true,
	imports: [
		AsyncPipe,
		NgFor,
		NgClass,
		MatTableModule,
		MatTooltipModule,
		MatButtonModule,
		MatIconModule,
		MatDialogModule,
		BranchPickerComponent,
		ActionDropDownComponent,
	],
})
export class TransportsComponent implements OnInit, OnDestroy {
	private _done = new Subject();
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
}

export default TransportsComponent;
