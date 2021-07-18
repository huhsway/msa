export interface IBook {
  id?: number;
  title?: string;
  author?: string;
  description?: string;
}

export class Book implements IBook {
  constructor(public id?: number, public title?: string, public author?: string, public description?: string) {}
}
