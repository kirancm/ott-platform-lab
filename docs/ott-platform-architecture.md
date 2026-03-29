# OTT Platform Architecture

This diagram reflects the services and platform components currently present in this repository and Helm chart.

```mermaid
flowchart LR
    operator["Operator / Admin UI"]
    viewer["Viewer Apps"]
    ingress["NGINX Ingress"]
    opcon["OpCon Controller BFF\nSpring Boot + WebFlux"]
    keycloak["Keycloak"]
    search["Search Aggregator Service"]

    subgraph app["OTT Application Services"]
        content["Content Service"]
        cwm["CWM Service\nWorkflow + Jobs"]
        analytics["Analytics Service"]
    end

    subgraph data["Data & Messaging"]
        postgres["PostgreSQL"]
        redis["Redis"]
        kafka["Kafka"]
        elastic["Elasticsearch"]
    end

    subgraph obs["Observability"]
        prometheus["Prometheus"]
        grafana["Grafana"]
        jaeger["Jaeger"]
    end

    operator --> ingress
    viewer --> ingress

    ingress --> opcon
    ingress --> content
    ingress --> analytics
    ingress --> cwm

    opcon --> keycloak
    opcon --> cwm
    opcon --> content
    opcon --> search

    content --> postgres
    content --> redis
    content --> kafka

    cwm --> postgres
    cwm --> kafka

    analytics --> kafka
    analytics --> elastic

    opcon -. metrics/traces .-> prometheus
    content -. metrics/traces .-> prometheus
    cwm -. metrics/traces .-> prometheus
    analytics -. metrics/traces .-> prometheus
    prometheus --> grafana

    opcon -. tracing .-> jaeger
    content -. tracing .-> jaeger
    cwm -. tracing .-> jaeger
    analytics -. tracing .-> jaeger
```

## Notes

- `OpCon Controller` is the BFF for operator-facing workflows, search, and content views.
- `Search Aggregator Service` is included because `opcon-controller` is configured to call it, even though its code is not currently present in this repository.
- `Keycloak` is shown as an external identity provider used by `OpCon Controller`.
- `Prometheus`, `Grafana`, `Jaeger`, `Redis`, and `Elasticsearch` come from the Helm chart dependencies and values.
