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
	input,
	output,
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

/**
 * Shared presentational delete control for a single attribute instance.
 *
 * Renders one of two variants so the add/delete UX stays identical across the
 * create dialog and the artifact editor:
 * - `deletable` true  -> an enabled warning-colored button that emits `delete`.
 * - `deletable` false -> a disabled button wrapped in a tooltip span that
 *   explains why it can't be deleted (Material won't show tooltips on a
 *   disabled button directly, hence the wrapper).
 *
 * Purely presentational: it holds no deletion logic and performs no
 * persistence. The parent decides `deletable` (via the shared
 * `isAttributeInstanceDeletable` rule) and handles the emitted `delete`.
 */
@Component({
	selector: 'osee-attribute-delete-button',
	imports: [MatIconButton, MatIcon, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		@if (deletable()) {
			<button
				mat-icon-button
				type="button"
				[attr.aria-label]="ariaLabel()"
				matTooltip="Delete Attribute Instance"
				(click)="delete.emit()">
				<mat-icon class="tw-text-warning"
					>remove_circle_outline</mat-icon
				>
			</button>
		} @else {
			<span [matTooltip]="disabledReason()">
				<button
					mat-icon-button
					type="button"
					disabled
					[attr.aria-label]="disabledAriaLabel()">
					<mat-icon>remove_circle_outline</mat-icon>
				</button>
			</span>
		}
	`,
})
export class AttributeDeleteButtonComponent {
	/** Whether this instance can be deleted (drives enabled vs disabled variant). */
	deletable = input.required<boolean>();

	/** aria-label for the enabled delete button. */
	ariaLabel = input<string>('Delete attribute instance');

	/** Tooltip explaining why deletion is blocked (disabled variant). */
	disabledReason = input<string>('This attribute cannot be deleted.');

	/** aria-label for the disabled button. */
	disabledAriaLabel = input<string>('Cannot delete attribute');

	/** Emitted when the user clicks the enabled delete button. */
	readonly delete = output<void>();
}
