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
import { HelpAnchorDirective } from '../../help-drawer/help-anchor.directive';

/**
 * Shared presentational toolbar controls for adding attributes and toggling
 * delete mode. Used by both the create dialog title bar and the artifact
 * editor toolbar so the two affordances look and behave identically.
 *
 * Purely presentational: it emits `add` / `toggleDeleteMode` and lets the
 * parent perform the work (open the add dialog, flip the delete-mode signal).
 * The `deleteMode` input only drives the button's icon/color/tooltip.
 *
 * `showAdd` lets a consumer hide the add button when there is nothing left to
 * add. Help-anchor ids are optional; when empty the anchor never matches, so
 * consumers that don't use the help drawer can ignore them.
 */
@Component({
	selector: 'osee-attribute-toolbar',
	imports: [MatIconButton, MatIcon, MatTooltip, HelpAnchorDirective],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		@if (showAdd()) {
			<button
				mat-icon-button
				type="button"
				(click)="add.emit()"
				matTooltip="Add Attribute"
				aria-label="Add Attribute"
				[oseeHelpAnchor]="addHelpAnchor()">
				<mat-icon>add_circle_outline</mat-icon>
			</button>
		}
		<button
			mat-icon-button
			type="button"
			(click)="toggleDeleteMode.emit()"
			[matTooltip]="
				deleteMode() ? 'Exit delete mode' : 'Delete attributes'
			"
			aria-label="Toggle Delete Mode"
			[class]="deleteMode() ? 'tw-text-warning' : ''"
			[oseeHelpAnchor]="deleteHelpAnchor()">
			<mat-icon>{{
				deleteMode() ? 'delete_sweep' : 'delete_outline'
			}}</mat-icon>
		</button>
	`,
})
export class AttributeToolbarComponent {
	/** Whether the add button is shown (hidden when nothing is addable). */
	showAdd = input<boolean>(true);

	/** Whether delete mode is active (drives the toggle icon/color/tooltip). */
	deleteMode = input<boolean>(false);

	/** Optional help-drawer anchor id for the add button. */
	addHelpAnchor = input<string>('');

	/** Optional help-drawer anchor id for the delete-mode toggle. */
	deleteHelpAnchor = input<string>('');

	/** Emitted when the user clicks the add button. */
	readonly add = output<void>();

	/** Emitted when the user toggles delete mode. */
	readonly toggleDeleteMode = output<void>();
}
