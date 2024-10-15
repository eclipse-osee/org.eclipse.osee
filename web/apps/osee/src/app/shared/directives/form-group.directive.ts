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
	AfterViewChecked,
	Directive,
	EnvironmentInjector,
	OnDestroy,
	inject,
	output,
	runInInjectionContext,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgModelGroup } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';

@Directive({
	selector: '[oseeFormGroup]',
	standalone: true,
})
export class FormGroupDirective implements AfterViewChecked, OnDestroy {
	private readonly ngForm = inject(NgModelGroup);

	//don't like having to do this :(
	private environmentInjector = inject(EnvironmentInjector);
	formGroupStatusChange = output<
		'VALID' | 'INVALID' | 'PENDING' | 'DISABLED'
	>();

	private _statusChanges!: Observable<
		'PENDING' | 'INVALID' | 'VALID' | 'DISABLED'
	>;
	private _statusChangesSubscription: Subscription | undefined;

	/** Inserted by Angular inject() migration for backwards compatibility */
	ngOnDestroy(): void {
		//developer's note: this is needed to keep the subscription from leaking to other pages, this directive doesn't clean up super nice
		if (this._statusChangesSubscription) {
			this._statusChangesSubscription.unsubscribe();
		}
	}
	ngAfterViewChecked(): void {
		const statusChanges = this.ngForm.statusChanges;
		//we do not want to constantly be resubscribing, as that's a memory leak waiting to happen
		if (statusChanges && !this._statusChangesSubscription) {
			this._statusChanges = statusChanges;
			//this should hopefully always be true
			runInInjectionContext(this.environmentInjector, () => {
				this._statusChangesSubscription = this._statusChanges
					.pipe(takeUntilDestroyed())
					.subscribe((v) => {
						//unpack it to the outside world
						this.formGroupStatusChange.emit(v);
					});
			});
		}
	}
}
