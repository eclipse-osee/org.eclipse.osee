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
import { Component, Inject, Input } from '@angular/core';
import { combineLatest, iif, of, Subject } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	map,
	scan,
	share,
	switchMap,
	tap,
} from 'rxjs/operators';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { A11yModule } from '@angular/cdk/a11y';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import {
	CurrentStructureService,
	EnumsService,
	STRUCTURE_SERVICE_TOKEN,
	WarningDialogService,
} from '@osee/messaging/shared';
import type { structure } from '@osee/messaging/shared';

@Component({
	selector: 'osee-messaging-edit-structure-field',
	templateUrl: './edit-structure-field.component.html',
	styleUrls: ['./edit-structure-field.component.sass'],
	standalone: true,
	imports: [
		MatFormFieldModule,
		FormsModule,
		A11yModule,
		MatSelectModule,
		MatOptionModule,
		MatInputModule,
		NgIf,
		NgFor,
		AsyncPipe,
	],
})
export class EditStructureFieldComponent<
	R extends keyof structure = any,
	T extends Pick<structure, keyof structure> = any
> {
	@Input() structureId!: string;
	@Input() header: R = {} as R;
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
		switchMap(() =>
			this.warningService.openStructureDialog(this._structure)
		),
		switchMap((val) => this.structureService.partialUpdateStructure(val))
	);
	categories = this.enumService.categories;
	applics = this.structureService.applic;
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
					switchMap(() =>
						this.warningService.openStructureDialog(this._structure)
					),
					switchMap((val) =>
						this.structureService.partialUpdateStructure(val)
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
		this._value.next(value);
	}
	updateImmediately(value: T) {
		this._immediateValue.next(value);
	}

	compareApplics(o1: any, o2: any) {
		return o1.id === o2.id && o1.name === o2.name;
	}
	focusChanged(event: string | null) {
		this._focus.next(event);
	}
}
