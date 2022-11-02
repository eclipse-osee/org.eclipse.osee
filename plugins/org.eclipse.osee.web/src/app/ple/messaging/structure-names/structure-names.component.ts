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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { UiService } from '../../../ple-services/ui/ui.service';
import { HttpLoadingService } from '../../../services/http-loading.service';
import { CurrentStructureNamesService } from '../shared/services/ui/current-structure-names.service';

@Component({
	selector: 'osee-messaging-structure-names',
	templateUrl: './structure-names.component.html',
	styleUrls: ['./structure-names.component.sass'],
})
export class StructureNamesComponent implements OnInit {
	_filter = new BehaviorSubject<string>('');
	names = combineLatest([this.structureService.names, this._filter]).pipe(
		map(([names, filter]) =>
			filter !== ''
				? names.filter(
						(path) =>
							path.name.toLowerCase().includes(filter) ||
							path.paths
								.map((p) => p.name.toLowerCase())
								.flat()
								.includes(filter)
				  )
				: names
		)
	);
	basePath = combineLatest([this.uiService.id, this.uiService.type]).pipe(
		switchMap(([id, type]) => of(`../../../${type}/${id}`))
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

	applyFilter(event: Event) {
		this._filter.next(
			(event.target as HTMLInputElement).value.toLowerCase()
		);
	}
}
