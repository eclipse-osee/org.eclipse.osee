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
import {
	Component,
	computed,
	inject,
	input,
	model,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ControlContainer } from '@angular/forms';
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { MessageTypeDropdownComponent } from '@osee/messaging/message-type/message-type-dropdown';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	pairwise,
	map,
	combineLatest,
	switchMap,
	take,
	of,
	takeUntil,
	filter,
} from 'rxjs';

@Component({
	selector: 'osee-persisted-message-type-dropdown',
	standalone: true,
	imports: [MessageTypeDropdownComponent],
	template: `<osee-message-type-dropdown
		[required]="required()"
		[disabled]="disabled()"
		[(value)]="value"
		[hintHidden]="hintHidden()" />`,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class PersistedMessageTypeDropdownComponent {
	private control = signal(inject(ControlContainer));
	private statusSignal = computed(() => {
		const _control = this.control();
		const _statusChanges = _control.statusChanges;
		if (_control && _statusChanges) {
			return _statusChanges;
		}
		return of('INVALID');
	});
	artifactId = input.required<`${number}`>();
	artifactApplicability = input.required<applic>();

	comment = input('Modifying message periodicity');
	value =
		model.required<
			attribute<
				string,
				| typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
				| typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONTYPE
			>
		>();
	protected attrValue = writableSlice(this.value, 'value');
	disabled = input(false);
	required = input(false);

	hintHidden = input(true);
	private _notValid = this.statusSignal().pipe(
		filter((v) => v === 'INVALID' || v === 'DISABLED')
	);
	private _value$ = toObservable(this.value).pipe(takeUntil(this._notValid));

	private _valueUpdated = this._value$.pipe(
		takeUntil(this._notValid),
		pairwise(),
		map(
			([prev, curr]) =>
				prev.value !== curr.value &&
				prev.id === curr.id &&
				prev.gammaId === curr.gammaId
		)
	);

	private _artifactId$ = toObservable(this.artifactId);
	private _artifactApplicability$ = toObservable(this.artifactApplicability);

	private _comment$ = toObservable(this.comment);

	private _currentTxService = inject(CurrentTransactionService);

	private _tx = toSignal(
		combineLatest([this._valueUpdated, this.statusSignal()]).pipe(
			filter(([updated, validity]) => updated && validity === 'VALID'),
			switchMap((_) =>
				combineLatest([
					this._value$,
					this._artifactId$,
					this._artifactApplicability$,
					this._comment$,
				]).pipe(
					take(1),
					switchMap(([value, artId, applic, comment]) => {
						const attrs =
							value.id === '-1' && value.gammaId === '-1'
								? { add: [value] }
								: { set: [value] };
						return this._currentTxService
							.modifyArtifactAndMutate(
								comment,
								artId,
								applic,
								attrs
							)
							.pipe(take(1));
					})
				)
			)
		)
	);
}
