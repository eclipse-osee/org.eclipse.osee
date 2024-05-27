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
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	BehaviorSubject,
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	of,
	switchMap,
	tap,
} from 'rxjs';
import { CiBatchService } from '../../../services/ci-batch.service';
import { ScriptBatch } from '../../../types';

@Component({
	selector: 'osee-batch-dropdown',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatOptionLoadingComponent,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatIcon,
		MatSuffix,
		MatOption,
	],
	templateUrl: './batch-dropdown.component.html',
})
export class BatchDropdownComponent {
	private batchService = inject(CiBatchService);

	filter = new BehaviorSubject<string>('');
	isOpen = new BehaviorSubject<boolean>(false);
	private _openAutoComplete = new ReplaySubject<void>();

	pageSize = 300;

	selectedBatch = this.batchService.selectedBatch.pipe(
		tap((batch) => this.updateFilter(batch.name))
	);

	batchOptions = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter.pipe(
				debounceTime(250),
				distinctUntilChanged(),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.batchService.getBatches(
							pageNum,
							this.pageSize,
							filter
						)
					)
				)
			)
		)
	);

	count = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter.pipe(
				debounceTime(250),
				distinctUntilChanged(),
				switchMap((filter) => this.batchService.getBatchesCount(filter))
			)
		)
	);

	autoCompleteOpened() {
		this._openAutoComplete.next();
		this.isOpen.next(true);
	}

	close() {
		this.isOpen.next(false);
	}

	updateValue(value: ScriptBatch) {
		this.batchService.routeToBatch(value.id);
		this.updateFilter(value.name);
	}

	updateFilter(value: string) {
		this.filter.next(value);
	}

	downloadBatch() {
		this.batchService.downloadBatch();
	}
}
