export type KanbanWorkflow = {
	readonly id: string;
	readonly title: string;
	readonly state: string;
	readonly team: string;
	readonly assignees: readonly string[];
	readonly description?: string;
};
