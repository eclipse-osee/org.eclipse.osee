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
import { WarningDialogService } from '../../../../shared/services/ui/warning-dialog.service';
import { CurrentMessagesService } from '../../../services/current-messages.service';
import { subMessage } from '../../../types/sub-messages';

@Component({
	selector: 'osee-messaging-edit-sub-message-field',
	templateUrl: './edit-sub-message-field.component.html',
	styleUrls: ['./edit-sub-message-field.component.sass'],
})
export class EditSubMessageFieldComponent<
	R extends keyof subMessage = any,
	T extends Pick<subMessage, keyof subMessage> = any
> {
	@Input() messageId!: string;
	@Input() subMessageId!: string;
	@Input() header: R = {} as R;
	@Input() value: T = {} as T;
	private _value: Subject<T> = new Subject();
	_subMessage: Partial<subMessage> = {
		id: this.subMessageId,
	};
	private _sendValue = this._value.pipe(
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		map(
			(x: any) => (this._subMessage[this.header as keyof subMessage] = x)
		),
		tap(() => {
			this._subMessage.id = this.subMessageId;
		})
	);

	applics = this.messageService.applic.pipe(
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
					switchMap((value) =>
						this.warningService.openSubMessageDialog(
							this._subMessage
						)
					),
					switchMap((value) =>
						this.messageService.partialUpdateSubMessage(
							value,
							this.messageId
						)
					)
				),
				of(false)
			)
		)
	);
	constructor(
		private messageService: CurrentMessagesService,
		private warningService: WarningDialogService
	) {
		this._updateValue.subscribe();
	}
	updateSubMessage(value: T) {
		this._value.next(value);
	}
	compareApplics(o1: any, o2: any) {
		return o1.id === o2.id && o1.name === o2.name;
	}
	focusChanged(event: string | null) {
		this._focus.next(event);
	}
}
