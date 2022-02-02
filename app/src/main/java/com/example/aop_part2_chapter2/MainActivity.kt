package com.example.aop_part2_chapter2

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.aop_part2_chapter2.api.LottoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val number_picker:NumberPicker by lazy{ findViewById(R.id.number_picker)}
    private val bt_add: Button by lazy{ findViewById(R.id.bt_add)}
    private val bt_clear: Button by lazy {findViewById(R.id.bt_clear)}
    private val bt_run :Button by lazy{findViewById(R.id.bt_run)}
    private val tv_date: TextView by lazy { findViewById(R.id.tv_date) }
    private val bt_real:Button by lazy { findViewById(R.id.bt_real) }
    private val bt_current:Button by lazy { findViewById(R.id.bt_current) }
    private var didrun = false
    private val real_number_list= mutableListOf<Int>()
    private var current = 990
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.dhlottery.co.kr")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private lateinit var lottoService: LottoService

    //set을 통해 번호 추가시 중복방지
    private val picNumberSet = mutableSetOf<Int>()
    //리스트로 fb선언을 하여 간편하게 코드 구성
    private val number_tv_list :List<TextView> by lazy {
        listOf<TextView>(

            findViewById(R.id.tv_1),
            findViewById(R.id.tv_2),
            findViewById(R.id.tv_3),
            findViewById(R.id.tv_4),
            findViewById(R.id.tv_5),
            findViewById(R.id.tv_6),

            )
    }
    private val number_tv_real_list :List<TextView> by lazy {
        listOf<TextView>(

            findViewById(R.id.tv_real_1),
            findViewById(R.id.tv_real_2),
            findViewById(R.id.tv_real_3),
            findViewById(R.id.tv_real_4),
            findViewById(R.id.tv_real_5),
            findViewById(R.id.tv_real_6),

            )
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable {
            currentNumberCalculate()
        }).start()



        //number_picker 1~45까지의 숫자 할당
        number_picker.minValue = 1
        number_picker.maxValue = 45
        number_picker.textColor = Color.argb(255,0,0,0)
        initRunButton()
        initAddButton()
        initClearButton()
        initRealNumber()
    }



    private fun initRunButton() {
        bt_run.setOnClickListener{
            val list = getRandomNumber()
            //foreach만으로는 index값을 가져올 수 없기에 해당 함수를 사용하여 레이아웃의 text뷰에 getrandm=omnumber로 얻은 리스트를 연결
            list.forEachIndexed{ index, number ->
                val textView = number_tv_list[index]
                textView.text = number.toString()
                setNumberBackground(number,textView)
                textView.isVisible=true
            }
            didrun=true
        }
    }
    private fun initAddButton() {
        bt_add.setOnClickListener{
            if (didrun){
                Toast.makeText(this, "초기화 이후에 실행해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (picNumberSet.size >=5){
                Toast.makeText(this,"번호는 5개까지만 추가할 수 있습니다",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (picNumberSet.contains(number_picker.value)){
                Toast.makeText(this, "이미 선택한 번호입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //여기서 textView를 set의 사이즈를 통해 인덱스를 구분하고 각 인덱스(순서)에 해당하는 숫자를 순서대로 레이아웃의 text에 배치
            val textView = number_tv_list[picNumberSet.size]
            textView.isVisible=true
            textView.text= number_picker.value.toString()

            //텍스트뷰의 숫자마다 다른 배경을 정해주는 코드
            setNumberBackground(number_picker.value, textView)



            picNumberSet.add(number_picker.value)
        }
    }

    private fun initClearButton() {
        bt_clear.setOnClickListener{
            picNumberSet.clear()
            number_tv_list.forEach{
                it.isVisible=false
            }
            didrun=false
        }

    }



    private fun getRandomNumber(): List<Int> {
        //리스트에 1~45까지 순서대로 넣은 후 shuffle함수를 통해 리스트를 섞은 후 sublist를 통해 길이가 6인 리스트를 추출
        val numberlist = mutableListOf<Int>()
            .apply {
                for (i in 1..45){
                    if (picNumberSet.contains(i)){
                        continue
                    }
                    this.add(i)
                }
            }
        numberlist.shuffle()
        //번호를 직접 추가하는 과정을 넣었기 때문에 추가한 번호 set을 tolist하고 총 6개의 번호중 직접 추가한 번호의 갯수를 제외한 sublist를 추출
        val newlist =picNumberSet.toList() + numberlist.subList(0,6-picNumberSet.size)
        return newlist.sorted()
    }

    private fun setNumberBackground(number:Int, textView: TextView){
        when(number){
            in 1..10 -> textView.background = ContextCompat.getDrawable(this,R.drawable.circle_yellow)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this,R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this,R.drawable.circle_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this,R.drawable.circle_gray)
            in 40..45 -> textView.background = ContextCompat.getDrawable(this,R.drawable.circle_green)
        }
    }
    private fun initRealNumber(){
        bt_real.setOnClickListener {

        }
        bt_current.setOnClickListener {
            currentNumberView()
        }


    }

    private fun currentNumberView() {
        Log.d("현재회차", current.toString())
        val list = real_number_list
        Log.d("numberView",list.toString())
        list.forEachIndexed { index, number ->
            val textView = number_tv_real_list[index]
            textView.text = number.toString()
            setNumberBackground(number, textView)
            textView.isVisible = true
        }
    }


    private fun currentNumberCalculate() {
        lottoService=retrofit.create(LottoService::class.java)
            lottoService.getCurrentLotto(current)
                .enqueue(object : Callback<LottoDto> {
                    override fun onResponse(call: Call<LottoDto>, response: Response<LottoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("실패", "")
                            return
                        }
                        response.body()?.let {
                            if (it.returnValue=="success") {
                                current++
                                Log.d("성공여부", current.toString())
                                Log.d("return value",it.returnValue)
                                currentNumberCalculate()
                            } else {
                                current--
                                Log.d("최근회차완료", current.toString())
                                Log.d("회차 로또번호",it.toString())
                                currentNumberSetting(current)
                                return
                            }
                        }
                    }
                    override fun onFailure(call: Call<LottoDto>, t: Throwable) {
                        Log.d("실패", "")
                        return
                    } })
    }

    private fun currentNumberSetting(current:Int) {
        lottoService=retrofit.create(LottoService::class.java)
        lottoService.getCurrentLotto(current)
            .enqueue(object : Callback<LottoDto> {
                override fun onResponse(call: Call<LottoDto>, response: Response<LottoDto>) {
                    if (response.isSuccessful.not()) {
                        Log.d("실패", "")
                        return
                    }
                    response.body()?.let {
                            if (real_number_list.size == 0) {
                                real_number_list.add(it.drwtNo1)
                                real_number_list.add(it.drwtNo2)
                                real_number_list.add(it.drwtNo3)
                                real_number_list.add(it.drwtNo4)
                                real_number_list.add(it.drwtNo5)
                                real_number_list.add(it.drwtNo6)
                            } else{
                                real_number_list.clear()
                                real_number_list.add(it.drwtNo1)
                                real_number_list.add(it.drwtNo2)
                                real_number_list.add(it.drwtNo3)
                                real_number_list.add(it.drwtNo4)
                                real_number_list.add(it.drwtNo5)
                                real_number_list.add(it.drwtNo6)
                            }
                            Log.d("로또 리스트",real_number_list.toString())

                            return

                    }
                }
                override fun onFailure(call: Call<LottoDto>, t: Throwable) {
                    Log.d("실패", "")
                    return
                } })
    }



}