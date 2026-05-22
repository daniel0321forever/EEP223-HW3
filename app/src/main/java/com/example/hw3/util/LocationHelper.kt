package com.example.hw3.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class GeoCoordinates(val latitude: Double, val longitude: Double)

class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    suspend fun getCoordinates(): GeoCoordinates {
        if (!hasLocationPermission()) {
            return seattleFallback()
        }
        val lastLocation = getLastLocation()
        if (lastLocation != null) {
            return GeoCoordinates(lastLocation.latitude, lastLocation.longitude)
        }
        val currentLocation = getCurrentLocation()
        if (currentLocation != null) {
            return GeoCoordinates(currentLocation.latitude, currentLocation.longitude)
        }
        return seattleFallback()
    }

    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (cont.isActive) cont.resume(location)
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
    }

    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val cancellationToken = CancellationTokenSource()
        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { location ->
                if (cont.isActive) cont.resume(location)
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
    }

    companion object {
        fun seattleFallback(): GeoCoordinates =
            GeoCoordinates(latitude = 47.6062, longitude = -122.3321)
    }
}
