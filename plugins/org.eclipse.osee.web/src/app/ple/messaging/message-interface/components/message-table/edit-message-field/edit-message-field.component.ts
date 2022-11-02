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
import { Component, Input, OnInit } from '@angular/core';
import { combineLatest, iif, of, Subject } from 'rxjs';
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
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { CurrentMessagesService } from '../../../services/current-messages.service';
import { message } from '../../../types/messages';

@Component({
	selector: 'osee-messaging-edit-message-field',
	templateUrl: './edit-message-field.component.html',
	styleUrls: ['./edit-message-field.component.sass'],
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
	applics = this.messageService.applic.pipe(
		takeUntil(this.messageService.done)
	);
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

	compareApplics(o1: any, o2: any) {
		return o1.id === o2.id && o1.name === o2.name;
	}
	focusChanged(event: string | null) {
		this._focus.next(event);
	}
}
