# SentinelGate - Kritik Altyapı İzleme ve Güvenli Mesajlaşma Backend Sistemi

[![V-Model](https://img.shields.io/badge/SDLC-V--Model-blue.svg)](docs/V_MODEL_TEST_PLAN.md)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-Stateless%20JWT%20%2B%20RBAC-red.svg)](docs/ARCHITECTURE.md)
[![License](https://img.shields.io/badge/License-Proprietary%20Defense-black.svg)](#)

SentinelGate; askeri üsler, enerji nakil şebekeleri, nükleer tesisler ve kritik kamu altyapılarında görev yapan saha sensörlerinden gelen telemetri verilerini güvenli (şifreli, doğrulanabilir) toplayan, operatör ekranlarına anlık ve güvenilir biçimde ileten, tüm kritik adımları ve yetkisiz erişim denemelerini kayıt altına alan **Savunma Sanayi sınıfı bir Backend platformudur**.

Proje, **V-Model (V-Shaped SDLC)** prensiplerine göre gereksinimden kabul testine kadar tam izlenebilirlik (Traceability) ilkelerine bağlı kalınarak geliştirilmiştir.

---

## 🌟 Temel Özellikler

- **V-Model Metodolojisi:** DO-178C ve IEEE standartları referans alınarak her geliştirme fazına karşılık gelen bir doğrulama ve test planı.
- **Zero-Trust & Stateless JWT:** `io.jsonwebtoken (JJWT 0.12.x)` ile 256-bit HMAC-SHA algoritmalı güvenli token üretimi ve doğrulaması.
- **Rol Bazlı Erişim Denetimi (RBAC):**
  - `ROLE_ADMIN`: Sistem yönetimi, denetim (audit) logları inceleme, kullanıcı yetkilendirme.
  - `ROLE_OPERATOR`: Telemetri izleme, durum değerlendirme ve sahadan gelen alarmları sorgulama.
  - `ROLE_SENSOR`: Yalnızca telemetri verisi gönderme iznine sahip kısıtlı IoT / saha rolü.
- **Otomatik Audit Log Sistemi:** Kritik işlemlerin yanı sıra sisteme yönelik tüm yetkisiz erişim denemelerini (HTTP 401 / 403) istemci IP'si, uç nokta, metod ve detaylarla veritabanında denetim altına alma.
- **OpenAPI 3 / Swagger Entegrasyonu:** Yetkilendirme başlığı (Bearer Token) destekli interaktif API dokümantasyonu.
- **Multi-Stage Docker & Düşük Bellek Optimizasyonu:** Render / Koyeb gibi bulut ortamlarının ücretsiz katmanlarında sorunsuz çalışması için `-Xmx350m` optimize edilmiş distroless/alpine konteyner mimarisi.

---

## 🏗️ Katmanlı Mimari ve Dizin Yapısı

Detaylı mimari dokümantasyon için [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) ve test planı için [docs/V_MODEL_TEST_PLAN.md](docs/V_MODEL_TEST_PLAN.md) dosyalarını inceleyebilirsiniz.

```
com.sentinelgate
├── audit          # Otomatik işlem ve güvenlik ihlali denetim mekanizması (AOP / Filter)
├── config         # Swagger, WebMvc, Güvenlik ve Genel Ayarlar
├── controller     # REST API Uç Noktaları (Auth, Telemetry, Audit)
├── dto            # Request / Response DTO Sözleşmeleri
├── entity         # Veritabanı Varlıkları (User, Telemetry, AuditLog)
├── exception      # Global Exception Handler ve Özel İstisnalar
├── repository     # Spring Data JPA Arayüzleri
├── security       # JWT Filter, Token Provider, UserDetailsService
└── service        # İş Mantığı ve Seed Data Servisleri
```

---

## 🚀 Hızlı Başlangıç

### Gereksinimler
- **Java 17 (Eclipse Temurin önerilir)**
- **Maven 3.9+**
- **Docker & Docker Compose** (Opsiyonel: Konteynerize ortam için)
- **PostgreSQL 15+** (Lokal kurulum için)

### 1. Docker Compose ile Tek Komutta Başlatma (Tavsiye Edilen)
SentinelGate API ve PostgreSQL veritabanını izole bir ağda ayağa kaldırmak için:

```bash
docker-compose up --build -d
```

Servisler ayağa kalktığında:
- **API URL:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **Healthcheck:** `http://localhost:8080/actuator/health`

### 2. Yerel Ortamda Derleme ve Çalıştırma
```bash
# Bağımlılıkları çekme ve derleme
mvn clean compile

# Testleri yürütme
mvn test

# Uygulamayı başlatma
mvn spring-boot:run
```

---

## 🔒 Demo Kullanıcı Hesapları (Otomatik Seed Data)

Uygulama ilk ayağa kalktığında `SeedDataService` tarafından idempotent (yoksa oluşturan) demo kullanıcılar tanımlanır:

| Kullanıcı Adı | Şifre | Rol | Yetki Kapsamı |
|---|---|---|---|
| `admin` | `AdminPass123!` | `ROLE_ADMIN` | Tüm sistem yönetimi ve Audit Logları |
| `operator` | `OperatorPass123!` | `ROLE_OPERATOR` | Telemetri sorgulama ve izleme |
| `sensor-01` | `SensorPass123!` | `ROLE_SENSOR` | Telemetri verisi gönderme |

---

## 🧪 Postman ile Test
Hazır senaryolar içeren test koleksiyonu `postman/SentinelGate.postman_collection.json` dosyasında yer almaktadır. Postman'e aktararak tüm senaryoları otomatik koşabilirsiniz.

---

## 📄 Lisans
Bu proje Savunma Sanayi Kritik Altyapı Güvenliği spesifikasyonlarına uygun olarak geliştirilmiştir.
