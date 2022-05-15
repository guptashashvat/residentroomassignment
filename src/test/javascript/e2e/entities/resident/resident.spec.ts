import { browser, ExpectedConditions as ec /* , promise */ } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import {
  ResidentComponentsPage,
  /* ResidentDeleteDialog, */
  ResidentUpdatePage,
} from './resident.page-object';

const expect = chai.expect;

describe('Resident e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let residentComponentsPage: ResidentComponentsPage;
  let residentUpdatePage: ResidentUpdatePage;
  /* let residentDeleteDialog: ResidentDeleteDialog; */
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing(username, password);
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Residents', async () => {
    await navBarPage.goToEntity('resident');
    residentComponentsPage = new ResidentComponentsPage();
    await browser.wait(ec.visibilityOf(residentComponentsPage.title), 5000);
    expect(await residentComponentsPage.getTitle()).to.eq('Residents');
    await browser.wait(ec.or(ec.visibilityOf(residentComponentsPage.entities), ec.visibilityOf(residentComponentsPage.noResult)), 1000);
  });

  it('should load create Resident page', async () => {
    await residentComponentsPage.clickOnCreateButton();
    residentUpdatePage = new ResidentUpdatePage();
    expect(await residentUpdatePage.getPageTitle()).to.eq('Create or edit a Resident');
    await residentUpdatePage.cancel();
  });

  /* it('should create and save Residents', async () => {
        const nbButtonsBeforeCreate = await residentComponentsPage.countDeleteButtons();

        await residentComponentsPage.clickOnCreateButton();

        await promise.all([
            residentUpdatePage.setNameInput('name'),
            residentUpdatePage.setPhone_numberInput('5'),
            residentUpdatePage.setEmailInput('email'),
            residentUpdatePage.roomSelectLastOption(),
        ]);

        await residentUpdatePage.save();
        expect(await residentUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await residentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /* it('should delete last Resident', async () => {
        const nbButtonsBeforeDelete = await residentComponentsPage.countDeleteButtons();
        await residentComponentsPage.clickOnLastDeleteButton();

        residentDeleteDialog = new ResidentDeleteDialog();
        expect(await residentDeleteDialog.getDialogTitle())
            .to.eq('Are you sure you want to delete this Resident?');
        await residentDeleteDialog.clickOnConfirmButton();
        await browser.wait(ec.visibilityOf(residentComponentsPage.title), 5000);

        expect(await residentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
