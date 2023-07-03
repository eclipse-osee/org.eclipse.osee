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
import {
	ChangeDetectionStrategy,
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applic } from '@osee/shared/types/applicability';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	distinctUntilKeyChanged,
	of,
	ReplaySubject,
	Subject,
	switchMap,
} from 'rxjs';
import { MatOptionLoadingComponent } from '../mat-option-loading/mat-option-loading/mat-option-loading.component';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';

/**
 * Component used for selecting an applicability.
 *
 * @example HTML:	
		Typescript:
		@Component({
		selector: 'example-component'
		template: '<osee-applicability-selector
			[applicability]="value"
			(applicabilityChange)="updateValue($event)">
		</osee-applicability-selector>'
		styles: ''
		standalone: true,
		imports: ApplicabilitySelectorComponent
		})
		export class ExampleComponent {}
 */
@Component({
	selector: 'osee-applicability-selector',
	standalone: true,
	templateUrl: './applicability-selector.component.html',
	styleUrls: ['./applicability-selector.component.sass'],
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		NgIf,
		FormsModule,
		MatInputModule,
		MatOptionModule,
		MatFormFieldModule,
		MatAutocompleteModule,
		MatIconModule,
		MatOptionLoadingComponent,
	],
})
export class ApplicabilitySelectorComponent implements OnChanges {
	constructor(private applicService: ApplicabilityListUIService) {}
	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.applicability.previousValue !==
			changes.applicability.currentValue
		) {
			this._typeAhead.next(changes.applicability.currentValue.name);
		}
		if (
			changes.count !== undefined &&
			changes.count.previousValue !== changes.count.currentValue
		) {
			this._count.next(changes.count.currentValue);
		}
	}

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	@Input() applicability: applic | undefined = { id: '-1', name: '' };

	@Output() applicabilityChange = new Subject<applic>();

	@Input() count: number = 3;

	@Input() required: boolean = false;

	private _count = new BehaviorSubject<number>(3);

	get filter() {
		return this._typeAhead;
	}

	applicabilities = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([this._typeAhead, this._count]).pipe(
				distinctUntilKeyChanged(0),
				debounceTime(500),
				switchMap(([filter, count]) =>
					of((pageNum: string | number) =>
						this.applicService.getApplicabilities(
							pageNum,
							count,
							filter
						)
					)
				)
			)
		)
	);

	applicabilityCount = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				switchMap((filter) =>
					this.applicService.getApplicabilityCount(filter)
				)
			)
		)
	);

	updateTypeAhead(value: string | applic) {
		if (typeof value === 'string') {
			this._typeAhead.next(value);
		} else {
			this._typeAhead.next(value.name);
		}
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
	}
	updateValue(value: applic) {
		this.applicabilityChange.next(value);
	}
}
