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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	input,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';

/**
 * The save lifecycle of a single auto-saving field, as far as the UI cares to
 * show it. `null` is the quiet default — a clean, already-saved field shows
 * NOTHING (no glyph, no reserved box), so the editor looks untouched in the
 * common case. Only the transient/action-needed states render.
 */
export type fieldSaveStatus = 'unsaved' | 'saving' | 'error' | null;

/**
 * Tiny, stateless indicator for an auto-saving field's {@link fieldSaveStatus}.
 * It holds no logic and no timers: the owning editor derives the status from its
 * save lifecycle and passes it in; this component only maps status -> view.
 *
 * Rendering per status:
 * - `null`  -> nothing (clean/saved is silent).
 * - `unsaved` -> amber edit glyph, tooltip instructing how to save (blur-to-save).
 * - `saving`  -> small spinner.
 * - `error`   -> red error glyph, tooltip to retry.
 *
 * Accessibility: a polite `aria-live` region announces the state transitions to
 * screen readers (the glyph alone is sighted-only). The region is always present
 * (so changes are announced) but empty when clean, matching the visual silence.
 */
@Component({
	selector: 'osee-field-save-status',
	imports: [MatIcon, MatProgressSpinner, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<span
			class="tw-inline-flex tw-items-center"
			role="status"
			aria-live="polite">
			@switch (status()) {
				@case ('unsaved') {
					<mat-icon
						class="!tw-h-4 !tw-w-4 !tw-text-[16px] !tw-leading-none tw-text-caution"
						[matTooltip]="unsavedTooltip()"
						aria-label="Unsaved changes. Click outside the field to save.">
						edit
					</mat-icon>
				}
				@case ('saving') {
					<mat-progress-spinner
						mode="indeterminate"
						[diameter]="14"
						[strokeWidth]="2"
						matTooltip="Saving…"
						aria-label="Saving changes." />
				}
				@case ('error') {
					<mat-icon
						class="!tw-h-4 !tw-w-4 !tw-text-[16px] !tw-leading-none tw-text-warning"
						[matTooltip]="errorTooltip()"
						aria-label="Save failed. Edit the field and click outside it to retry.">
						error_outline
					</mat-icon>
				}
			}
		</span>
	`,
})
export class FieldSaveStatusComponent {
	/** Current save status; `null` renders nothing (clean/saved is silent). */
	status = input<fieldSaveStatus>(null);

	/**
	 * Optional field name, woven into the tooltips so a user hovering an indicator
	 * on a dense form knows which field it refers to.
	 */
	fieldName = input<string>('');

	protected readonly unsavedTooltip = computed(() => {
		const name = this.fieldName();
		return name
			? `${name} is unsaved. Click outside the field to save.`
			: 'Unsaved. Click outside the field to save.';
	});

	protected readonly errorTooltip = computed(() => {
		const name = this.fieldName();
		return name
			? `${name} failed to save. Edit the field and click outside it to retry.`
			: 'Save failed. Edit the field and click outside it to retry.';
	});
}
