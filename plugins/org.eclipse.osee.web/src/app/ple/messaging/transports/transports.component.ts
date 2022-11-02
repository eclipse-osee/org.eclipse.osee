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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { UiService } from '../../../ple-services/ui/ui.service';
import { NewTransportTypeDialogComponent } from '../shared/components/dialogs/new-transport-type-dialog/new-transport-type-dialog.component';
import { CurrentTransportTypeService } from '../shared/services/ui/current-transport-type.service';
import { HeaderService } from '../shared/services/ui/header.service';
import { transportType } from '../shared/types/transportType';

@Component({
	selector: 'osee-transports',
	templateUrl: './transports.component.html',
	styleUrls: ['./transports.component.sass'],
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
