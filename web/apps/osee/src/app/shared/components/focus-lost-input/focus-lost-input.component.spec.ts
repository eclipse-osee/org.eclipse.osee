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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FocusLostInputComponent } from './focus-lost-input.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('FocusLostInputComponent', () => {
	let component: FocusLostInputComponent<unknown>;
	let fixture: ComponentFixture<FocusLostInputComponent<unknown>>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [FocusLostInputComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(FocusLostInputComponent);
		fixture.componentRef.setInput('value', '');
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('suppressCommit', () => {
		const formField = () =>
			fixture.nativeElement.querySelector('mat-form-field');

		/** Advance past both 500ms debounces (focus-loss sample + value debounce). */
		const flushDebounces = () => new Promise((r) => setTimeout(r, 1100));

		it('emits valueChange on blur when commits are not suppressed', async () => {
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('typed value');
			formField().dispatchEvent(new Event('focusout'));
			await flushDebounces();

			expect(emitted).toContain('typed value');
		});

		it('does NOT emit valueChange when the blur happened while suppressed', async () => {
			fixture.componentRef.setInput('suppressCommit', true);
			fixture.detectChanges();
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('typed while conflicted');
			formField().dispatchEvent(new Event('focusout'));
			await flushDebounces();

			expect(emitted).toEqual([]);
		});

		it('honors the suppression state at blur time even if it clears before the debounced emit', async () => {
			// Suppressed while the field is focused/edited...
			fixture.componentRef.setInput('suppressCommit', true);
			fixture.detectChanges();
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('stale edit');
			formField().dispatchEvent(new Event('focusout'));

			// ...then suppression clears (e.g. a conflict is resolved) BEFORE the ~500ms
			// debounced commit would fire. The commit must still be dropped because the blur
			// occurred while suppressed -- otherwise it would clobber the resolved value.
			fixture.componentRef.setInput('suppressCommit', false);
			fixture.detectChanges();
			await flushDebounces();

			expect(emitted).toEqual([]);
		});
	});
});
