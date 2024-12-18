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
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatAnchor } from '@angular/material/button';
import {
	MatAccordion,
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import {
	MatFormField,
	MatHint,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ConnectionDropdownComponent } from '@osee/messaging/shared/dropdowns';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { CurrentStructureNamesService } from '@osee/messaging/shared/services';
import { connection, connectionSentinel } from '@osee/messaging/shared/types';
import { UiService } from '@osee/shared/services';
import { HttpLoadingService } from '@osee/shared/services/network';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-structure-names',
	templateUrl: './structure-names.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	imports: [
		RouterLink,
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
		MatHint,
		MatAccordion,
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		MatAnchor,
		MessagingControlsComponent,
		ConnectionDropdownComponent,
	],
})
export class StructureNamesComponent implements OnInit {
	private uiService = inject(UiService);
	private structureService = inject(CurrentStructureNamesService);
	private route = inject(ActivatedRoute);
	private loadingService = inject(HttpLoadingService);

	_filter = new BehaviorSubject<string>('');
	// connection = new BehaviorSubject<connection>(connectionSentinel);
	protected connection = signal<connection>(connectionSentinel);
	private _connection = toObservable(this.connection);

	names = this._connection.pipe(
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

	protected computePathNames(
		paths: {
			name: string;
			path: string;
		}[]
	) {
		return paths.map((x) => x.name + ':' + x.path).join(',');
	}

	basePath = combineLatest([this.uiService.id, this.uiService.type]).pipe(
		switchMap(([id, type]) => of(`../../../connections/${type}/${id}`))
	);
	loading = this.loadingService.isLoading;

	ngOnInit(): void {
		this.route.paramMap
			.pipe(
				map((params) => {
					this.uiService.typeValue =
						(params.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					this.uiService.idValue = params.get('branchId') || '';
				})
			)
			.subscribe();
	}

	applyFilter(event: Event) {
		this._filter.next(
			(event.target as HTMLInputElement).value.toLowerCase()
		);
	}
}

export default StructureNamesComponent;
