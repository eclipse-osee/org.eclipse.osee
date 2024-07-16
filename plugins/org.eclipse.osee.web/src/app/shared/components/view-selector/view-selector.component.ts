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
import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
	ApplicabilityListUIService,
	ViewsRoutedUiService,
} from '@osee/shared/services';
import { applic } from '@osee/shared/types/applicability';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	from,
	of,
	scan,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-view-selector',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
	],
	templateUrl: './view-selector.component.html',
	styles: [],
})
export class ViewSelectorComponent {
	constructor(
		private applicService: ApplicabilityListUIService,
		private viewsService: ViewsRoutedUiService
	) {}

	filterText = new BehaviorSubject<string>('');

	noneOption = { id: '-1', name: 'None' } as applic;

	views = combineLatest([this.applicService.views, this.filterText]).pipe(
		switchMap(([applics, filterText]) =>
			from(applics).pipe(
				filter((a) =>
					a.name.toLowerCase().includes(filterText.toLowerCase())
				),
				scan((acc, curr) => {
					acc.push(curr);
					return acc;
				}, [] as applic[])
			)
		)
	);

	selectedView = combineLatest([this.views, this.viewsService.viewId]).pipe(
		switchMap(([views, viewId]) => {
			const view = views.find((v) => v.id === viewId);
			return view ? of(view) : of(this.noneOption);
		})
	);

	selectView(view: applic) {
		this.viewsService.ViewId = view.id;
	}

	applyFilter(text: Event) {
		const value = (text.target as HTMLInputElement).value;
		this.filterText.next(value);
	}
}
