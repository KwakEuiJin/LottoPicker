package com.example.aop_part2_chapter2.api

import com.example.aop_part2_chapter2.LottoDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoService {
    @GET("common.do?method=getLottoNumber")
    fun getCurrentLotto(
        @Query("drwNo") drwNo:Int
    ): Call<LottoDto>


}