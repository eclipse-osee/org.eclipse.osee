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
import { A11yModule } from '@angular/cdk/a11y';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {
	CurrentMessagesService,
	EnumsService,
} from '@osee/messaging/shared/services';
import type { message } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { combineLatest, iif, of, ReplaySubject, Subject } from 'rxjs';
import {
	debounceTime,
	distinctUntilChanged,
	map,
	scan,
	share,
	switchMap,
	takeUntil,
	tap,
} from 'rxjs/operators';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { applic } from '@osee/shared/types/applicability';

@Component({
	selector: 'osee-messaging-edit-message-field',
	templateUrl: './edit-message-field.component.html',
	styleUrls: ['./edit-message-field.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		A11yModule,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MatInputModule,
		MatSlideToggleModule,
		MatAutocompleteModule,
		MatOptionLoadingComponent,
		ApplicabilitySelectorComponent,
	],
})
export class EditMessageFieldComponent<
	R extends keyof message = any,
	T extends Pick<message, keyof message> = any
> {
	@Input() messageId!: string;
	@Input() header: R = {} as R;
	@Input() value: T = {} as T;
	private _value: Subject<T> = new Subject();
	private _immediateValue: Subject<T> = new Subject();
	_message: Partial<message> = {
		id: this.messageId,
	};
	private _sendValue = this._value.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._message[this.header] = x)),
		tap(() => {
			this._message.id = this.messageId;
		})
	);
	private _immediateSendValue = this._immediateValue.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map((x) => (this._message[this.header] = x)),
		tap(() => {
			this._message.id = this.messageId;
		}),
		switchMap((val) =>
			this.messageService.partialUpdateMessage(this._message)
		)
	);
	rates = this.enumService.rates.pipe(takeUntil(this.messageService.done));
	types = this.enumService.types.pipe(takeUntil(this.messageService.done));
	periodicities = this.enumService.periodicities.pipe(
		takeUntil(this.messageService.done)
	);
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
						this.messageService.partialUpdateMessage(this._message)
					)
				),
				of(false)
			)
		)
	);
	constructor(
		private messageService: CurrentMessagesService,
		private enumService: EnumsService
	) {
		this._updateValue.subscribe();
		this._immediateSendValue.subscribe();
	}

	updateMessage(value: T) {
		this._value.next(value);
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
