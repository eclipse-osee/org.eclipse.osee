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
import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { ArtifactExplorerSettingsDialogComponent } from './artifact-explorer-settings-dialog/artifact-explorer-settings-dialog.component';
import { ArtifactExplorerPreferencesService } from '../../services/artifact-explorer-preferences.service';
import { filter, switchMap, take } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-artifact-explorer-user-menu',
	standalone: true,
	imports: [MatIcon, MatMenuItem],
	template: `
		<button
			mat-menu-item
			(click)="openSettingsDialog()"
			data-cy="artifact-explorer-settings">
			<mat-icon>settings</mat-icon><span>Settings</span>
		</button>
	`,
})
class ArtifactExplorerUserMenuComponent {
	private dialog = inject(MatDialog);
	private preferencesService = inject(ArtifactExplorerPreferencesService);

	private prefs =
		this.preferencesService.artifactExplorerPreferences.pipe(
			takeUntilDestroyed()
		);

	openSettingsDialog() {
		this.prefs
			.pipe(
				take(1),
				switchMap((prefs) =>
					this.dialog
						.open(ArtifactExplorerSettingsDialogComponent, {
							data: structuredClone(prefs),
						})
						.afterClosed()
						.pipe(
							take(1),
							filter((res) => res !== undefined && res !== ''),
							switchMap((res) =>
								this.preferencesService
									.createOrUpdateArtifactExplorerPrefs(res)
									.pipe(take(1))
							)
						)
				)
			)
			.subscribe();
	}
}
export default ArtifactExplorerUserMenuComponent;
