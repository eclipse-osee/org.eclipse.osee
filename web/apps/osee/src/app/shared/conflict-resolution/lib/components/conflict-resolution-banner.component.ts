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
 * Reusable banner shown when another user changed the entity being edited while
 * the current user has unsaved edits. Presents a warning message plus the
 * conflict actions (resolve/merge and, optionally, discard-and-refresh).
 *
 * Presentation only, with ONE consistent look (a rounded, warning-outlined box)
 * so it reads the same on every page. It owns no layout: margins, stickiness and
 * placement are the consuming page's responsibility (wrap it as needed). This
 * keeps the component reusable on any page without page-specific variants.
 */
@Component({
	selector: 'osee-conflict-resolution-banner',
	imports: [MatIcon, MatIconButton, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div
			role="alert"
			class="tw-flex tw-items-center tw-gap-2 tw-rounded-md tw-border tw-border-solid tw-border-warning tw-px-3 tw-py-1 tw-text-sm">
			<mat-icon
				class="tw-shrink-0 tw-text-warning"
				fontSet="material-icons-outlined"
				>sync_problem</mat-icon
			>
			<span class="tw-flex-1">{{ message() }}</span>
			<button
				mat-icon-button
				class="tw-shrink-0 [--mdc-icon-button-state-layer-size:2rem]"
				matTooltip="Resolve conflicts between your changes and server changes."
				aria-label="Resolve conflicts"
				[disabled]="resolving()"
				(click)="resolve.emit()">
				<mat-icon>merge_type</mat-icon>
			</button>
			@if (showDiscard()) {
				<button
					mat-icon-button
					class="tw-shrink-0 [--mdc-icon-button-state-layer-size:2rem]"
					matTooltip="Discard local changes and load latest version."
					aria-label="Discard local changes"
					[disabled]="resolving()"
					(click)="discard.emit()">
					<mat-icon>refresh</mat-icon>
				</button>
			}
		</div>
	`,
})
export class ConflictResolutionBannerComponent {
	/** The warning message describing the conflict. */
	message = input<string>(
		'This item was modified by another user while you had unsaved changes.'
	);
	/** Whether a resolution is in progress (disables the action buttons). */
	resolving = input<boolean>(false);
	/** Whether to show the discard-and-refresh action. */
	showDiscard = input<boolean>(true);

	/** Emitted when the user asks to resolve/merge conflicts. */
	resolve = output<void>();
	/** Emitted when the user asks to discard local changes and refresh. */
	discard = output<void>();
}
