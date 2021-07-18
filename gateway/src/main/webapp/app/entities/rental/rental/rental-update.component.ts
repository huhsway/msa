import { Component, Vue, Inject } from 'vue-property-decorator';

import { numeric, required, minLength, maxLength, minValue, maxValue } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';
import { IRental, Rental } from '@/shared/model/rental/rental.model';
import RentalService from './rental.service';

const validations: any = {
  rental: {
    userId: {},
    rentalStatus: {},
  },
};

@Component({
  validations,
})
export default class RentalUpdate extends Vue {
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('rentalService') private rentalService: () => RentalService;
  public rental: IRental = new Rental();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.rentalId) {
        vm.retrieveRental(to.params.rentalId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.rental.id) {
      this.rentalService()
        .update(this.rental)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('gatewayApp.rentalRental.updated', { param: param.id });
          this.alertService().showAlert(message, 'info');
        });
    } else {
      this.rentalService()
        .create(this.rental)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('gatewayApp.rentalRental.created', { param: param.id });
          this.alertService().showAlert(message, 'success');
        });
    }
  }

  public retrieveRental(rentalId): void {
    this.rentalService()
      .find(rentalId)
      .then(res => {
        this.rental = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
