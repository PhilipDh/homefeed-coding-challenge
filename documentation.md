# Homefeed Coding Challenge

## Das Projekt Starten

```bash
# Application läuft auf Port 8080
./gradlew bootRun
```

### Beispiel Requests:

```bash
# Anonymer User
curl http://localhost:8080/api/v1/homefeed

# Angemeldeter User
curl -H "X-User-Id: user123" http://localhost:8080/api/v1/homefeed
```
Alternativ liegen auch im `resources/requests` directory requests die mit IntelliJ genutzt werden können
## Endpunkte:

Um die Daten für den Homefeed zu erhalten
- GET "/api/v1/homefeed"

Healthcheck über Spring Boot Actuator
- GET "/actuator/health"

### Generelle Architektur

Das Projekt folgt einer klassischen drei Schichten Architektur:
- Controller Layer
- Service Layer
- Data/Repository Layer

#### Controller Layer
Das Controller Layer enthält die REST Endpunkte der Applikation. Der `HomefeedController` nimmt HTTP Requests entgegen und delegiert die Verarbeitung an die Service Schicht. Nach dem Service Aufruf wird das Ergebnis in Transfer Objects gemapped, bevor es als JSON Response zurückgegeben wird.

Die bewusste Trennung zwischen internen Service Objekten und API Response Objekten bietet mehrere Vorteile: Änderungen an der internen Datenstruktur führen nicht automatisch zu Breaking Changes in der API. Zusätzlich können API spezifische Anforderungen wie Feldnamen oder Strukturen unabhängig von der Business Logik angepasst werden. Auch wenn die Datenstrukturen aktuell ähnlich aussehen, zahlt sich diese Entkopplung langfristig aus.

#### Service Layer
Im Service Layer liegt die Kern Business Logik. Der zentrale `HomefeedService` orchestriert den Datenabruf über eine plugin basierte Architektur: Alle verfügbaren Module (Banner, Greeting, Highlight) implementieren das gemeinsame `HomefeedModule` Interface. Über Springs Dependency Injection werden automatisch alle Implementierungen erkannt und zur Laufzeit bereitgestellt.

Ein kritischer Aspekt ist die Performance: Da der Homefeed zeitkritisch für das Frontend ist, werden alle Module parallel geladen. Das Laden der Module hat einen konfigurierbaren Timeout (250ms), um zu verhindern, dass langsame Module die gesamte Response verzögern. Module, die das Timeout überschreiten, werden aus der Response ausgeschlossen, und der User bekommt trotzdem einen funktionierenden Homefeed, nur mit weniger Content.

Die Module selbst befinden sich im `modules` Subpackage und enthalten die jeweilige Logik für ihren Content Type. Sie greifen auf Repositories zu, filtern und ranken Daten basierend auf User Präferenzen und bereiten sie für die Darstellung auf. 
Der `HomefeedService` cached die finale Response pro User, um wiederholte Aufrufe zu beschleunigen. Es wird ein Caffeine cache genutzt, aber es ist einfach diesen durch einen Distributed Cache, wie Redis, auszutauschen. 

#### Data/Repository Layer
Das Data Layer abstrahiert den Datenzugriff über Repository Interfaces. Jede Entität (Banner, User, Highlight.) hat ein entsprechendes Repository. 
Für diese Challenge liegen die Daten nur im Memory als einfache Listen, aber durch die Interface Abstraktion könnte problemlos auf eine echte Datenbank (JPA/Hibernate mit PostgreSQL) umgestellt werden, ohne dass höhere Schichten davon betroffen wären.


### Annahmen/Entscheidungen

#### API Design & Response Struktur

