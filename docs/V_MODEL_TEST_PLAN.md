# SentinelGate - V-Model Test ve Doğrulama Planı (Verification & Validation)

**Doküman Kodu:** SG-VMODEL-TP-01  
**Proje Adı:** SentinelGate - Kritik Altyapı İzleme ve Güvenli Mesajlaşma Backend Sistemi  
**Metodoloji:** V-Model (V-Shaped SDLC: Doğrulama ve Geçerleme)  
**Standartlar:** IEEE 829 (Test Dokümantasyonu), DO-178C (İzlenebilirlik Matrisi)  

---

## 1. V-Model Yaşam Döngüsü Eşlemesi

V-Model gereğince sol taraftaki her geliştirme aşaması, sağ taraftaki doğrudan ilgili test ve doğrulama aşamasıyla birebir eşlenmiştir:

```
[İş ve Kullanıcı Gereksinimleri] <====================> [Kullanıcı Kabul Testleri (UAT / Postman)]
       \                                                              /
   [Sistem Mimarisi & RBAC] <=====================> [Sistem & Entegrasyon Testleri]
          \                                                     /
      [Modül / Katman Tasarımı] <================> [Birim Testleri (JUnit 5 & Mockito)]
             \                                           /
              =========> [Kaynak Kod Geliştirme] ========
```

---

## 2. Test Fazları ve Seviyeleri

### 2.1. Birim Testleri (Unit Tests)
- **Kapsam:** Service ve Security bileşenlerinin iş mantığı doğrulama testleri.
- **Teknolojiler:** JUnit 5, Mockito.
- **Hedef:** `AuthService`, `TelemetryService`, `JwtTokenProvider` bileşenlerinde %85+ test kapsamı.
- **Kriter:** Dış bağımlılıklar (Database, Network) mock'lanmalıdır.

### 2.2. Entegrasyon Testleri (Integration Tests)
- **Kapsam:** Controller -> Service -> Repository katmanları arası etkileşim, Spring Security filtre zinciri, JWT doğrulama ve veri bütünlüğü.
- **Teknolojiler:** `@SpringBootTest`, `MockMvc`, Test H2 In-Memory DB.
- **Hedef:** 401 Unauthorized, 403 Forbidden ve 200/201 HTTP senaryolarının uçtan uca doğrulanması.

### 2.3. Sistem ve Güvenlik Testleri (Security & RBAC Tests)
- **Kapsam:** Rol Bazlı Yetkilendirme (RBAC) ihlalleri:
  - `ROLE_SENSOR`'ün operatör/admin verilerini sorgulayamaması (403 Forbidden).
  - Geçersiz/süresi dolmuş JWT ile erişim yapılamaması (401 Unauthorized).
  - Başarısız yetkilendirmelerin `audit_logs` tablosuna yazılması.

### 2.4. Kabul ve API Testleri (Acceptance / Postman Collection)
- **Kapsam:** Savunma Sanayi operatörü ve saha sensörü senaryolarını simüle eden Postman koleksiyonu (`postman/SentinelGate.postman_collection.json`).
- **Örnek Senaryo:**
  1. `admin` kullanıcısı ile token alma.
  2. `sensor-01` ile telemetri verisi basma (`POST /api/v1/telemetry`).
  3. `operator` ile telemetri verilerini filtreleyerek sorgulama (`GET /api/v1/telemetry`).
  4. Yetkisiz istek simülasyonu ve Audit Log kayıtlarının incelenmesi.

---

## 3. Gereksinim İzlenebilirlik Matrisi (Requirements Traceability Matrix - RTM)

| Gereksinim Kodu | Tanım | Geliştirme Bileşeni | Test / Doğrulama Seviyesi | Doğrulama Yöntemi |
|---|---|---|---|---|
| **REQ-SEC-01** | Stateless JWT Kimlik Doğrulama | `JwtTokenProvider`, `JwtAuthenticationFilter` | Birim & Entegrasyon | `JwtTokenProviderTest`, `AuthControllerTest` |
| **REQ-SEC-02** | Rol Bazlı Erişim Denetimi (RBAC) | `SecurityConfig`, `UserRole` | Sistem & Güvenlik | `SecurityRbacIntegrationTest` |
| **REQ-TEL-01** | Kritik Sensör Telemetri Veri Alımı | `TelemetryController`, `TelemetryService` | Birim & Entegrasyon | `TelemetryControllerTest`, `TelemetryServiceTest` |
| **REQ-AUD-01** | Güvenlik İhlali ve İşlem Denetim Loglama | `AuditLogFilter`, `AuditLogAspect` | Entegrasyon | `AuditLogIntegrationTest` |
| **REQ-OPS-01** | Container İçi Sağlık Kontrolü & Deploy | `Dockerfile`, `docker-compose.yml` | Dağıtım / Sistem | `actuator/health` probe doğrulama |

---

## 4. Test Yürütme ve Kabul Kriterleri (Exit Criteria)
1. Tüm birim ve entegrasyon testleri (`mvn test`) **sıfır hata (0 failures, 0 errors)** ile tamamlanmalıdır.
2. Derleme adımı (`mvn clean compile` / `mvn package`) hiçbir uyarı ve kırılma olmadan sonuçlanmalıdır.
3. Postman koleksiyonundaki tüm uçtan uca senaryolar HTTP 200/201 ve beklenen HTTP 401/403 kodlarını üretmelidir.
