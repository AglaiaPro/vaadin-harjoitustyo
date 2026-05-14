# Työraportti: Vaadin Web -harjoitustyö

GitHub-linkki: https://github.com/AglaiaPro/vaadin-harjoitustyo

## Toteutettu sovellus

Sovellus on CRM/projektienhallinta, jossa hallitaan asiakkaita, asiakasprofiileja,
projekteja ja teknologioita. Sovellus käyttää Vaadin Flowia, Spring Bootia,
Spring Securitya, Spring Data JPA:ta ja relaatiotietokantaa.

## Data, entiteetit ja CRUD

1. `Customer` on ensimmäinen entiteetti. Sille on lista, lomake, muokkaus,
poisto, repository ja tietokantaan asti toimivat CRUD-operaatiot.
2. `CustomerProfile` on 1:1-suhteessa `Customer`-entiteettiin. Suhde näkyy
   profiilien listassa asiakkaan nimenä ja asiakkaiden listassa yrityksenä.
3. `Project` on 1:N-suhteessa `Customer`-entiteettiin. Asiakkaalla voi olla
   monta projektia. Suhde näkyy asiakkaiden listassa projektien lukumääränä ja
   projektien listassa asiakkaan nimenä.
4. `Technology` on M:N-suhteessa `Project`-entiteettiin. Projektien listassa
   näkyvät teknologiat ja teknologioiden listassa projektien lukumäärä.
5. Kaikilla neljällä pääsisältöentiteetillä on vähintään viisi validoitavaa
   kenttää Bean Validation -annotaatioilla.

## Suodattaminen Criteria API:lla

Edistynyt haku on `AdvancedSearchView`-näkymässä ja toteutus on
`ProjectSearchService`-luokassa.

1. Haku tukee useita kenttiä: hakusana, status, prioriteetti, budjettiväli,
   asiakas, asiakkaan kaupunki ja teknologia.
2. Päivämäärähaku on toteutettu aloituspäivän ja deadline-päivän väleillä.
3. Relaatiohaku käyttää JOINia asiakkaaseen ja teknologioihin.
4. Relaatioentiteetin ominaisuuksista voi hakea asiakkaan kaupungilla,
   asiakkaan nimellä/sähköpostilla ja teknologian nimellä.
5. Monimutkainen haku on muodossa `(title OR description OR customer.email) AND muut ehdot`.

## Tyylit ja ulkoasu

1. Globaalit tyylit ovat `src/main/frontend/themes/harjoitustyo/styles.css`
   ja `theme.json`: fontti, väripaletti, kaarevuus, nappien varjo ja kenttien
   ulkoasu on muutettu.
2. Komponenttityylit: `addClassName`, `getStyle().set()` ja
   `addThemeVariants`/`setThemeVariants` ovat käytössä useissa näkymissä.
3. Näkymäkohtainen CSS on `src/main/frontend/styles/search-view.css`, ja se
   vaikuttaa vain hakunäkymän komponentteihin.
4. Lumo Utility -luokkia käytetään esimerkiksi `HomeView`- ja
   `AdvancedSearchView`-näkymissä: background, text color, padding, shadow,
   border radius, height/width, margin, font size ja display.
5. CSS-luokkien hover-, focus- ja transition-määritykset ovat globaalissa
   teemassa luokille kuten `metric-card` ja `smooth-button`.

## SPA-rakenne

1. `MainLayout` perii `AppLayout`-luokan ja sisältää headerin, navigaation ja
   footerin. Näkymät käyttävät `@Route(value = "...", layout = MainLayout.class)`.
2. Vähintään kolme erityyppistä näkymää: dashboard (`HomeView`), CRUD-split
   layoutit (`CustomerView`, `ProjectView` jne.) ja edistynyt hakunäkymä
   (`AdvancedSearchView`). Lisäksi profiili/tiedostot-näkymässä on upload- ja
   editoripainotteinen layout.
3. Headerissa on sovelluksen nimi, käyttäjätieto, logout-painike ja
   `DrawerToggle`.
4. Navigaatiossa on ikonit ja aktiivisen sivun korostus.
5. Footerissa on tekijän nimi, copyright ja lisälinkit. Footer on visuaalisesti
   erotettu ja sijoitettu drawerin alaosaan responsiivisesti.

## Autentikointi ja tietoturva

1. Spring Security + Vaadin -integrointi on `SecurityConfig`-luokassa. Käyttäjä
   on oma `AppUser`-entiteetti. Salasana tallennetaan BCrypt-hashina.
2. Kaikki näkevät päänäkymän, kirjautuneet näkevät sisältösivuja, `USER` ja
   `SUPER` näkevät projektisivun, ja admin-sivu on vain `ADMIN`-roolille.
3. Rekisteröitymissivu on `RegisterView`.
4. Käyttöoikeuden puuttuessa näytetään `AccessDeniedView`.
5. Käyttäjä voi lisätä oman kuvan `UserProfileView`-näkymässä.
6. GitHub/Google OAuth2 on toteutettu Spring Security OAuth2 Client
   -integraatiolla (`SecurityConfig`, `OAuth2AccountService`). Käytännön
   demossa riittää GitHub tai Google, koska vaatimus sanoo "Gmail tai GitHub".
   OAuth-salaisuuksia ei tallenneta GitHubiin, vaan ne luetaan paikallisesta
   `config/oauth.properties`-tiedostosta. Malli on tiedostossa
   `config/oauth.properties.example`. Redirect-osoitteet ovat
   `http://localhost:8080/login/oauth2/code/github` ja
   `http://localhost:8080/login/oauth2/code/google`.

## Muut toiminnallisuudet

1. Projekti on valmiina julkaistavaksi GitHubiin. Lisää GitHub-linkki ylle.
2. Vaadin Server Push on aktivoitu `@Push`-annotaatiolla ja `PushBroadcaster`illa.
3. Lokalisointi on toteutettu päänäkymässä suomi/englanti-valinnalla.
4. Docker-image on määritelty `Dockerfile`-tiedostossa.
5. `docker-compose.yml` käynnistää PostgreSQL-tietokannan ja Vaadin-sovelluksen.
6. Uuden käyttäjän luonnista lähetetään viesti ylläpitokäyttäjälle
   `MailService`-palvelussa.
7. Salasanan vaihto sähköpostilinkin avulla on toteutettu forgot/reset
   -näkymillä ja `PasswordResetToken`-entiteetillä.
8. Tiedoston lataus ja tallennus ovat `FilesView`-näkymässä.
9. CSV-tuonti ja -vienti ovat `FilesView`-näkymässä projekteille.
10. Spring Auditing on käytössä `AuditConfig`-luokassa ja `AuditableEntity`ssa.
11. Historiatiedot tallennetaan `HistoryEntry`-entiteettiin CRUD-muutoksista.
12. Historiatiedot näytetään `HistoryView`-näkymässä aikajanamaisena listana.
13. Ulkoinen JavaScript-komponentti Quill.js on lisätty `QuillEditor`-komponenttina.
