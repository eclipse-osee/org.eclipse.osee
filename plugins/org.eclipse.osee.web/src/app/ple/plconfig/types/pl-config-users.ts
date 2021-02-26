export interface userInterface {
    id: string,
    name: string,
    guid: null,
    active: boolean,
    description: null,
    workTypes: any[],
    tags: any[],
    userId: string,
    email: string,
    loginIds: string[],
    savedSearches: any[],
    userGroups: any[],
    artifactId: string,
    idString: string,
    idIntValue: number,
    uuid:number
}
export class user implements userInterface {
    id = '';
    name = '';
    guid = null;
    active = false;
    description = null;
    workTypes = [];
    tags= [];
    userId = '';
    email = '';
    loginIds = [];
    savedSearches = [];
    userGroups = [];
    artifactId = '';
    idString = '';
    idIntValue = 0;
    uuid = 0;
}