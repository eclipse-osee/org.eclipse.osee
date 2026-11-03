/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { PersistedArtifactAttributeEditorComponent } from './persisted-artifact-attribute-editor.component';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { applicabilitySentinel } from '@osee/applicability/types';
import { CurrentTransactionService } from '@osee/transactions/services';
import {
	EditorDirtyService,
	PendingAttributeValuesService,
} from '@osee/shared/conflict-resolution';

/**
 * Unit coverage for the commit model of PersistedArtifactAttributeEditorComponent:
 * the single `commit` path, the immediate-vs-deferred wrappers, and the
 * edit-generation + value-identity staleness guard. These are the highest-risk
 * pieces reworked this effort and are exercised here deterministically (an e2e
 * test is a blunt instrument for a sub-500ms stale-commit race).
 *
 * The save path is observed by spying on CurrentTransactionService.modifyArtifactAndMutate
 * (returning an empty observable) so no real transaction is built or sent; we assert
 * only whether a save was attempted and with what value.
 */
describe('PersistedArtifactAttributeEditorComponent (commit model)', () => {
	let fixture: ComponentFixture<PersistedArtifactAttributeEditorComponent>;
	let component: PersistedArtifactAttributeEditorComponent;
	let tx: CurrentTransactionService;
	let dirty: EditorDirtyService;
	let pending: PendingAttributeValuesService;
	let saveSpy: ReturnType<typeof vi.spyOn>;

	const ARTIFACT_ID = '200' as const;
	const ATTR_ID = '55' as const;
	const EDITOR_KEY = `${ARTIFACT_ID}-${ATTR_ID}`;

	/** A persisted (has real id/gamma) attribute with the given value. */
	function attr(
		value: string,
		name = 'Some Attr'
	): attribute<string, ATTRIBUTETYPEID> {
		return {
			id: ATTR_ID,
			typeId: '1152921504606847090' as ATTRIBUTETYPEID,
			gammaId: '999',
			value,
			name,
		} as attribute<string, ATTRIBUTETYPEID>;
	}

	function setup(initialValue = 'persisted') {
		TestBed.configureTestingModule({
			imports: [
				PersistedArtifactAttributeEditorComponent,
				BrowserAnimationsModule,
			],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
				// Panel-scoped in production; provide directly for the unit under test.
				PendingAttributeValuesService,
			],
		});

		tx = TestBed.inject(CurrentTransactionService);
		dirty = TestBed.inject(EditorDirtyService);
		pending = TestBed.inject(PendingAttributeValuesService);
		dirty.clearAll();
		pending.clear();

		// Spy the save so no real transaction fires; return an empty observable so
		// the component's take(1) subscription completes without error.
		saveSpy = vi
			.spyOn(tx, 'modifyArtifactAndMutate')
			.mockReturnValue(of() as never);

		fixture = TestBed.createComponent(
			PersistedArtifactAttributeEditorComponent
		);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('attr', attr(initialValue));
		fixture.componentRef.setInput('artifactId', ARTIFACT_ID);
		fixture.componentRef.setInput(
			'artifactApplicability',
			applicabilitySentinel
		);
		fixture.detectChanges();
	}

	/** The value passed to the (spied) save call number `callIndex`, if any. */
	function savedValueAt(callIndex: number): string | undefined {
		const call = saveSpy.mock.calls[callIndex];
		if (!call) return undefined;
		// modifyArtifactAndMutate(comment, artId, applic, attrConfig)
		const attrConfig = call[3] as {
			set?: { value: string }[];
			add?: { value: string }[];
		};
		return (attrConfig.set ?? attrConfig.add)?.[0]?.value;
	}

	afterEach(() => {
		saveSpy?.mockRestore();
	});

	describe('immediate commits (atomic widgets)', () => {
		it('enum selection saves (regression: enum used to be dropped)', () => {
			setup('Unspecified');
			component.onEnumChange('Completed');
			expect(saveSpy).toHaveBeenCalledTimes(1);
			expect(savedValueAt(0)).toBe('Completed');
		});

		it('boolean toggle saves', () => {
			setup('false');
			component.onBooleanChange(true);
			expect(saveSpy).toHaveBeenCalledTimes(1);
			expect(savedValueAt(0)).toBe('true');
		});

		it('does not save when the atomic value equals the persisted value', () => {
			setup('Unspecified');
			component.onEnumChange('Unspecified');
			expect(saveSpy).not.toHaveBeenCalled();
		});

		it('an atomic commit is never dropped by staleness even if the field was not dirty-first', () => {
			setup('false');
			// No prior onLiveInput/dirty: the atomic path must still save.
			expect(dirty.isDirty(EDITOR_KEY)).toBe(false);
			component.onBooleanChange(true);
			expect(saveSpy).toHaveBeenCalledTimes(1);
		});
	});

	describe('deferred commits (text blur)', () => {
		it('a live edit then blur commit saves', () => {
			setup('persisted');
			component.onLiveInput('typed');
			expect(dirty.isDirty(EDITOR_KEY)).toBe(true);
			expect(pending.get(ATTR_ID)).toBe('typed');

			component.onTextCommitted('typed');
			expect(saveSpy).toHaveBeenCalledTimes(1);
			expect(savedValueAt(0)).toBe('typed');
		});

		it('drops a deferred commit whose value does not match the pending value', () => {
			setup('persisted');
			component.onLiveInput('newest');
			// A stale echo carrying an OLD value must be ignored (value identity).
			component.onTextCommitted('stale-old');
			expect(saveSpy).not.toHaveBeenCalled();
		});

		it('drops a deferred commit when the field has no live edit (pending cleared)', () => {
			setup('persisted');
			component.onLiveInput('typed');
			// Simulate a resolve/discard clearing local state (bulk clear).
			pending.remove(ATTR_ID);
			dirty.clearAll();
			component.onTextCommitted('typed');
			expect(saveSpy).not.toHaveBeenCalled();
		});
	});

	describe('conflict handling', () => {
		it('does not save while conflicted but keeps the edit tracked for the dialog', () => {
			setup('persisted');
			fixture.componentRef.setInput('conflicted', true);
			fixture.detectChanges();

			component.onLiveInput('typed while conflicted');
			component.onTextCommitted('typed while conflicted');

			// Save is blocked...
			expect(saveSpy).not.toHaveBeenCalled();
			// ...but the edit remains dirty + pending so it surfaces in the dialog.
			expect(dirty.isDirty(EDITOR_KEY)).toBe(true);
			expect(pending.get(ATTR_ID)).toBe('typed while conflicted');
		});

		it('an enum change while conflicted is tracked but not saved', () => {
			setup('Unspecified');
			fixture.componentRef.setInput('conflicted', true);
			fixture.detectChanges();

			component.onEnumChange('Completed');

			expect(saveSpy).not.toHaveBeenCalled();
			expect(dirty.isDirty(EDITOR_KEY)).toBe(true);
			expect(pending.get(ATTR_ID)).toBe('Completed');
		});
	});

	describe('retype-after-discard race (the decisive edge case)', () => {
		it('a stale old echo does not overwrite a newer retyped value, and the new value still saves', () => {
			setup('persisted');

			// 1) User types an edit; a blur commit is "in flight" (not yet delivered).
			component.onLiveInput('old value');

			// 2) A conflict resolve/discard bulk-clears local state and bumps the
			//    edit generation.
			pending.remove(ATTR_ID);
			dirty.clearAll();

			// 3) User starts a NEW edit (recreates the pending slot in the NEW generation).
			component.onLiveInput('new value');

			// 4) The STALE echo of the old blur finally arrives. It must be dropped:
			//    its value ('old value') no longer matches the pending ('new value'),
			//    and its generation is stale.
			component.onTextCommitted('old value');
			expect(saveSpy).not.toHaveBeenCalled();

			// 5) The genuine new commit still saves the new value.
			component.onTextCommitted('new value');
			expect(saveSpy).toHaveBeenCalledTimes(1);
			expect(savedValueAt(0)).toBe('new value');
		});
	});

	describe('edit generation capture', () => {
		it('a bulk clear between edit start and commit invalidates the deferred commit', () => {
			setup('persisted');
			component.onLiveInput('typed');
			const genDuringEdit = dirty.generation();

			// Bulk clear bumps the generation; even though the user re-dirties with the
			// SAME value, the earlier echo captured the old generation and is dropped.
			dirty.clearAll();
			expect(dirty.generation()).toBe(genDuringEdit + 1);

			// Re-establish a live edit at the new generation, same value.
			component.onLiveInput('typed');
			// The genuine commit at the current generation + matching pending saves.
			component.onTextCommitted('typed');
			expect(saveSpy).toHaveBeenCalledTimes(1);
		});
	});

	describe('saveStatus (inline indicator derivation)', () => {
		/** `saveStatus` is protected; read it through the instance for assertions. */
		const status = () =>
			(
				component as unknown as {
					saveStatus: () => 'unsaved' | 'saving' | 'error' | null;
				}
			).saveStatus();

		it('is null (silent) for a clean, untouched field', () => {
			setup('persisted');
			expect(status()).toBeNull();
		});

		it('is "unsaved" once a live edit marks the field dirty', () => {
			setup('persisted');
			component.onLiveInput('typed');
			expect(status()).toBe('unsaved');
		});

		it('returns to null after a successful save clears dirty', () => {
			setup('persisted');
			// Emit a result so the save's `next` handler runs (marks clean, saving off).
			// (The default of() completes without emitting, which would leave `saving`.)
			saveSpy.mockReturnValue(of({}) as never);
			component.onLiveInput('typed');
			expect(status()).toBe('unsaved');
			component.onTextCommitted('typed');
			expect(status()).toBeNull();
		});

		it('is "error" after a save fails, and clears when a new edit starts', () => {
			setup('persisted');
			// Make the save fail so the error signal is set.
			saveSpy.mockReturnValue(
				throwError(() => new Error('save failed')) as never
			);
			component.onLiveInput('typed');
			component.onTextCommitted('typed');
			expect(status()).toBe('error');

			// A fresh edit clears the error and shows unsaved again.
			component.onLiveInput('typed again');
			expect(status()).toBe('unsaved');
		});

		it('is null while blocked by a conflict (the amber ring owns that state, no double glyph)', () => {
			setup('persisted');
			fixture.componentRef.setInput('conflicted', true);
			fixture.detectChanges();

			component.onLiveInput('typed while conflicted');
			// Dirty + conflicted => blockedUnsaved; saveStatus defers to the ring.
			expect(status()).toBeNull();
		});
	});
});
