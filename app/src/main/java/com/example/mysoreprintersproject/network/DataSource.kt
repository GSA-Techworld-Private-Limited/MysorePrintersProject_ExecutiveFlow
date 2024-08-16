package com.example.mysoreprintersproject.network
import com.example.mysoreprintersproject.responses.CheckInRequest
import com.example.mysoreprintersproject.responses.CheckOutRequest
import com.example.mysoreprintersproject.responses.ChecksResponses
import com.example.mysoreprintersproject.responses.CollectionReport
import com.example.mysoreprintersproject.responses.ExecutiveDashboard
import com.example.mysoreprintersproject.responses.LoginResponse
import com.example.mysoreprintersproject.responses.NetSalesResponse
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.SummaryReportResponses
import com.example.mysoreprintersproject.responses.SupplyReportResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DataSource {


    @GET("/app-executive/profile_setting/")
     fun getProfileOfExecutive(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ):Call<ProfileResponses>

    @POST("/app-executive/check_in/")
    suspend fun checkIn(
        @Header("Authorization") token: String,
        @Body checkInRequest: CheckInRequest
    ) : ChecksResponses



    @POST("/app-executive/check_out/")
    suspend fun cheOut(
        @Header("Authorization") token: String,
        @Body checkOutRequest:CheckOutRequest
    ) : ChecksResponses


    @GET("/app-executive/working_summary/")
    fun getExecutiveDashboard(
        @Header("Authorization") token: String,
        @Query("id")id:String,
        @Query("period")period:String
    ):Call<ExecutiveDashboard>


    @GET("/app-executive/summary-report/")
    fun getSummaryReport(
        @Header("Authorization") token: String,
        @Query("id")id:String,
        @Query("period")period:String
    ):Call<SummaryReportResponses>

    @POST("/app-executive/collection_report/")
    fun sendCollectionReport(
        @Header("Authorization") token: String,
        @Body  collection_report: CollectionReport
    ):Call<Void>


    @GET("/app-executive/supply-report-list/")
    fun getSupplyReport(
        @Header("Authorization") token: String,
        @Query("id")id:String,
        @Query("period")period:String
    ):Call<List<SupplyReportResponse>>


    @GET("/app-executive/executive_netsales/")
    fun getNetSale(
        @Header("Authorization") token: String,
        @Query("id")id:String
    ):Call<NetSalesResponse>
//    @GET("api/event/create/")
//    suspend fun getAllEvents(
//        @Header("Authorization") token: String
//    ):EventResponse
//
//
//    @GET("api/event/{id}/passes/")
//    suspend fun getEventPasses(
//        @Header("Authorization")token: String,
//        @Path("id") id: Int
//    ): BuyDetailResponses
//
//    @GET("api/user-purchase-ticket/")
//    suspend fun getTickets(
//        @Header("Authorization") token:String
//    ): TicketDetailsResponse
//
//
//    @GET("api/user-purchase-ticket/")
//     fun getTicketsAfterSucessfull(
//        @Header("Authorization") token:String
//    ):Call<TicketDetailsResponse>
//
//
//    @FormUrlEncoded
//    @POST("api/event/{id}/buy_pass/")
//     fun postTicket(
//        @Header("Authorization") token:String,
//        @Path("id") id: Int,
//        @Field("pass_type")pass_type:String,
//        @Field("quantity")quantity:Int
//    ): Call<PostTicketResponse>
//
//
//
//     @GET("api/user-profile/")
//     fun getProfile(
//         @Header("Authorization")token:String
//    ):Call<UserData>
//
//
//     @FormUrlEncoded
//     @POST("api/add-to-cart/")
//     fun addToCart(
//         @Header("Authorization")token:String,
//         @Field("event_id")event_id:Int,
//         @Field("pass_type")pass_type:String,
//         @Field("quantity")quantity:Int
//     ):Call<PostCartResponses>
//
//
//
//
//
//     @GET("api/get-cart-items/")
//     suspend fun getCart(
//         @Header("Authorization")token:String
//     ):CartResponses
//
//
//     @FormUrlEncoded
//     @PUT("api/remove-increase-product-cart/{id}/")
//      fun  increasequantity(
//         @Header("Authorization")token:String,
//         @Path("id")id: Int,
//         @Field("quantity")quntity:Int
//      ):Call<PostCartResponses>
//
//      @FormUrlEncoded
//      @PUT("api/decrease-product-cart/{id}/")
//      fun decreasequantity(
//          @Header("Authorization")token:String,
//          @Path("id")id: Int,
//          @Field("quantity")quntity:Int
//      ):Call<PostCartResponses>
//
//
//      @DELETE("api/remove-increase-product-cart/{id}/")
//      fun deleteCart(
//          @Header("Authorization")token:String,
//          @Path("id")id: Int
//      ):Call<PostCartResponses>
//
//
//      @POST("api/purchase-from-cart/")
//      fun buyfromcart(
//          @Header("Authorization")token:String,
//      ):Call<CartResponses>
//
//
//
//     @POST("api/logout/")
//     fun logout(
//         @Header("Authorization") token:String,
//     ):Call<LogoutResponse>
//
//    @GET("api/type-of-events/")
//    suspend fun getFilters(
//        @Header("Authorization")token: String
//    ):CategoryResponses
//
//
//    @GET("api/events/filter/")
//    fun getEventsByFilter(
//        @Header("Authorization")token: String,
//        @Query("type_of_event")typeofEvent:String
//    ):Call<CategotyFilterId>
//
//
//    @POST("api/stripe-payment/initiate/")
//    fun initiatePayment(@Body paymentRequest: PaymentRequest):Call<ResponseBody>
//
//
//
//    @POST("api/cinetpay-payment/initiate/")
//    fun initiatePaymentCinepay(@Body paymentRequest: PaymentRequest):Call<ResponseBody>
//
//
//    @POST("api/wave-payment/initiate/")
//    fun initiatePaymentWavepay(@Body paymentRequest: PaymentRequest):Call<ResponseBody>
//
//
//    @POST("api/orange-payment/initiate/")
//    fun initiatePaymentorangepay(@Body paymentRequest: PaymentRequest):Call<ResponseBody>
//
//
//
//
//    @GET("api/v3/convert/")
//    fun getConversionRate(
//        @Query("values") value: Int,
//        @Query("base_currency") baseCurrency: String,
//        @Query("currencies") currencies: String
//    ): Call<ConversionResponse>
//
//
//
//    @FormUrlEncoded
//    @POST("api/favorite_events/")
//    fun postforFavriote(
//        @Header("Authorization")token: String,
//        @Field("event_id")id:String
//    ):Call<FavrioteResponse>
//
//
//    @DELETE("api/remove_favorite_event/{event_id}/")
//    fun removefromFavriote(
//        @Header("Authorization")token: String,
//        @Path("event_id")id:String
//    ):Call<FavrioteResponse>
//
//    @GET("api/favorite_events/")
//    fun getFavriotes(
//        @Header("Authorization")token: String,
//    ):Call<List<FavrioteResponse>>
//
//
//    @GET("api/google-login/")
//    fun googleLogin(
//
//    ):Call<GoogleResponse>
//
//
//
//    @POST("api/send-gift/")
//    fun sendGift(
//        @Header("Authorization")token: String,
//        @Body giftRequests: GiftRequests
//    ):Call<GoogleResponse>

}