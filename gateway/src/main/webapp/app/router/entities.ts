import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Rental = () => import('@/entities/rental/rental/rental.vue');
// prettier-ignore
const RentalUpdate = () => import('@/entities/rental/rental/rental-update.vue');
// prettier-ignore
const RentalDetails = () => import('@/entities/rental/rental/rental-details.vue');
// prettier-ignore
const Book = () => import('@/entities/book/book/book.vue');
// prettier-ignore
const BookUpdate = () => import('@/entities/book/book/book-update.vue');
// prettier-ignore
const BookDetails = () => import('@/entities/book/book/book-details.vue');
// prettier-ignore
const BookCatalog = () => import('@/entities/bookCatalog/book-catalog/book-catalog.vue');
// prettier-ignore
const BookCatalogUpdate = () => import('@/entities/bookCatalog/book-catalog/book-catalog-update.vue');
// prettier-ignore
const BookCatalogDetails = () => import('@/entities/bookCatalog/book-catalog/book-catalog-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/rental',
    name: 'Rental',
    component: Rental,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/rental/new',
    name: 'RentalCreate',
    component: RentalUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/rental/:rentalId/edit',
    name: 'RentalEdit',
    component: RentalUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/rental/:rentalId/view',
    name: 'RentalView',
    component: RentalDetails,
    meta: { authorities: [Authority.USER] },
  },

  {
    path: '/book',
    name: 'Book',
    component: Book,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/new',
    name: 'BookCreate',
    component: BookUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/:bookId/edit',
    name: 'BookEdit',
    component: BookUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book/:bookId/view',
    name: 'BookView',
    component: BookDetails,
    meta: { authorities: [Authority.USER] },
  },

  {
    path: '/book-catalog',
    name: 'BookCatalog',
    component: BookCatalog,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book-catalog/new',
    name: 'BookCatalogCreate',
    component: BookCatalogUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book-catalog/:bookCatalogId/edit',
    name: 'BookCatalogEdit',
    component: BookCatalogUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/book-catalog/:bookCatalogId/view',
    name: 'BookCatalogView',
    component: BookCatalogDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
