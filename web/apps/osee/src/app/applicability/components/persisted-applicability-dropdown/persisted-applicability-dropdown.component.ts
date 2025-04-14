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
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ControlContainer } from '@angular/forms';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { applic } from '@osee/applicability/types';
import { UiService } from '@osee/shared/services';
import { branchSentinel } from '@osee/shared/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	combineLatest,
	filter,
	map,
	of,
	pairwise,
	switchMap,
	takeUntil,
} from 'rxjs';

@Component({
	selector: 'osee-persisted-applicability-dropdown',
	imports: [ApplicabilityDropdownComponent],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<osee-applicability-dropdown
		[required]="required()"
		[disabled]="disabled()"
		[(applicability)]="applicability" />`,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class PersistedApplicabilityDropdownComponent {
	private control = signal(inject(ControlContainer));
	private uiService = signal(inject(UiService));

	branchIdOverride = input<string>(branchSentinel.id);
	protected branchId = toSignal(this.uiService().id);
	protected selectedBranch = computed(() => {
		const override = this.branchIdOverride();
		if (Number(override) > 0) {
			return override;
		}
		return this.branchId();
	});

	private statusSignal = computed(() => {
		const _control = this.control();
		const _statusChanges = _control.statusChanges;
		if (_control && _statusChanges) {
			return _statusChanges;
		}
		return of('INVALID');
	});
	artifactId = input.required<`${number}`>();
	applicability = model.required<applic>();
	comment = input('Modifying applicability');
	disabled = input(false);

	required = input(false);
	private _notValid = this.statusSignal().pipe(
		filter((v) => v === 'INVALID' || v === 'DISABLED')
	);
	private _applicability$ = toObservable(this.applicability).pipe(
		takeUntil(this._notValid)
	);

	private _applicability = this._applicability$.pipe(
		takeUntil(this._notValid),
		filter((v) => v.id !== '-1' && v.id !== '0'),
		pairwise(),
		filter(([prev, curr]) => prev.id !== curr.id),
		map(([_prev, curr]) => curr)
	);
	private _artifactId$ = toObservable(this.artifactId);
	private _comment$ = toObservable(this.comment);

	private _currentTxService = inject(CurrentTransactionService);

	private _tx = toSignal(
		combineLatest([
			this._artifactId$,
			this._applicability,
			this._comment$,
			this.statusSignal(),
		]).pipe(
			switchMap(([artId, applic, comment, validity]) => {
				if (validity === 'VALID') {
					return this._currentTxService.modifyArtifactAndMutate(
						comment,
						artId,
						applic,
						{},
						this.selectedBranch()
					);
				}
				return of();
			})
		)
	);
}
