# Homefeed Coding Challenge

## Entscheidungs Log

- Erweiterbarkeit durch DI im HomefeedService des HomefeedModule interfaces
- Type safety durch sealed interfaces
  - Jedes Modul hat aktuell eine methode die den typ/namen des modules zurück gibt, bedeutet ich brauche aktuell keine Jackson Polymorphie in welcher er durch annotations gehandled wird wie TOs serialisiert werden
  - Vielleicht passe ich das später nochmal an
- REST response sieht aktuell wie folgt aus
```json
{
  "modules": [
    {
      "type": "banner",
      "items": [
        {
          "title": "",
          "message": "",
          "bannerType": "",
          "imageUrl": "",
          "iconName": "",
          "actionUrl": "",
          "actionLabel": ""
        }
      ]
    },
    {
      "type": "greeting",
      "items": [
        {
          "text": ""
        }
      ]
    }
  ]
}
```
Später würde ich die antwort eventuell anpassen um noch modul Ids zu untersützen um im frontend eine einfache identifizierung zu haben. Zusätzlich fällt noch auf, dass greeting zwar eine liste returned aber eigentlich immer nur ein item ist. Kann man fixen in dem man den payload auf des level des typen runtersetzt und in fällen wie bei dem banner trotzdem eine liste returned, aber um es einheitlich zu halten belasse ich es erstmal dabei.
Es sollte aber definitiv noch ein displayType zurück gegeben werden der sagt wie die items dargestellt werden sollen. So kann man bei einzelnen items in einer Liste sicher stellen, dass es auch so dargestellt wird: "single", "carousel", "grid"...
