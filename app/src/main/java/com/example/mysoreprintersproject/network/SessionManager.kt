package com.example.mysoreprintersproject.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.mysoreprintersproject.R
import java.util.concurrent.Phaser

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),
        Context.MODE_PRIVATE)


    private val sharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    companion object {
        const val USER_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ID="id"
        const val ROLE="role"
        const val COUNTRY_CODE="country_code"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val PLACE_NAME="PLACE_NAME"
        private const val AREA="AREA"
        private const val PHASE="PHASE"
        private const val CITY="CITY"
        private const val STATE="STATE"
        private const val PINCODE="PINCODE"
        private const val KM="km"
    }




    fun saveCountryCode(code:String){
        val editor = prefs.edit()
        editor.putString(COUNTRY_CODE, code)
        editor.apply()
    }
    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun saveRefreshToken(token: String) {
        val editor = prefs.edit()
        editor.putString(REFRESH_TOKEN, token)
        editor.apply()
    }


    fun saveKm(km:String){
        val editor=prefs.edit()
        editor.putString(KM,km)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN,null)
    }
    fun fetchRefreshToken(): String? {
        return prefs.getString(REFRESH_TOKEN,null)
    }


    fun fetchKm():String?{
        return prefs.getString(KM,null)
    }

    fun logout() {
        prefs.edit().apply {
            remove(USER_TOKEN)
            remove(REFRESH_TOKEN)
            apply()
        }
    }

    fun clearSession() {
        sharedPreferences.edit().remove("access_token").apply()
        sharedPreferences.edit().remove("role").apply()
        sharedPreferences.edit().remove("latitude").apply()
        sharedPreferences.edit().remove("longitude").apply()
        sharedPreferences.edit().remove("PLACE_NAME").apply()
        sharedPreferences.edit().remove("AREA").apply()
        sharedPreferences.edit().remove("PHASE").apply()
        sharedPreferences.edit().remove("STATE").apply()
        sharedPreferences.edit().remove("PINCODE").apply()
    }


    fun saveUserId(userId:String){
        val editor=prefs.edit()
        editor.putString(USER_ID,userId)
        editor.apply()
    }

    fun saveUserRole(role:String){
        val editor=prefs.edit()
        editor.putString(ROLE,role)
        editor.apply()
    }

    fun fetchUserId():String?{
        return prefs.getString(USER_ID,null)
    }

    fun fetchCountryCode():String?{
        return prefs.getString(COUNTRY_CODE,null)
    }


    fun fetchUserRole():String?{
        return  prefs.getString(ROLE,null)
    }


    // Store latitude
    fun storeLatitude(latitude: Double) {
        val editor = prefs.edit()
        editor.putFloat(KEY_LATITUDE, latitude.toFloat())
        editor.apply()
    }

    // Retrieve latitude
    fun getLatitude(): Double? {
        val latitude = prefs.getFloat(KEY_LATITUDE, Float.NaN)
        return if (!latitude.isNaN()) latitude.toDouble() else null
    }

    // Store longitude
    fun storeLongitude(longitude: Double) {
        val editor = prefs.edit()
        editor.putFloat(KEY_LONGITUDE, longitude.toFloat())
        editor.apply()
    }

    // Retrieve longitude
    fun getLongitude(): Double? {
        val longitude = prefs.getFloat(KEY_LONGITUDE, Float.NaN)
        return if (!longitude.isNaN()) longitude.toDouble() else null
    }




    fun storePlaceName(placeName: String) {
        val editor = prefs.edit()
        editor.putString(PLACE_NAME, placeName)
        editor.apply()
    }

    fun storePhase(phase: String) {
        val editor = prefs.edit()
        editor.putString(PHASE, phase)
        editor.apply()
    }

    fun storeArea(area: String) {
         val editor = prefs.edit()
        editor.putString(AREA, area)
        editor.apply()
    }

    fun storePincode(pincode: String) {
        val editor = prefs.edit()
        editor.putString(PINCODE, pincode)
        editor.apply()
    }

    fun storeCity(city: String) {
        val editor = prefs.edit()
        editor.putString(CITY, city)
        editor.apply()
    }

    fun storeState(state: String) {
        val editor = prefs.edit()
        editor.putString(STATE, state)
        editor.apply()
    }

    fun fetchCity():String?{
        return prefs.getString(CITY,null)
    }
    fun fetchPhase():String?{
        return prefs.getString(PHASE,null)
    }
    fun fetchPlace():String?{
        return prefs.getString(PLACE_NAME,null)
    }
    fun fetchArea():String?{
        return prefs.getString(AREA,null)
    }
    fun fetchPincode():String?{
        return prefs.getString(PINCODE,null)
    }
    fun fetchState():String?{
        return prefs.getString(STATE,null)
    }



    fun saveCheckInState(isCheckedIn: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean("IS_CHECKED_IN", isCheckedIn)
        editor.apply()
    }

    fun isCheckedIn(): Boolean {
        return prefs.getBoolean("IS_CHECKED_IN", false)
    }

}