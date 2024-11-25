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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, OnInit, viewChild, inject } from '@angular/core';
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
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { HeaderService } from '@osee/messaging/shared/services';
import type { transportType } from '@osee/messaging/shared/types';
import {
	EditTransportTypeDialogComponent,
	NewTransportTypeDialogComponent,
} from '@osee/messaging/transports/dialogs';
import { UiService } from '@osee/shared/services';
import { Subject } from 'rxjs';
import { filter, switchMap, take, tap } from 'rxjs/operators';
import { CurrentTransportTypePageService } from './lib/services/current-transport-type-page.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AttributeToValuePipe } from '@osee/attributes/pipes';

@Component({
	selector: 'osee-transports',
	templateUrl: './transports.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	imports: [
		AsyncPipe,
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
		AttributeToValuePipe,
	],
})
export class TransportsComponent implements OnInit {
	private transportTypesService = inject(CurrentTransportTypePageService);
	private headerService = inject(HeaderService);
	private ui = inject(UiService);
	private route = inject(ActivatedRoute);
	dialog = inject(MatDialog);

	private _done = new Subject();
	menuPosition = {
		x: '0',
		y: '0',
	};
	matMenuTrigger = viewChild.required(MatMenuTrigger);
	transports =
		this.transportTypesService.transportTypes.pipe(takeUntilDestroyed());
	headers =
		this.headerService.AllTransportTypeHeaders.pipe(takeUntilDestroyed());

	valueTracker(index: number, _item: unknown) {
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
			.open(NewTransportTypeDialogComponent, {
				minWidth: '70vw',
				minHeight: '80vh',
			})
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
		this.matMenuTrigger().menuData = {
			transport: type,
		};
		this.matMenuTrigger().openMenu();
	}

	openEditDialog(type: transportType) {
		const previous = structuredClone(type);
		this.dialog
			.open(EditTransportTypeDialogComponent, {
				data: type,
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is transportType => value !== undefined),
				switchMap((type) =>
					this.transportTypesService.modifyType(type, previous)
				)
			)
			.subscribe();
	}
}

export default TransportsComponent;
