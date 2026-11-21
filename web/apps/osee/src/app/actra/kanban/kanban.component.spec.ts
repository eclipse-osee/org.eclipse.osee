import { CdkDrag, CdkDropList } from '@angular/cdk/drag-drop';
import { provideHttpClient } from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { By } from '@angular/platform-browser';
import { apiURL } from '@osee/environments';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { KanbanComponent } from './kanban.component';

describe('KanbanComponent', () => {
	let component: KanbanComponent;
	let fixture: ComponentFixture<KanbanComponent>;
	let confirmation: Subject<boolean>;
	const openDialog = vi.fn(() => ({ afterClosed: () => confirmation }));

	beforeEach(async () => {
		confirmation = new Subject<boolean>();
		openDialog.mockClear();
		await TestBed.configureTestingModule({
			imports: [KanbanComponent, NoopAnimationsModule],
			providers: [
				provideHttpClient(),
				provideHttpClientTesting(),
				{
					provide: MatDialog,
					useValue: {
						open: openDialog,
						getDialogById: () => undefined,
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(KanbanComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		TestBed.tick();
	});

	it('moves a workflow card between columns from the selected work definition', async () => {
		expect(component).toBeTruthy();
		const http = TestBed.inject(HttpTestingController);
		http.expectOne(apiURL + '/ats/config').flush({
			topTeamDefinition: '1',
			idToTeamDef: { '100': { name: 'SAW SW', parentId: '1' } },
		});
		await fixture.whenStable();
		fixture.debugElement
			.query(By.css('#team-definition-filter'))
			.triggerEventHandler('selectionChange', { value: '100' });
		TestBed.tick();
		http.expectOne(apiURL + '/ats/workdef/teamdef/100').flush({
			states: [{ name: 'Open' }, { name: 'Complete' }],
		});
		http.expectOne(apiURL + '/ats/world/search').flush({
			results: [
				{
					id: '500',
					values: ['Requirements update', 'Open', 'SAW SW', 'Taylor'],
				},
			],
		});
		await fixture.whenStable();

		const headings = Array.from<HTMLElement>(
			fixture.nativeElement.querySelectorAll('h2')
		);
		expect(headings.map((heading) => heading.textContent.trim())).toEqual([
			'Open (1)',
			'Complete (0)',
		]);

		const [open, complete] = fixture.debugElement.queryAll(
			By.directive(CdkDropList)
		);
		const drop = {
			previousContainer: open.injector.get(CdkDropList),
			container: complete.injector.get(CdkDropList),
			item: open.query(By.directive(CdkDrag)).injector.get(CdkDrag),
		};
		complete.triggerEventHandler('cdkDropListDropped', {
			...drop,
			isPointerOverContainer: false,
		});
		TestBed.tick();
		expect(headings[0].textContent.trim()).toBe('Open (1)');
		expect(openDialog).not.toHaveBeenCalled();

		complete.triggerEventHandler('cdkDropListDropped', {
			...drop,
			isPointerOverContainer: true,
		});
		expect(openDialog).toHaveBeenCalledWith(
			expect.anything(),
			expect.objectContaining({
				data: expect.objectContaining({
					text: expect.stringContaining('Complete'),
				}),
			})
		);
		confirmation.next(false);
		confirmation.complete();
		TestBed.tick();
		expect(headings[0].textContent.trim()).toBe('Open (1)');

		confirmation = new Subject<boolean>();
		complete.triggerEventHandler('cdkDropListDropped', {
			...drop,
			isPointerOverContainer: true,
		});
		confirmation.next(true);
		confirmation.complete();
		TestBed.tick();
		expect(headings.map((heading) => heading.textContent.trim())).toEqual([
			'Open (0)',
			'Complete (1)',
		]);
		expect(complete.nativeElement.textContent).toContain(
			'Requirements update'
		);

		const cardElement = complete.query(By.directive(CdkDrag));
		const currentCard = cardElement.injector.get(CdkDrag);
		cardElement.triggerEventHandler('cdkDragStarted');
		TestBed.tick();
		expect(open.classes['tw-opacity-40']).toBe(true);
		expect(complete.classes['tw-opacity-40']).toBeFalsy();
		cardElement.triggerEventHandler('cdkDragEnded');
		TestBed.tick();
		expect(open.classes['tw-opacity-40']).toBeFalsy();
		expect(
			drop.previousContainer.enterPredicate(
				currentCard,
				drop.previousContainer
			)
		).toBe(false);
		open.triggerEventHandler('cdkDropListDropped', {
			previousContainer: drop.container,
			container: drop.previousContainer,
			item: currentCard,
			isPointerOverContainer: true,
		});
		TestBed.tick();
		expect(headings[1].textContent.trim()).toBe('Complete (1)');
		http.verify();
	});
});
