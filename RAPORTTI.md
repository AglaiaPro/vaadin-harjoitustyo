# Tyoraportti: Vaadin Web -harjoitustyo

GitHub-linkki: lisaa tahan repositorion URL julkaisemisen jalkeen.

## Toteutettu sovellus

Sovellus on CRM/projektienhallinta, jossa hallitaan asiakkaita, asiakasprofiileja,
projekteja ja teknologioita. Sovellus kayttaa Vaadin Flowia, Spring Bootia,
Spring Securitya, Spring Data JPA:ta ja relaatiotietokantaa.

## Data, entiteetit ja CRUD

1. `Customer` on ensimmainen entiteetti. Sille on lista, lomake, muokkaus,
poisto, repository ja tietokantaan asti toimivat CRUD-operaatiot.
2. `CustomerProfile` on 1:1-suhteessa `Customer`-entiteettiin. Suhde nakyy
   profiilien listassa asiakkaan nimena ja asiakkaiden listassa yrityksena.
3. `Project` on 1:N-suhteessa `Customer`-entiteettiin. Asiakkaalla voi olla
   monta projektia. Suhde nakyy asiakkaiden listassa projektien lukumaarana ja
   projektien listassa asiakkaan nimena.
4. `Technology` on M:N-suhteessa `Project`-entiteettiin. Projektien listassa
   nakyvat teknologiat ja teknologioiden listassa projektien lukumaara.
5. Kaikilla neljalla paasisaltoentiteetilla on vahintaan viisi validoitavaa
   kenttaa Bean Validation -annotaatioilla.

## Suodattaminen Criteria API:lla

Edistynyt haku on `AdvancedSearchView`-nakymassa ja toteutus on
`ProjectSearchService`-luokassa.

1. Haku tukee useita kenttia: hakusana, status, prioriteetti, budjettivali,
   asiakas, asiakkaan kaupunki ja teknologia.
2. Paivamaarahaku on toteutettu aloituspaivan ja deadline-paivan valeilla.
3. Relaatiohaku kayttaa JOINia asiakkaaseen ja teknologioihin.
4. Relaatioentiteetin ominaisuuksista voi hakea asiakkaan kaupungilla,
   asiakkaan nimella/sahkopostilla ja teknologian nimella.
5. Monimutkainen haku on muodossa `(title OR description OR customer.email) AND muut ehdot`.

## Tyylit ja ulkoasu

1. Globaalit tyylit ovat `src/main/frontend/themes/harjoitustyo/styles.css`
   ja `theme.json`: fontti, varipaletti, kaarevuus, nappien varjo ja kenttien
   ulkoasu on muutettu.
2. Komponenttityylit: `addClassName`, `getStyle().set()` ja
   `addThemeVariants`/`setThemeVariants` ovat kaytossa useissa nakymissa.
3. Nakymakohtainen CSS on `src/main/frontend/styles/search-view.css`, ja se
   vaikuttaa vain hakunakyman komponentteihin.
4. Lumo Utility -luokkia kaytetaan esimerkiksi `HomeView`- ja
   `AdvancedSearchView`-nakymissa: background, text color, padding, shadow,
   border radius, height/width, margin, font size ja display.
5. CSS-luokkien hover-, focus- ja transition-maaritykset ovat globaalissa
   teemassa luokille kuten `metric-card` ja `smooth-button`.

## SPA-rakenne

1. `MainLayout` perii `AppLayout`-luokan ja sisaltaa headerin, navigaation ja
   footerin. Nakymat kayttavat `@Route(value = "...", layout = MainLayout.class)`.
2. Vahintaan kolme erityyppista nakymaa: dashboard (`HomeView`), CRUD-split
   layoutit (`CustomerView`, `ProjectView` jne.) ja edistynyt hakunakymä
   (`AdvancedSearchView`). Lisaksi profiili/tiedostot-nakymassa on upload- ja
   editoripainotteinen layout.
3. Headerissa on sovelluksen nimi, kayttajatieto, logout-painike ja
   `DrawerToggle`.
4. Navigaatiossa on ikonit ja aktiivisen sivun korostus.
5. Footerissa on tekijan nimi, copyright ja lisalinkit. Footer on visuaalisesti
   erotettu ja sijoitettu drawerin alaosaan responsiivisesti.

## Autentikointi ja tietoturva

1. Spring Security + Vaadin -integrointi on `SecurityConfig`-luokassa. Kayttaja
   on oma `AppUser`-entiteetti. Salasana tallennetaan BCrypt-hashina.
2. Kaikki nakevat paanakyman, kirjautuneet nakevat sisaltosivuja, `USER` ja
   `SUPER` nakevat projektisivun, ja admin-sivu on vain `ADMIN`-roolille.
3. Rekisteroitymissivu on `RegisterView`.
4. Kayttooikeuden puuttuessa naytetaan `AccessDeniedView`.
5. Kayttaja voi lisata oman kuvan `UserProfileView`-nakymassa.
6. GitHub/Google OAuth2 on toteutettu Spring Security OAuth2 Client
   -integraatiolla (`SecurityConfig`, `OAuth2AccountService`). Kaytannon
   demossa riittaa GitHub tai Google, koska vaatimus sanoo "Gmail tai GitHub".
   OAuth-salaisuuksia ei tallenneta GitHubiin, vaan ne luetaan paikallisesta
   `config/oauth.properties`-tiedostosta. Malli on tiedostossa
   `config/oauth.properties.example`. Redirect-osoitteet ovat
   `http://localhost:8080/login/oauth2/code/github` ja
   `http://localhost:8080/login/oauth2/code/google`.

## Muut toiminnallisuudet

1. Projekti on valmiina julkaistavaksi GitHubiin. Lisaa GitHub-linkki ylle.
2. Vaadin Server Push on aktivoitu `@Push`-annotaatiolla ja `PushBroadcaster`illa.
3. Lokalisointi on toteutettu paanakymassa suomi/englanti-valinnalla.
4. Docker-image on maaritelty `Dockerfile`-tiedostossa.
5. `docker-compose.yml` kaynnistaa PostgreSQL-tietokannan ja Vaadin-sovelluksen.
6. Uuden kayttajan luonnista lahetetaan viesti yllapitokayttajalle
   `MailService`-palvelussa.
7. Salasanan vaihto sahkopostilinkin avulla on toteutettu forgot/reset
   -nakymilla ja `PasswordResetToken`-entiteetilla.
8. Tiedoston lataus ja tallennus ovat `FilesView`-nakymassa.
9. CSV-tuonti ja -vienti ovat `FilesView`-nakymassa projekteille.
10. Spring Auditing on kaytossa `AuditConfig`-luokassa ja `AuditableEntity`ssa.
11. Historiatiedot tallennetaan `HistoryEntry`-entiteettiin CRUD-muutoksista.
12. Historiatiedot naytetaan `HistoryView`-nakymassa aikajanamaisena listana.
13. Ulkoinen JavaScript-komponentti Quill.js on lisatty `QuillEditor`-komponenttina.