Jedes Modul in der Homefeed Response folgt einer einheitlichen Struktur:
```json
{
  "modules": [
    {
      "id": "banner_1771438455957_aee02a6d",
      "type": "banner",
      "displayType": "carousel",
      "items": [
        {
          "title": "Mid Season Sale",
          "message": "Up to 30% off on selected items. Limited time offer!",
          "bannerType": "PROMO",
          "imageUrl": "https://example.com/banners/mid-season-sale.jpg",
          "iconName": null,
          "actionUrl": "/products/sale",
          "actionLabel": "Shop Now"
        },
        {
          "title": "VIP Exclusive: Early Access",
          "message": "Get early access to our new collection. VIP members only!",
          "bannerType": "PROMO",
          "imageUrl": "https://example.com/banners/vip-exclusive.jpg",
          "iconName": null,
          "actionUrl": "/products/new-collection",
          "actionLabel": "Explore Now"
        },
        {
          "title": "New Features Available",
          "message": "Check out our improved search and filtering options!",
          "bannerType": "INFO",
          "imageUrl": null,
          "iconName": "https://example.com/icons/info.png",
          "actionUrl": "/features",
          "actionLabel": "Learn More"
        }
      ]
    },
    {
      "id": "greeting_1771438455957_690ad6ad",
      "type": "greeting",
      "displayType": "single",
      "items": [
        {
          "text": "Hello Anna Schmidt"
        }
      ]
    },
    {
      "id": "highlight_1771438455957_029fde6c",
      "type": "highlight",
      "displayType": "grid",
      "items": [
        {
          "title": "Spring Collection 2026",
          "description": "Discover the freshest styles for the new season. Light fabrics, vibrant colors.",
          "imageUrl": "https://cdn.example.com/highlights/spring-2026.jpg"
        },
        {
          "title": "Accessorize Your Style",
          "description": "Complete your look with bags, watches, and jewelry.",
          "imageUrl": "https://cdn.example.com/highlights/accessories-spring.jpg"
        }
      ]
    }
  ]
}
```

Diese homogene Struktur macht das Frontend einfacher, da alle Module gleich verarbeitet werden können. Der Nachteil ist, dass auch Module die semantisch nur ein einzelnes Item liefern (wie das Greeting Modul) trotzdem ein Array zurückgeben müssen. In zukünftiger Entwicklung könnte dies noch angepasst werden.

#### Type Safety mit Sealed Interfaces

Für die Type Safety der verschiedenen Module nutzt die Applikation Sealed Interfaces. Wenn ein neues Modul hinzugefügt wird man gezwungen mapper zu erweitern, da die Applikation sonst nicht kompiliert. Eine Alternative wäre es `@JsonTypeInfo` oder `@JsonSubTypes` zu nutzen, aber die Komplexität der Applikation ist aktuell gering genug um sealed interfaces zu nutzen. Im Rahmen von einem Update der Response Strukturierung würde dies nochmal ausgewertet werden.
Zusätzlich kann die Nutzung dieser Annotationen auch Fehler verstecken, da dies zur Compile Time nicht auffällt.

#### Modul-Implementierungen

Die drei implementierten Module wurden bewusst gewählt, um verschiedene Komplexitätsstufen abzudecken:

**Greeting Modul**: Das einfachste Modul - keine Datenbankzugriffe, nur simple String Manipulation basierend auf der User-ID.

**Banner Modul**: Mittlere Komplexität mit Repository Zugriff und einfacher Filterlogik. Banner werden basierend auf User Präferenzen (Interessen, Status) gefiltert. Demonstriert die Personalisierung anhand von User-Daten.

**Highlight Modul**: Die komplexeste Implementierung mit ausgelagerter Ranking Logik im `HighlightRankingService`. Hier findet ein mehrstufiger Prozess statt: Daten laden, nach Relevanz ranken, und Top-N selektieren. Zeigt, wie Module eigene Sub-Services nutzen können.

#### Authentication & User Context

Der Homefeed Endpunkt ist bewusst nicht authentifiziert - auch anonyme User sollen einen Homefeed sehen können. Um trotzdem Personalisierung zu ermöglichen, kann optional ein `X-User-Id` Header mitgeschickt werden:

- Mit Header: User bekommt personalisierten Content basierend auf seinen Präferenzen und historischen Interaktionen
- Ohne Header: User bekommt generic Content basierend auf allgemeinen Trends

