package com.example.mysoreprintersproject.network

import com.example.mysoreprintersproject.responses.CheckInRequest
import com.example.mysoreprintersproject.responses.ChecksResponses
import com.example.mysoreprintersproject.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {




    @FormUrlEncoded
    @POST("/mobile/login/")
    suspend fun login(
        @Field("email")  username: String,
        @Field("password")  password: String,
        @Field("fcm_token")fcmToken:String
    ) : LoginResponse




//    @FormUrlEncoded
//    @POST("api/end-user/register/")
//    suspend fun register(
//        @Field("first_name")name:String,
//        @Field("last_name")surname:String,
//        @Field("email")email:String,
//        @Field("telephone_number")phoneNumber:String,
//        @Field("password")password:String,
//        @Field("address")address:String
//    ):SignUpResponse
//
//
//
//    @FormUrlEncoded
//    @POST("api/forget-password/")
//    suspend fun resetpasswordotp(
//        @Field("email")email:String
//    ):ResetOtpResponse
//
//
//
//    @FormUrlEncoded
//    @POST("api/verify-otp/")
//    suspend fun verifyotp(
//        @Field("otp")otp:String
//    ):ResetOtpResponse
//
//
//    @FormUrlEncoded
//    @POST("api/reset-password/")
//    suspend fun resetpassword(
//        @Field("otp")otp:String,
//        @Field("new_password")newpassword:String,
//        @Field("confirm_password")confirmpassword:String
//    ):ResetOtpResponse


}