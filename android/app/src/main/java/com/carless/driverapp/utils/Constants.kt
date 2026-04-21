package com.carless.driverapp.utils

object Constants {
    // 10.0.2.2 is the Android emulator alias for the host machine's localhost
    //const val BASE_URL = "http://10.0.2.2:8080/api/"
    const val BASE_URL = "http://127.0.0.1:8080/api/"

    val CABA_ZONES = listOf(
        "Palermo", "San Telmo", "Puerto Madero", "Recoleta", "Belgrano",
        "Flores", "Caballito", "Villa Crespo", "Almagro", "Boedo",
        "Montserrat", "Retiro", "Congreso", "Núñez", "Coghlan"
    )
}
