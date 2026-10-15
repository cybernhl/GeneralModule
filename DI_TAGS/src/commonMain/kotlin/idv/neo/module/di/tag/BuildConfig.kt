package idv.neo.module.di.tag

const val APP_NAME_TAG: String = "APP_NAME"

const val APP_VERSION_TAG: String = "APP_VERSION"

const val APP_RUNTIME_TYPE_TAG: String = "BUILD_TYPE"
const val APP_RUNTIME_ENV_TAG: String = "FLAVOR_ENV"

const val APP_API_URL_TAG: String = "API_URL"
const val APP_API_URLS_TAG: String = "API_URLS"

const val APP_RUNTIME_SESSION_TAG: String = "RUNTIME_SESSION"

const val NETWORK_DIAGNOSTIC_SLOW_REQUEST_THRESHOLD_TAG: String = "SLOW_REQUEST_THRESHOLD"

const val HTTP_CLIENT_DEFAULT_PLUGINS_TAG: String = "HTTP_CLIENT_DEFAULT_PLUGINS"

const val HTTP_CLIENT_PLUGINS_TAG: String = "HTTP_CLIENT_PLUGINS"

const val HTTP_CLIENT_WEBSOCKET_TAG: String = "HTTP_CLIENT_WEBSOCKET"

const val HTTP_CLIENT_WEBSOCKET_PLUGINS_TAG: String = "HTTP_CLIENT_WEBSOCKET_PLUGINS"

const val AUTH_TOKEN_TAG: String = "AUTH_TOKEN"

const val AUTH_ERROR_CODE_TAG: String = "AUTH_ERROR_CODE"

const val APP_DATASTORE_FILE_TAG: String = "APP_DATASTORE"



const val GENERAL_JSON_TAG: String = "GENERAL_KOTLINX_JSON"

const val PROBE_TIMEOUT_TAG: String = "PROBE_TIMEOUT"

const val MAX_SIZE_TAG: String = "MAX_SIZE_JSON"

const val PROBE_PATH_TAG: String = "PROBE_PATH"

const val SESSION_SCOPE_ID_TAG: String = "UserSessionScope"

const val SESSION_SCOPE_NAME_TAG: String = "UserSession"

//Domain Probe
const val PROBE_PATH: String = "/public/connectivity-probe.png"

const val PROBE_TIMEOUT: Long = 3_000L

const val MAX_SIZE: Long = 999_999L

//Network Diagnostic
const val GRAFANA_LOKI_URL_TAG: String = "LOKI_URL"
const val GRAFANA_LOKI_USER_ID_TAG: String = "LOKI_USER_ID"
const val GRAFANA_LOKI_API_KEY_TAG: String = "LOKI_API_KEY"