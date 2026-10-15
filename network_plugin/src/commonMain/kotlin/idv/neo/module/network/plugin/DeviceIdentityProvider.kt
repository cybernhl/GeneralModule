package idv.neo.module.network.plugin

interface DeviceIdentityProvider {
    val ostype: String
    val app_version:String
    val device_id:String
}