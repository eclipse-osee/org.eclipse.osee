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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrentStructureNamesService } from '@osee/messaging/shared/services';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { HttpLoadingService } from '@osee/shared/services/network';
import { UiService } from '@osee/shared/services';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { ConnectionDropdownComponent } from 'src/app/ple/messaging/shared/dropdowns/connection-dropdown/connection-dropdown.component';
import { connection, connectionSentinel } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-structure-names',
	templateUrl: './structure-names.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		RouterLink,
		AsyncPipe,
		FormsModule,
		MatButtonModule,
		MatInputModule,
		MatFormFieldModule,
		MatIconModule,
		MatExpansionModule,
		MessagingControlsComponent,
		ConnectionDropdownComponent,
	],
})
export class StructureNamesComponent implements OnInit {
	_filter = new BehaviorSubject<string>('');
	connection = new BehaviorSubject<connection>(connectionSentinel);

	names = this.connection.pipe(
		switchMap((connection) =>
			this.structureService.getStructureNames(connection.id || '-1').pipe(
				switchMap((names) =>
					this._filter.pipe(
						map((filter) =>
							filter !== ''
								? names.filter(
										(path) =>
											path.name
												.toLowerCase()
												.includes(filter) ||
											path.paths
												.map((p) =>
													p.name.toLowerCase()
												)
												.flat()
												.includes(filter)
								  )
								: names
						)
					)
				)
			)
		)
	);

	basePath = combineLatest([
		this.uiService.id,
		this.uiService.type,
		this.uiService.viewId,
	]).pipe(
		switchMap(([id, type, viewId]) =>
			of(`../../../connections/${type}/${id}/${viewId}`)
		)
	);
	loading = this.loadingService.isLoading;

	constructor(
		private uiService: UiService,
		private structureService: CurrentStructureNamesService,
		private route: ActivatedRoute,
		private loadingService: HttpLoadingService
	) {}

	ngOnInit(): void {
		this.route.paramMap
			.pipe(
				map((params) => {
					this.uiService.typeValue = params.get('branchType') || '';
					this.uiService.idValue = params.get('branchId') || '';
				})
			)
			.subscribe();
	}

	selectConnection(value: connection) {
		this.connection.next(value);
	}

	applyFilter(event: Event) {
		this._filter.next(
			(event.target as HTMLInputElement).value.toLowerCase()
		);
	}
}

export default StructureNamesComponent;
