# Vaadin Web -harjoitustyo

CRM-tyyppinen Vaadin + Spring Boot -sovellus harjoitustyön vaatimuksiin.

## Kirjautuminen

Seedatut käyttäjät:

| Käyttäjä | Salasana | Rooli |
| --- | --- | --- |
| `admin` | `admin123` | `ADMIN` |
| `super` | `super123` | `SUPER` |
| `user` | `user123` | `USER` |

## Käynnistys Dockerilla

```bash
docker compose up --build
```

Sovellus avautuu osoitteeseen http://localhost:8080.

## Käynnistys paikallisesti

Tarvitset Java 21:n, Mavenin ja Node/npm:n Vaadin frontend -kehitystilaa varten.

```bash
mvn spring-boot:run
```

## OAuth2

GitHub- ja Google-kirjautuminen aktivoituu kopioimalla
`config/oauth.properties.example` tiedostoksi `config/oauth.properties` ja
tayttamalla client-id/client-secret -arvot. Tiedosto on gitignoressa.

Callback/redirect-osoitteet:

- GitHub: `http://localhost:8080/login/oauth2/code/github`
- Google: `http://localhost:8080/login/oauth2/code/google`

## Palautus

Raportti on tiedostossa `RAPORTTI.md`.
