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
	output,
	signal,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID, MULTIPLICITY_ID } from '@osee/attributes/constants';
import { applic } from '@osee/applicability/types';
import { PersistedArtifactAttributeEditorComponent } from '../persisted-artifact-attribute-editor/persisted-artifact-attribute-editor.component';

/** Maximum instances shown before collapsing. */
const COLLAPSE_LIMIT = 5;

@Component({
	selector: 'osee-attribute-group',
	imports: [
		MatIcon,
		MatIconButton,
		MatTooltip,
		PersistedArtifactAttributeEditorComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<div class="tw-flex tw-items-center tw-gap-1">
			<div class="tw-min-w-0 tw-flex-1">
				<span
					class="tw-mb-1 tw-block tw-text-xs tw-font-medium"
					data-testid="attribute-group-header"
					>{{ name() }}
					<span class="tw-opacity-50"
						>({{ attrs().length }})</span
					></span
				>
				<div
					class="tw-rounded tw-border tw-border-solid tw-border-osee-neutral-50 tw-p-2 dark:tw-border-osee-neutral-60"
					[class.tw-pr-0]="deleteMode()"
					[class.tw-pr-2]="!deleteMode()">
					<div class="tw-flex tw-flex-col tw-gap-2">
						@for (attr of visibleAttrs(); track attr.id) {
							<div class="tw-flex tw-items-center tw-gap-1">
								<div class="tw-min-w-0 tw-flex-1">
									<osee-persisted-artifact-attribute-editor
										[attr]="attr"
										[artifactId]="artifactId()"
										[artifactApplicability]="
											artifactApplicability()
										"
										[disabled]="disabled()"
										[showLabel]="false" />
								</div>
								@if (deleteMode() && isDeletable(attr)) {
									<button
										mat-icon-button
										(click)="deleteAttribute.emit(attr)"
										matTooltip="Delete Attribute Instance"
										aria-label="Delete attribute instance">
										<mat-icon class="tw-text-warning"
											>remove_circle_outline</mat-icon
										>
									</button>
								}
								@if (deleteMode() && !isDeletable(attr)) {
									<span
										matTooltip="Minimum required count reached.">
										<button
											mat-icon-button
											disabled
											aria-label="Cannot delete attribute">
											<mat-icon
												>remove_circle_outline</mat-icon
											>
										</button>
									</span>
								}
							</div>
						}
					</div>
					@if (attrs().length > collapseLimit) {
						<button
							class="tw-mt-2 tw-cursor-pointer tw-border-none tw-bg-transparent tw-p-0 tw-text-xs tw-text-primary"
							(click)="toggleExpanded()">
							@if (expanded()) {
								Show less
							} @else {
								Show
								{{ attrs().length - collapseLimit }}
								more...
							}
						</button>
					}
				</div>
			</div>
		</div>
	`,
})
export class AttributeGroupComponent {
	/** Group name (attribute type name). */
	name = input.required<string>();
	/** All attribute instances in this group. */
	attrs = input.required<attribute<string, ATTRIBUTETYPEID>[]>();
	/** Artifact ID for the persisted editors. */
	artifactId = input.required<`${number}`>();
	/** Artifact applicability. */
	artifactApplicability = input.required<applic>();
	/** Whether editing is disabled. */
	disabled = input(false);
	/** Whether delete mode is active. */
	deleteMode = input(false);
	/** All attributes on the artifact (needed for multiplicity checks). */
	allAttributes = input.required<attribute<string, ATTRIBUTETYPEID>[]>();

	/** Emitted when user clicks delete on an instance. */
	deleteAttribute = output<attribute<string, ATTRIBUTETYPEID>>();

	protected readonly collapseLimit = COLLAPSE_LIMIT;
	protected readonly expanded = signal(false);

	protected readonly visibleAttrs = computed(() => {
		if (this.expanded() || this.attrs().length <= COLLAPSE_LIMIT) {
			return this.attrs();
		}
		return this.attrs().slice(0, COLLAPSE_LIMIT);
	});

	protected toggleExpanded() {
		this.expanded.update((v) => !v);
	}

	protected isDeletable(attr: attribute<string, ATTRIBUTETYPEID>): boolean {
		if (attr.name?.toLowerCase() === 'name') {
			return false;
		}
		const multiplicityId = attr.multiplicity?.id;
		const allOfType = this.allAttributes().filter(
			(a) => a.typeId === attr.typeId
		);
		if (
			multiplicityId === MULTIPLICITY_ID.EXACTLY_ONE ||
			multiplicityId === MULTIPLICITY_ID.AT_LEAST_ONE
		) {
			return allOfType.length > 1;
		}
		return true;
	}
}