In einer Produktionsumgebung würde dies durch richtige Authentication ersetzt werden (z.B. JWT Token). Der Token würde dann geparst werden um die User-ID zu extrahieren, aber der Endpunkt selbst bliebe öffentlich zugänglich. So können auch nicht angemeldete User browsen, während angemeldete User bessere Personalisierung bekommen. 
Der Endpunkt müsste natürlich Rate Limited sein, da keine Authentifizierung notwendig ist.

#### Vereinfachungen für die Challenge

**Entities & Datenmodell**: Um Entwicklungszeit zu sparen, wurden die Entities stark vereinfacht. Beziehungen zwischen Entitäten (1:n, n:n) sind als simple String Listen oder komma separierte Strings modelliert. In einer echten Anwendung müssten hier proper JPA Relations mit `@OneToMany`, `@ManyToMany` etc. verwendet werden.

**Caching Strategie**: Das aktuelle Caching findet auf Service Level statt; die gesamte Homefeed Response wird pro User gecached. Dies hat die folgenden Nachteile:
- Wenn ein Modul in den Timeout läuft, wird die Response ohne dieses Modul gecached
- Alle Module haben die gleiche TTL, obwohl manche Daten länger cacheable wären (z.B. Banner vs. personalisierte Highlights)

Eine bessere Lösung wäre Caching auf Modul Ebene mit individuellen TTLs. Dies wurde aus Zeitgründen nicht implementiert, ist aber in der "Weitere Entwicklung" Sektion notiert.


### Weitere Entwicklung

In den nächsten Schritten sollten die folgenden Dinge angegangen werden:
- Datenbank Anbindung
- Anpassung der Entities an ein ausgereifteres Datenmodell
- Authentifizierung
- Metrics
- Besseres Caching
- Mehr tests, speziell auch Unit Tests für die Module um hier die einzelne Funktionalität genauer zu testen

Weitere Ideen:
- API Versioning
  - Da Apps seltener geupdated werden als ein Backend sollte man eine Strategie haben um nicht inkompatible Module an die App zurück zu schicken, dies kann über eine Version gemacht werden den die  App mitschickt. Diese Info wird dann im `UserContext` und kann von jedem Modul ausgewertet werden
  - Für Changes die die gesamte Response ändern müsste ein neuer Endpunkt zu Verfügung gestellt werden

## Deployment in einer Cloud Umgebung

Für ein Deployment in AWS mit Kubernetes würde ich auf folgende Architektur setzen:

AWS Core Infrastructure:
- EKS für die Container Orchestrierung
- RDS PostgreSQL/Aurora als Datenbank mit Read Replicas (Homefeed ist read heavy)
- ElastiCache Redis für distributed caching
- Application Load Balancer für Traffic Distribution mit Health Checks
- CloudFront optional für Edge Caching von statischen Assets

Kubernetes Setup:
Im Deployment müssten Replicas über mehrere AZs verteilt werden mit entsprechenden Resource Limits für CPU und Memory. Für Auto Scaling würde ein HPA basierend auf CPU und Custom Metrics wie Request Rate zum Einsatz kommen. Configuration über ConfigMaps für Feature Flags und Timeouts, Secrets für DB Credentials über AWS Secrets Manager.

Readiness/Liveness Probes würden auf `/actuator/health` zeigen und die Rolling Update Strategy auf Zero Downtime setzen.

Event Driven Updates:
Für Cache Invalidierung würde ich auf Kafka oder SQS/SNS setzen. Events wie "BannerPublished" oder "UserPreferencesUpdated" könnten dann asynchron verarbeitet werden und gezielt Cache Keys invalidieren statt auf TTL zu warten.

Monitoring:
CloudWatch für basic Metrics, Prometheus + Grafana im Cluster für detaillierte Application Metrics. Tracing über X-Ray o.Ä. würde helfen Performance Bottlenecks zu identifizieren.
