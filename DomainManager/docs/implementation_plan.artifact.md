# Domain Management System Refactoring

Refactor `DomainManager` and `DomainProbe` to support multi-protocol, sub-domain mapping, and robust Root Seed based domain switching.

## Proposed Changes

### DomainManager Component

#### [MODIFY] [DomainManager.kt](file:///Users/neo.chang/Documents/AndroidStudioProjects/LANG/GeneralModule/DomainManager/src/commonMain/kotlin/idv/neo/module/network/domain/DomainManager.kt)
- Introduce `ServicePattern` data class to define protocol and sub-domain prefix.
- Refactor `DomainManager` to store `rootSeeds` instead of full URLs.
- Implement `getUrl(serviceKey: String)` with automatic URL normalization (ensuring trailing `/`).
- Add logic to extract Root Domain from full URLs for compatibility and seed discovery.
- Keep `getActiveUrl()` as a shortcut for the "API" service to maintain backward compatibility.

### DomainProbe Component

#### [MODIFY] [DomainProbe.kt](file:///Users/neo.chang/Documents/AndroidStudioProjects/LANG/GeneralModule/DomainProbe/src/commonMain/kotlin/idv/neo/module/network/domain/probe/DomainProbe.kt)
- Update `probeAndSortDomains` to support business-aware probing using `serviceKey`.
- Implement an option to use an internal, isolated `HttpClient` for probing to avoid interference from business plugins.
- Refine the two-phase probing logic (TTFB followed by background Body download).

### Dependency Injection

#### [MODIFY] [DomainModule.kt](file:///Users/neo.chang/Documents/AndroidStudioProjects/LANG/GeneralModule/DomainManager/src/commonMain/kotlin/idv/neo/di/moudle/DomainModule.kt)
- Update Koin definition to support the new `DomainManager` constructor.
- Provide a default `ServicePattern` map if none is injected from the application level.

## Verification Plan

### Manual Verification
- Verify that `getActiveUrl()` still returns a valid API URL for existing services.
- Verify that `getUrl("CHAT")` returns the correct WSS address.
- Run the desktop app and observe the `DomainProbe` logs to ensure seeds are correctly sorted and updated.
