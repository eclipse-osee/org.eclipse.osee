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

	describe('commit on blur', () => {
		const formField = () =>
			fixture.nativeElement.querySelector('mat-form-field');

		/**
		 * Advance past the single 500ms focus-settle debounce. The value stream is
		 * no longer debounced (it is blur-gated via `updateOn: 'blur'` and sampled at
		 * the settled blur), so only the focus debounce needs flushing.
		 */
		const flushDebounces = () => new Promise((r) => setTimeout(r, 700));

		it('emits valueChange with the latest value at the settled blur', async () => {
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('typed value');
			formField().dispatchEvent(new Event('focusout'));
			await flushDebounces();

			expect(emitted).toContain('typed value');
		});

		it('commits the LATEST value when it changes more than once before the settled blur', async () => {
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			// The model normally updates on blur; simulate the final blurred value
			// winning when the bound value settles to its last state.
			component.value.set('first');
			component.value.set('final');
			formField().dispatchEvent(new Event('focusout'));
			await flushDebounces();

			expect(emitted.at(-1)).toBe('final');
		});

		it('does NOT commit on a momentary focus flicker (blur then refocus within the debounce window)', async () => {
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('typed value');
			// Blur, then refocus well within the 500ms focus-settle window: this is a
			// flicker (e.g. clicking a toolbar button and returning), not a real commit.
			formField().dispatchEvent(new Event('focusout'));
			await new Promise((r) => setTimeout(r, 150));
			formField().dispatchEvent(new Event('focusin'));
			// Wait past the window; because focus ended on `true` (focused), no settled
			// blur emits, so nothing commits.
			await flushDebounces();

			expect(emitted).toEqual([]);
		});

		// The component is intentionally "dumb": it reports every settled-blur commit
		// and does not filter stale ones. Deciding whether a commit is still valid
		// (e.g. after a conflict resolve/discard) is the consumer's job via its
		// edit-generation guard -- see PersistedArtifactAttributeEditorComponent. This
		// keeps a single owner for commit validity rather than splitting it here.
		it('emits valueChange regardless of conflict state (no suppression here)', async () => {
			const emitted: unknown[] = [];
			component.valueChange.subscribe((v) => emitted.push(v));

			formField().dispatchEvent(new Event('focusin'));
			component.value.set('typed value');
			formField().dispatchEvent(new Event('focusout'));
			await flushDebounces();

			expect(emitted).toContain('typed value');
		});
	});
});
