package com.example.aop_part2_chapter2

import com.google.gson.annotations.SerializedName

data class LottoDto(
    @SerializedName("drwtNo1") val drwtNo1:Int,
    @SerializedName("drwtNo2") val drwtNo2:Int,
    @SerializedName("drwtNo3") val drwtNo3:Int,
    @SerializedName("drwtNo4") val drwtNo4:Int,
    @SerializedName("drwtNo5") val drwtNo5:Int,
    @SerializedName("drwtNo6") val drwtNo6:Int,
    @SerializedName("returnValue") val returnValue:String


)
