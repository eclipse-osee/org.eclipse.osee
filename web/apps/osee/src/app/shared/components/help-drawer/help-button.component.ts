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
	inject,
	input,
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { HelpDrawerService } from './help-drawer.service';

@Component({
	selector: 'osee-help-button',
	imports: [MatIconButton, MatIcon, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<button
			mat-icon-button
			type="button"
			matTooltip="Help"
			(click)="openHelp()">
			<mat-icon>help_outline</mat-icon>
		</button>
	`,
})
export class HelpButtonComponent {
	readonly topicId = input.required<string>();

	private readonly helpDrawerService = inject(HelpDrawerService);

	protected openHelp(): void {
		this.helpDrawerService.toggle(this.topicId());
	}
}
