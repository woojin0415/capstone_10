package com.dinkar.blescanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import com.dinkar.blescanner.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private lateinit var mBinding: ActivityHomeBinding
    //매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재선언
    private val binding get() = mBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //자동 생성된 뷰 바인딩 클래스에서의 inflate 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        // 이제부터 binding 바인딩 변수를 활용하여 마음 껏 xml 파일 내의 뷰 id 접근이 가능해집니다.

        binding.ScanningButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // intent 객체 생성
            //intent.putExtra("msg", binding.tvSendMSG.text.toString())
            // HelloWorld 라는 문자열을 담은 뒤 msg 라는 키로 잠금
            startActivity(intent)
            // intent에 저장되어 있는 액티비티(SubActivity)로 이동
            // finish() // finish() 사용 시, SubActivity로 이동하면서 MainActivity가 파괴되어 다시 켜지지 않음
        }
    }


}