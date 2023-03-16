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
import { CurrentStructureNamesService } from '@osee/messaging/shared';
import { BranchPickerComponent } from '@osee/shared/components';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { UiService } from '../../../ple-services/ui/ui.service';
import { HttpLoadingService } from '../../../services/http-loading.service';
@Component({
	selector: 'osee-messaging-structure-names',
	templateUrl: './structure-names.component.html',
	styleUrls: ['./structure-names.component.sass'],
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
		BranchPickerComponent,
	],
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

	applyFilter(event: Event) {
		this._filter.next(
			(event.target as HTMLInputElement).value.toLowerCase()
		);
	}
}

export default StructureNamesComponent;
