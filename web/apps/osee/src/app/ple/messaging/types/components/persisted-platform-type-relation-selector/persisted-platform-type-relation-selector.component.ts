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
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
	model,
	output,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { WarningDialogService } from '@osee/messaging/shared/services';
import { PlatformType } from '@osee/messaging/shared/types';
import { PlatformTypeDropdownComponent } from '@osee/messaging/types/dropdown';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { addRelation } from '@osee/transactions/operators';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	pairwise,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-persisted-platform-type-relation-selector',
	standalone: true,
	imports: [FormsModule, PlatformTypeDropdownComponent],
	template: `<form>
		<osee-platform-type-dropdown
			[allowOpenInSameTab]="true"
			[required]="true"
			[hideSearchButton]="true"
			[(platformType)]="platformType"
			(contextmenu)="contextmenu.emit($event)" />
	</form>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersistedPlatformTypeRelationSelectorComponent {
	artifactId = input.required<`${number}`>();
	platformType = model.required<PlatformType>();
	contextmenu = output<MouseEvent>();

	private _platformTypeId = computed(() => this.platformType().id);
	//current and previous platform type in order to unrelate/relate
	private _platformTypes = toObservable(this._platformTypeId).pipe(
		debounceTime(500),
		distinctUntilChanged(),
		pairwise(),
		filter(
			([prev, curr]) =>
				// TODO: probably want more checks here just to make it safer with db changes
				prev !== curr
		)
	);

	private _warningService = inject(WarningDialogService);
	private _artifactId$ = toObservable(this.artifactId);
	private _ids = combineLatest([this._artifactId$, this._platformTypes]).pipe(
		map(([artId, platformTypes]) => {
			return [artId, platformTypes[0], platformTypes[1]] as const;
		})
	);
	private _warning = this._ids.pipe(
		switchMap((ids) =>
			this._warningService
				.openElementDialogById(ids[0])
				.pipe(map((_) => ids))
		)
	);
	private _currentTxService = inject(CurrentTransactionService);

	private _tx = toSignal(
		this._warning.pipe(
			switchMap(([artId, prev, curr]) =>
				this._currentTxService
					.deleteRelation(
						`Changing platform type relation of ${artId}`,
						{
							typeId: RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
							aArtId: artId,
							bArtId: prev,
						}
					)
					.pipe(
						addRelation({
							typeId: RELATIONTYPEIDENUM.INTERFACEELEMENTPLATFORMTYPE,
							aArtId: artId,
							bArtId: curr,
						})
					)
			),
			this._currentTxService.performMutation()
		)
	);
}
