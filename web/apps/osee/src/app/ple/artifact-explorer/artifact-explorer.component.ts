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
	ElementRef,
	HostListener,
	inject,
	Input,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { fromEvent, take, takeUntil } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { ArtifactTabGroupComponent } from './lib/components/artifact-tab-group/artifact-tab-group.component';
import { ArtifactHierarchyPanelComponent } from './lib/components/hierarchy/artifact-hierarchy-panel/artifact-hierarchy-panel.component';
import { HierarchySection } from './lib/components/hierarchy/artifact-hierarchy-panel/artifact-hierarchy-panel.component';
import { ArtifactExplorerTabService } from './lib/services/artifact-explorer-tab.service';
import { ArtifactEditorDirtyService } from './lib/services/artifact-editor-dirty.service';

@Component({
	selector: 'osee-artifact-explorer',
	imports: [
		ArtifactHierarchyPanelComponent,
		ArtifactTabGroupComponent,
		CdkDropListGroup,
		MatIcon,
		MatIconButton,
		MatTooltip,
	],
	templateUrl: './artifact-explorer.component.html',
})
export class ArtifactExplorerComponent {
	private uiService = inject(UiService);
	private dirtyService = inject(ArtifactEditorDirtyService);
	private tabService = inject(ArtifactExplorerTabService);
	private destroyRef = inject(DestroyRef);

	constructor() {
		this.destroyRef.onDestroy(() => {
			document.body.style.cursor = '';
			document.body.style.userSelect = '';
		});
	}

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

	@Input() set branchType(branchType: 'working' | 'baseline' | '') {
		if (branchType != undefined) {
			this.uiService.typeValue = branchType;
		} else {
			this.uiService.typeValue = '';
		}
	}

	@Input() set branchId(branchId: string) {
		if (branchId != undefined) {
			this.uiService.idValue = branchId;
		} else {
			this.uiService.idValue = '';
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
			this.panelCollapsed.set(true);
		} else {
			this.activeSection.set(section);
			this.panelCollapsed.set(false);
		}
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
				this.panelWidthPercent.set(
					Math.max(15, Math.min(50, percent))
				);
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
