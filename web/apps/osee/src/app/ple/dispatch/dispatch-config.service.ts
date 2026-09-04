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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import {
	Observable,
	catchError,
	forkJoin,
	map,
	of,
	shareReplay,
	switchMap,
} from 'rxjs';
import { apiURL } from '@osee/environments';
import { UiService } from '@osee/shared/services';
import type { DispatchConfig } from './dispatch.types';
import {
	DISPATCH_CONFIG_VERSION,
	DISPATCH_CONFIG_ARTIFACT_TYPE_ID,
	DISPATCH_CONFIG_JSON_ATTR_TYPE_ID,
} from './dispatch.constants';

export type DispatchPageConfig = {
	readonly artifactId: string;
	readonly name: string;
	readonly version: string;
	readonly config: DispatchConfig;
};

type ArtifactToken = {
	readonly id: string;
	readonly name: string;
};

type ArtifactAttribute = {
	readonly typeId: string;
	readonly value: unknown;
};

type ArtifactResponse = {
	readonly id: string;
	readonly name: string;
	readonly attributes: ArtifactAttribute[];
};

/**
 * Service that loads all DispatchConfig artifacts from the common branch.
 * Each artifact has one or more JSON attribute values (one per version).
 * The service loads the full artifact, iterates over its attributes,
 * and picks the JSON value whose version field matches DISPATCH_CONFIG_VERSION.
 */
@Injectable({
	providedIn: 'root',
})
export class DispatchConfigService {
	private readonly http = inject(HttpClient);
	private readonly uiService = inject(UiService);
	private readonly branch = '570';

	/**
	 * Shared, cached stream of the loaded configs. Both the index and page
	 * components subscribe to this, so navigating between them reuses the
	 * single fan-out fetch instead of re-loading every artifact. The buffer
	 * is replayed to late subscribers and kept alive for the app session
	 * (config artifacts on the common branch are effectively static).
	 */
	private readonly configs$ = this.buildConfigs().pipe(
		shareReplay({ bufferSize: 1, refCount: false })
	);

	/**
	 * Returns all DispatchConfig pages matching the frontend version. Cached:
	 * repeated calls share one underlying fetch.
	 */
	getConfigs(): Observable<DispatchPageConfig[]> {
		return this.configs$;
	}

	/**
	 * Fetches all DispatchConfig artifacts, loads each one, reads their
	 * JSON attributes, and returns only configs matching the frontend version.
	 */
	private buildConfigs(): Observable<DispatchPageConfig[]> {
		return this.getDispatchConfigArtifacts().pipe(
			switchMap((tokens) => {
				if (tokens.length === 0) return of([]);
				return forkJoin(
					tokens.map((token) => this.loadConfigArtifact(token))
				);
			}),
			map((configs) =>
				configs.filter((c): c is DispatchPageConfig => c !== null)
			),
			catchError((err) => {
				this.uiService.ErrorText = `[Dispatch] Failed to load configurations: ${err.message || err}`;
				return of([]);
			})
		);
	}

	private getDispatchConfigArtifacts(): Observable<ArtifactToken[]> {
		return this.http
			.get<ArtifactToken[]>(
				`${apiURL}/orcs/branch/${this.branch}/artifact/search/token`,
				{
					params: {
						artifactType: DISPATCH_CONFIG_ARTIFACT_TYPE_ID,
					},
				}
			)
			.pipe(
				catchError((err) => {
					this.uiService.ErrorText = `[Dispatch] Failed to search for DispatchConfig artifacts: ${err.message || err}`;
					return of([]);
				})
			);
	}

	/**
	 * Loads the full artifact and iterates over its attributes to find
	 * the Dispatch Config Json values. Returns the one matching
	 * DISPATCH_CONFIG_VERSION, or null if none match.
	 */
	private loadConfigArtifact(
		token: ArtifactToken
	): Observable<DispatchPageConfig | null> {
		const artifactUrl = `${apiURL}/orcs/branch/${this.branch}/artifact/${token.id}/related/direct`;

		return this.http
			.get<ArtifactResponse>(artifactUrl, {
				params: { includeRelations: 'false' },
			})
			.pipe(
				map((artifact) => {
					const jsonAttrs = artifact.attributes.filter(
						(attr) =>
							String(attr.typeId) ===
							DISPATCH_CONFIG_JSON_ATTR_TYPE_ID
					);

					for (const attr of jsonAttrs) {
						try {
							const raw =
								typeof attr.value === 'string'
									? attr.value
									: JSON.stringify(attr.value);
							const parsed = JSON.parse(raw);
							if (
								String(parsed.version) ===
								DISPATCH_CONFIG_VERSION
							) {
								return {
									artifactId: token.id,
									name: artifact.name || token.name,
									version: String(parsed.version),
									config: parsed as DispatchConfig,
								};
							}
						} catch (e) {
							this.uiService.ErrorText = `[Dispatch] Invalid JSON in attribute on artifact "${artifact.name || token.name}" (${token.id}): ${(e as Error).message}`;
						}
					}
					return null;
				}),
				catchError((err) => {
					this.uiService.ErrorText = `[Dispatch] Failed to load artifact "${token.name}" (${token.id}): ${err.message || err}`;
					return of(null);
				})
			);
	}
}
