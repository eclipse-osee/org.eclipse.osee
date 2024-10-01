/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { MatAnchor } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import {
	MimRouteService,
	SharedConnectionUIService,
	SharedStructureUIService,
} from '@osee/messaging/shared/services';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { combineLatest, iif, of } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-header',
	styles: [],
	standalone: true,
	imports: [MatAnchor, RouterLink, AsyncPipe],
	template: `<p class="tw-line-clamp-1 tw-truncate">
		@for (link of route | async; track link.routerLink) {
			@if (link.displayName !== '') {
				<a
					mat-button
					type="button"
					[routerLink]="link.routerLink"
					queryParamsHandling="merge"
					>{{ link.displayName }}</a
				>
				/
			}
		}
	</p>`,
})
export class MimHeaderComponent {
	private _routeService = inject(MimRouteService);
	private _currentBranchService = inject(CurrentBranchInfoService);
	private _connectionService = inject(SharedConnectionUIService);
	private _structureService = inject(SharedStructureUIService);

	private _submessageId = toObservable(this._routeService.submessageId);
	private _connectionId = toObservable(this._routeService.connectionId);
	route = combineLatest([
		this._routeService.type,
		this._routeService.id,
		this._connectionId,
		this._routeService.messageId,
		this._submessageId,
		this._routeService.submessageToStructureBreadCrumbs,
		this._routeService.singleStructureId,
	]).pipe(
		debounceTime(0),
		switchMap(
			([
				type,
				id,
				connection,
				message,
				submessage,
				submessageToStructureBreadCrumbs,
				singleStructureId,
			]) =>
				iif(
					() => type !== '',
					iif(
						() => id != '',
						this._currentBranchService.currentBranch.pipe(
							switchMap((detail) =>
								iif(
									() => connection !== '-1',
									this._connectionService.connection.pipe(
										switchMap((connectionDetails) =>
											iif(
												() => message !== '',
												iif(
													() => submessage !== '-1',
													iif(
														() =>
															singleStructureId !==
															'',
														this._structureService.structure.pipe(
															switchMap(
																(structure) =>
																	of([
																		{
																			displayName:
																				type,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type,
																		},
																		{
																			displayName:
																				detail.name,
																			routerLink:
																				'/ple/messaging/' +
																				'connections' +
																				'/' +
																				type +
																				'/' +
																				id +
																				'/',
																		},
																		{
																			displayName:
																				connectionDetails
																					.name
																					.value,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages',
																		},
																		{
																			displayName:
																				submessageToStructureBreadCrumbs,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/elements',
																		},
																		{
																			displayName:
																				structure
																					.name
																					.value,
																			routerLink:
																				'/ple/messaging/' +
																				'connections/' +
																				type +
																				'/' +
																				id +
																				'/' +
																				connection +
																				'/messages/' +
																				message +
																				'/' +
																				submessage +
																				'/elements/' +
																				singleStructureId,
																		},
																	])
															)
														), //submessage,
														of([
															{
																displayName:
																	type,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type,
															},
															{
																displayName:
																	detail.name,
																routerLink:
																	'/ple/messaging/' +
																	'connections' +
																	'/' +
																	type +
																	'/' +
																	id +
																	'/',
															},
															{
																displayName:
																	connectionDetails
																		.name
																		.value,
																routerLink:
																	'/ple/messaging/' +
																	'connections/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	connection +
																	'/messages',
															},
															{
																displayName:
																	submessageToStructureBreadCrumbs,
																routerLink:
																	'/ple/messaging/' +
																	'connections/' +
																	type +
																	'/' +
																	id +
																	'/' +
																	connection +
																	'/messages/' +
																	message +
																	'/' +
																	submessage +
																	'/elements',
															},
														]) //submessage)
													),
													of() //message, DNE
												),
												of([
													{
														displayName: type,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type,
													},
													{
														displayName:
															detail.name,
														routerLink:
															'/ple/messaging/' +
															'connections' +
															'/' +
															type +
															'/' +
															id +
															'/',
													},
													{
														displayName:
															connectionDetails
																.name.value,
														routerLink:
															'/ple/messaging/' +
															'connections/' +
															type +
															'/' +
															id +
															'/' +
															connection +
															'/messages',
													},
												])
											)
										)
									),
									of([
										{
											displayName: type,
											routerLink:
												'/ple/messaging/' +
												'connections' +
												'/' +
												type,
										},
										{
											displayName: detail.name,
											routerLink: type + '/' + id,
										},
									])
								)
							)
						),
						of([{ displayName: type, routerLink: type }])
					),
					of()
				)
		)
	);
}

export default MimHeaderComponent;
