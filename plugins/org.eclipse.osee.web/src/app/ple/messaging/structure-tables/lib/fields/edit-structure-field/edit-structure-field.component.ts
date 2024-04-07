/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { CdkMonitorFocus } from '@angular/cdk/a11y';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Inject,
	Input,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import {
	CurrentStructureService,
	EnumsService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import type { structure } from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { applic } from '@osee/shared/types/applicability';
import { Subject, combineLatest, iif, of } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	map,
	scan,
	share,
	switchMap,
	tap,
} from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-edit-structure-field',
	templateUrl: './edit-structure-field.component.html',
	styles: [':host{ display: block; width: 100%;}'],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		FormsModule,
		MatFormField,
		CdkMonitorFocus,
		MatSelect,
		MatOption,
		MatInput,
		MatTooltip,
		NgIf,
		NgFor,
		AsyncPipe,
		ApplicabilitySelectorComponent,
	],
})
export class EditStructureFieldComponent<
	R extends keyof structure = any,
	T extends Pick<structure, keyof structure> = any,
> {
	@Input() structureId!: string;
	@Input() header!: R;
	@Input() value: T = {} as T;
	private _value: Subject<T> = new Subject();
	private _immediateValue: Subject<T> = new Subject();
	_structure: Partial<structure> = {
		id: this.structureId,
	};
	private _sendValue = this._value.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._structure[this.header] = x)),
		tap(() => {
			this._structure.id = this.structureId;
		})
	);
	private _immediateUpdateValue = this._immediateValue.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._structure[this.header] = x)),
		tap(() => {
			this._structure.id = this.structureId;
		}),
		switchMap((val) => this.structureService.partialUpdateStructure(val))
	);
	categories = this.enumService.categories;

	private _focus = new Subject<string | null>();
	private _updateValue = combineLatest([this._sendValue, this._focus]).pipe(
		scan(
			(acc, curr) => {
				if (acc.type === curr[1]) {
					acc.count++;
				} else {
					acc.count = 0;
					acc.type = curr[1];
				}
				acc.value = curr[0];
				return acc;
			},
			{ count: 0, type: '', value: undefined } as {
				count: number;
				type: string | null;
				value: T | undefined;
			}
		),
		switchMap((update) =>
			iif(
				() => update.type === null,
				of(true).pipe(
					switchMap((val) =>
						this.structureService.partialUpdateStructure(
							this._structure
						)
					)
				),
				of(false)
			)
		)
	);
	constructor(
		@Inject(STRUCTURE_SERVICE_TOKEN)
		private structureService: CurrentStructureService,
		private enumService: EnumsService,
		private warningService: WarningDialogService
	) {
		this._updateValue.subscribe();
		this._immediateUpdateValue.subscribe();
	}
	updateStructure(value: T) {
		if (this.header === 'applicability') {
			this.focusChanged('applicability');
		}
		this._value.next(value);
		if (this.header === 'applicability') {
			this.focusChanged(null);
		}
	}
	updateImmediately(value: T) {
		this._immediateValue.next(value);
	}

	focusChanged(event: string | null) {
		this._focus.next(event);
	}

	/**
	 * Note, this is a hack until we improve the types, don't use unless you know what you are doing
	 */
	isApplic(value: unknown): value is applic {
		return (
			value !== null &&
			value !== undefined &&
			typeof value === 'object' &&
			'id' in value &&
			'name' in value &&
			typeof value.id === 'string' &&
			typeof value.name === 'string'
		);
	}

	/**
	 * Note, this is a hack until we improve the types, don't use unless you know what you are doing
	 */
	returnAsT(value: unknown): T {
		return value as T;
	}
}
