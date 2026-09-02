/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { CdkDropListGroup } from '@angular/cdk/drag-drop';
import {
	Component,
	DestroyRef,
	effect,
	ElementRef,
	HostListener,
	inject,
	input,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { fromEvent, take, takeUntil } from 'rxjs';
import { ArtifactTabGroupComponent } from './lib/components/artifact-tab-group/artifact-tab-group.component';
import { ArtifactExplorerSidebarComponent } from './lib/components/hierarchy/artifact-explorer-sidebar/artifact-explorer-sidebar.component';
import { HierarchySection } from './lib/components/hierarchy/artifact-explorer-sidebar/artifact-explorer-sidebar.component';
import { ArtifactExplorerTabService } from './lib/services/artifact-explorer-tab.service';
import { ArtifactEditorDirtyService } from './lib/services/artifact-editor-dirty.service';

@Component({
	selector: 'osee-artifact-explorer',
	imports: [
		ArtifactExplorerSidebarComponent,
		ArtifactTabGroupComponent,
		CdkDropListGroup,
		MatIcon,
		MatIconButton,
		MatTooltip,
	],
	templateUrl: './artifact-explorer.component.html',
})
export class ArtifactExplorerComponent {
	/** Route param for the active sidebar panel. */
	panel = input<string>('');

	private dirtyService = inject(ArtifactEditorDirtyService);
	private tabService = inject(ArtifactExplorerTabService);
	private destroyRef = inject(DestroyRef);
	private router = inject(Router);

	constructor() {
		this.destroyRef.onDestroy(() => {
			document.body.style.cursor = '';
			document.body.style.userSelect = '';
		});
	}

	/** Sync route param to activeSection on init. */
	private readonly syncPanelFromRoute = effect(() => {
		const panel = this.panel();
		if (panel === 'hierarchy' || panel === 'search' || panel === 'branch') {
			this.activeSection.set(panel);
			this.panelCollapsed.set(false);
		}
	});

	/** Reference to the main layout container for percentage calculations. */
	protected layoutContainer =
		viewChild<ElementRef<HTMLElement>>('layoutContainer');

	/** Prevent browser tab/window close when there are unsaved changes. */
	@HostListener('window:beforeunload', ['$event'])
	onBeforeUnload(event: BeforeUnloadEvent) {
		if (this.dirtyService.hasDirtyEditors()) {
			event.preventDefault();
		}
	}

	/** Panel width as a percentage of the layout container (20-80). */
	protected panelWidthPercent = signal(25);
	protected panelCollapsed = signal(false);
	protected activeSection = signal<HierarchySection>('hierarchy');

	/**
	 * Toggle a section in the activity bar.
	 * If the clicked section is already active and the panel is open, collapse it.
	 * Otherwise, set the section and ensure the panel is open.
	 */
	toggleSection(section: HierarchySection) {
		if (!this.panelCollapsed() && this.activeSection() === section) {
			// Collapsing the active panel: drop the section from the URL so the
			// route no longer advertises an open panel and syncPanelFromRoute
			// won't re-open it on reload.
			this.panelCollapsed.set(true);
			this.router.navigate(['/ple/artifact/explorer'], {
				queryParamsHandling: 'merge',
				replaceUrl: true,
			});
			return;
		}
		// Activating a section: reflect it in the URL and open the panel.
		this.activeSection.set(section);
		this.panelCollapsed.set(false);
		this.router.navigate(['/ple/artifact/explorer', section], {
			queryParamsHandling: 'merge',
			replaceUrl: true,
		});
	}

	onDividerMouseDown(event: MouseEvent): void {
		event.preventDefault();
		document.body.style.cursor = 'col-resize';
		document.body.style.userSelect = 'none';

		const container = this.layoutContainer()?.nativeElement;
		if (!container) return;

		// Calculate the offset between where the mouse is and where the divider currently sits
		// so dragging doesn't cause a jump.
		const rect = container.getBoundingClientRect();
		const currentPercent = this.panelWidthPercent();
		const currentPx = (currentPercent / 100) * rect.width;
		const offsetX = event.clientX - rect.left - currentPx;

		const mouseup$ = fromEvent(document, 'mouseup').pipe(take(1));

		fromEvent<MouseEvent>(document, 'mousemove')
			.pipe(takeUntil(mouseup$), takeUntilDestroyed(this.destroyRef))
			.subscribe((e) => {
				const percent =
					((e.clientX - rect.left - offsetX) / rect.width) * 100;
				this.panelWidthPercent.set(Math.max(15, Math.min(50, percent)));
			});

		mouseup$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
			document.body.style.cursor = '';
			document.body.style.userSelect = '';
		});
	}

	onDividerKeyDown(event: KeyboardEvent): void {
		const step = 2;
		if (event.key === 'ArrowLeft') {
			event.preventDefault();
			this.panelWidthPercent.update((v) => Math.max(15, v - step));
		} else if (event.key === 'ArrowRight') {
			event.preventDefault();
			this.panelWidthPercent.update((v) => Math.min(50, v + step));
		}
	}
}

export default ArtifactExplorerComponent;
