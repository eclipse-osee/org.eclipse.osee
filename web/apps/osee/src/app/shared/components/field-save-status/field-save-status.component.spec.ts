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
import { FieldSaveStatusComponent } from './field-save-status.component';

/**
 * The indicator is pure presentation: status enum in -> glyph/spinner/nothing out.
 * These assert the "silent when clean" contract (null renders nothing), that each
 * visible state renders its own element, and that the field name is woven into the
 * accessible tooltips.
 */
describe('FieldSaveStatusComponent', () => {
	let fixture: ComponentFixture<FieldSaveStatusComponent>;
	let el: HTMLElement;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [FieldSaveStatusComponent, BrowserAnimationsModule],
		});
		fixture = TestBed.createComponent(FieldSaveStatusComponent);
		el = fixture.nativeElement as HTMLElement;
	});

	function setStatus(
		status: 'unsaved' | 'saving' | 'error' | null,
		fieldName = ''
	) {
		fixture.componentRef.setInput('status', status);
		fixture.componentRef.setInput('fieldName', fieldName);
		fixture.detectChanges();
	}

	it('renders nothing when clean/saved (status null)', () => {
		setStatus(null);
		expect(el.querySelector('mat-icon')).toBeNull();
		expect(el.querySelector('mat-progress-spinner')).toBeNull();
	});

	it('renders the edit glyph for "unsaved"', () => {
		setStatus('unsaved');
		const icon = el.querySelector('mat-icon');
		expect(icon).toBeTruthy();
		expect(icon?.textContent?.trim()).toBe('edit');
		expect(el.querySelector('mat-progress-spinner')).toBeNull();
	});

	it('renders a spinner for "saving"', () => {
		setStatus('saving');
		expect(el.querySelector('mat-progress-spinner')).toBeTruthy();
		expect(el.querySelector('mat-icon')).toBeNull();
	});

	it('renders the error glyph for "error"', () => {
		setStatus('error');
		const icon = el.querySelector('mat-icon');
		expect(icon).toBeTruthy();
		expect(icon?.textContent?.trim()).toBe('error_outline');
	});

	it('keeps a polite live region for screen readers', () => {
		setStatus('unsaved');
		const live = el.querySelector('[aria-live="polite"]');
		expect(live).toBeTruthy();
		expect(live?.getAttribute('role')).toBe('status');
	});

	it('computes name-specific tooltips, and generic ones without a name', () => {
		const c = fixture.componentInstance as unknown as {
			unsavedTooltip: () => string;
			errorTooltip: () => string;
		};

		setStatus('unsaved', 'Description');
		expect(c.unsavedTooltip()).toBe(
			'Description is unsaved. Click outside the field to save.'
		);

		setStatus('error', '');
		expect(c.errorTooltip()).toBe(
			'Save failed. Edit the field and click outside it to retry.'
		);
	});
});
