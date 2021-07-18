export interface IBookCatalog {
  id?: string;
  title?: string;
  author?: string;
  description?: string;
}

export class BookCatalog implements IBookCatalog {
  constructor(public id?: string, public title?: string, public author?: string, public description?: string) {}
}
