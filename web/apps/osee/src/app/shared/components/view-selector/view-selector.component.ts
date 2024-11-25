/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import {
	Component,
	computed,
	effect,
	inject,
	model,
	signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applic } from '@osee/applicability/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { combineLatest, filter, from, of, scan, switchMap } from 'rxjs';

@Component({
	selector: 'osee-view-selector',
	imports: [
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocompleteTrigger,
		MatAutocomplete,
		FormsModule,
		MatOption,
		AsyncPipe,
	],
	template: `<mat-form-field
		appearance="fill"
		subscriptSizing="dynamic"
		class="tw-w-full">
		<mat-label>Select a View</mat-label>
		<input
			type="text"
			matInput
			[(ngModel)]="filterText"
			[matAutocomplete]="auto"
			name="autocomplete-text" />
		<mat-autocomplete
			autoActiveFirstOption
			#auto="matAutocomplete">
			@if (views | async; as _views) {
				<mat-option
					[value]="noneOption.name"
					(click)="selectView(noneOption)">
					{{ noneOption.name }}
				</mat-option>
				@for (option of _views; track option.id) {
					<mat-option
						[value]="option.name"
						(click)="selectView(option)">
						{{ option.name }}
					</mat-option>
				}
			}
		</mat-autocomplete>
	</mat-form-field>`,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class ViewSelectorComponent {
	private applicService = inject(ApplicabilityListUIService);

	public view = model.required<applic>();
	protected viewId = computed(() => this.view().id);
	private viewId$ = toObservable(this.viewId);
	protected filterText = signal('');

	private filterText$ = toObservable(this.filterText);
	protected noneOption: applic = { id: '-1', name: 'None' };

	private _viewEffect = effect(() => this.filterText.set(this.view().name));

	views = combineLatest([this.applicService.views, this.filterText$]).pipe(
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

	selectedView = combineLatest([this.views, this.viewId$]).pipe(
		switchMap(([views, viewId]) => {
			const view = views.find((v) => v.id === viewId);
			return view ? of(view) : of(this.noneOption);
		})
	);

	selectView(view: applic) {
		this.view.set(view);
	}
}
