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
import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UiService } from '@osee/shared/services';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, filter, iif, of, switchMap, take, tap } from 'rxjs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { CrossReference, connection } from '@osee/messaging/shared/types';
import {
	CrossReferenceTableComponent,
	NewCrossReferenceDialogComponent,
} from '@osee/messaging/cross-reference';
import { CrossReferenceService } from '@osee/messaging/shared/services';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';

@Component({
	selector: 'osee-cross-reference',
	standalone: true,
	imports: [
		CommonModule,
		CrossReferenceTableComponent,
		FormsModule,
		MatButtonModule,
		MatFormFieldModule,
		MatIconModule,
		MatSelectModule,
		RouterLink,
		MessagingControlsComponent,
	],
	templateUrl: './cross-reference.component.html',
})
export class CrossReferenceComponent implements OnInit, OnDestroy {
	constructor(
		private route: ActivatedRoute,
		private ui: UiService,
		private crossRefService: CrossReferenceService,
		public dialog: MatDialog
	) {}

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			this.ui.idValue = params.get('branchId') || '';
			this.ui.typeValue = params.get('branchType') || '';
			this.SelectedConnectionId = params.get('connectionId') || '';
		});
	}

	ngOnDestroy(): void {
		this.SelectedConnectionId = '';
	}

	openAddDialog() {
		this.dialog
			.open(NewCrossReferenceDialogComponent)
			.afterClosed()
			.pipe(
				take(1),
				filter((value): value is CrossReference => value !== undefined),
				switchMap((crossRef) =>
					this.crossRefService
						.createCrossReference(crossRef)
						.pipe(tap((_) => (this.ui.updated = true)))
				)
			)
			.subscribe();
	}

	compareConnections(conn1: connection, conn2: connection) {
		if (conn1 && conn2) {
			return conn1.id === conn2.id;
		}
		return false;
	}

	branchId = this.ui.id;
	branchType = this.ui.type;

	connections = this.crossRefService.connections;

	connectionSelectionText = this.connections.pipe(
		switchMap((connections) =>
			iif(
				() => connections.length > 0,
				of('Select a Connection'),
				of('No connections available')
			)
		)
	);

	connectionRoute = combineLatest([this.branchId, this.branchType]).pipe(
		switchMap(([branchId, branchType]) =>
			of(
				'/ple/messaging/crossreference/' +
					branchType +
					'/' +
					branchId +
					'/'
			)
		)
	);

	inEditMode = this.crossRefService.inEditMode;

	get selectedConnection() {
		return this.crossRefService.selectedConnection;
	}

	set SelectedConnection(connection: connection) {
		this.SelectedConnectionId = connection.id || '';
	}

	get selectedConnectionId() {
		return this.crossRefService.selectedConnectionId;
	}

	set SelectedConnectionId(connectionId: string) {
		this.crossRefService.SelectedConnectionId = connectionId;
	}
}

export default CrossReferenceComponent;
