# DairyCart

DairyCart is a learning MVP for a small dairy-products shop. Customers can browse milk, ghee, paneer, curd, butter, and lassi, add products to a basket, and place a demo pay-on-delivery order. A protected admin page lets the store manager add products.

## Project architecture

DairyCart uses a three-tier architecture. Each tier has a distinct responsibility:

### 1. Presentation tier

The presentation tier handles browser requests and renders the user interface.

- **Technology:** Spring MVC controllers and Thymeleaf templates.
- **Controllers:** `src/main/java/com/dairycart/web`
- **Pages and shared fragments:** `src/main/resources/templates`
- **Styles and browser scripts:** `src/main/resources/static`
- **Responsibilities:** display the catalog, accept search and category filters, manage basket and checkout forms, and show order confirmations.

### 2. Application tier

The application tier contains the shop's business rules and coordinates work between the web and data tiers.

- **Catalog:** `src/main/java/com/dairycart/catalog`
- **Basket:** `src/main/java/com/dairycart/cart`
- **Orders and checkout:** `src/main/java/com/dairycart/orders`
- **Web configuration and startup seed data:** `src/main/java/com/dairycart/config`
- **Responsibilities:** validate basket quantities, check stock, calculate totals, validate checkout details, and coordinate order placement.

### 3. Data tier

The data tier persists and retrieves catalog, inventory, and order information using JDBC.

- **Repositories:** `JdbcProductRepository` and `JdbcOrderRepository` in the catalog and orders packages.
- **Schema:** `src/main/resources/schema.sql`
- **Local development:** H2 in-memory database by default, or PostgreSQL through Docker Compose.
- **Responsibilities:** store products and stock, save orders and their items, and reserve stock when an order is placed.

## Request and order flow

1. A browser request reaches a Spring MVC controller in the presentation tier.
2. The controller calls a service in the application tier to apply shop rules.
3. The service reads or writes data through a repository in the data tier.
4. The controller returns a Thymeleaf page showing the result.

For checkout, the application validates the submitted customer details and basket, reserves available stock, and saves an order with its line items in a database transaction. The confirmation page displays the saved order. The basket is stored in the user's HTTP session.

## Main features

- Product browsing, search, and category filtering
- Session-based basket with quantity and stock checks
- Live basket total updates
- Guest checkout with input validation
- Demo pay-on-delivery orders and order confirmation
- Administrator sign-in and product creation

This is a learning MVP, not a production-ready store. It does not include customer accounts, online payments, delivery integrations, or production operations features.

## Run locally

Requirements: Java 17+ and Maven 3.9+.

Start with the default H2 in-memory database:

```sh
mvn spring-boot:run
```

Open [http://localhost:5432](http://localhost:5432). H2 data is recreated when the application restarts.

To run the app with PostgreSQL using Docker Compose:

```sh
docker compose up --build
```

Compose exposes the application at [http://localhost:5432](http://localhost:5432) and PostgreSQL at `localhost:5433`. Within the Compose network, the app connects to PostgreSQL on port `5432`.

The local admin page is [http://localhost:5432/admin](http://localhost:5432/admin). It redirects to a sign-in form. Local-only credentials are `localadmin` / `dairycart-local-only`; override them with `ADMIN_USERNAME` and `ADMIN_PASSWORD` before sharing the app. The demo database credentials in `compose.yaml` are for local development only.

## Build and test

```sh
mvn test
mvn package
```

## Current scope

The project currently focuses on the Java application and its local database setup. AWS database configuration and AWS deployment are not part of this README yet.
