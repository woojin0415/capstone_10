package com.dinkar.blescanner

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedWriter
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ScannerFragment : Fragment() {
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var storeButton: Button
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btScanner: BluetoothLeScanner? = null
    val eddystoneServiceId: ParcelUuid = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")
    var beaconSet: HashSet<Beacon> = HashSet()
    var beaconTypePositionSelected = 0
    var beaconAdapter: BeaconsAdapter? = null

    //추가
    var rssilist1 = ArrayList<Int>()
    var rssilist2 = ArrayList<Int>()
    var rssilist3 = ArrayList<Int>()

    var timelist1=ArrayList<String>()
    var timelist2=ArrayList<String>()
    var timelist3=ArrayList<String>()

    val mac1="C2:02:DD:00:13:E7"
    val mac2="C1:00:47:00:33:28"
    val mac3="C2:02:DD:00:13:E9"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        initViews(view)
        setUpBluetoothManager()
        return view
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    private fun initViews(view: View) {
        startButton = view.findViewById(R.id.startButton)
        stopButton = view.findViewById(R.id.stopButton)
        storeButton = view.findViewById(R.id.storeButton)

        spinner = view.findViewById(R.id.spinner)
        recyclerView = view.findViewById(R.id.recyclerView)
        startButton.setOnClickListener { onStartScannerButtonClick() }
        stopButton.setOnClickListener { onStopScannerButtonClick() }
        //추가
        storeButton.setOnClickListener { onStoreButtonClick() }
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        beaconAdapter = BeaconsAdapter(beaconSet.toList())
        recyclerView.adapter = beaconAdapter
        beaconAdapter!!.filter.filter(Utils.ALL)
        spinner.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                beaconTypePositionSelected = position
                setBeaconFilter(position)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                beaconAdapter!!.filter.filter(Utils.ALL)
            }

        })
        activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.BLE_Scanner_Type,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }
    }

    private fun onStartScannerButtonClick() {
        startButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE
        btScanner!!.startScan(leScanCallback)
    }


    private fun onStopScannerButtonClick() {
        stopButton.visibility = View.GONE
        startButton.visibility = View.VISIBLE
        btScanner!!.stopScan(leScanCallback)

    }
    //추가

    private fun onStoreButtonClick() {
        storeRssi()
    }

    private fun setUpBluetoothManager() {
        btManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        btScanner = btAdapter?.bluetoothLeScanner
        if (btAdapter != null && !btAdapter!!.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        }
        checkForLocationPermission()
    }

    private fun checkForLocationPermission() {
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (activity!!.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("This app needs location access")
            builder.setMessage("Please grant location access so this app can detect  peripherals.")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.setOnDismissListener {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_COARSE_LOCATION
                )
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    println("coarse location permission granted")
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover BLE beacons")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
    }


    private val leScanCallback: ScanCallback by lazy {
        object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord
            val beacon = Beacon(result.device.address)
            beacon.rssi = result.rssi
            beacon.manufacturer = result.device.name

            storeList(beacon.macAddress.toString(), beacon.rssi!!)

            if(beacon.macAddress == "00:3D:E8:3C:D9:7A"){
                Log.i("Lee","Scan: "+"00:3D:E8:3C:D9:7A")
            }


            if (scanRecord != null) {
                val serviceUuids = scanRecord.serviceUuids
                val iBeaconManufactureData = scanRecord.getManufacturerSpecificData(0X004c)
                if (serviceUuids != null && serviceUuids.size > 0 && serviceUuids.contains(
                        eddystoneServiceId
                    )
                ) {
                    val serviceData = scanRecord.getServiceData(eddystoneServiceId)
                    if (serviceData != null && serviceData.size > 18) {
                        val eddystoneUUID =
                            Utils.toHexString(Arrays.copyOfRange(serviceData, 2, 18))
                        val namespace = String(eddystoneUUID.toCharArray().sliceArray(0..19))
                        val instance = String(
                            eddystoneUUID.toCharArray()
                                .sliceArray(20 until eddystoneUUID.toCharArray().size)
                        )
                        beacon.type = Beacon.beaconType.eddystoneUID
                        beacon.namespace = namespace
                        beacon.instance = instance

                        Log.e("DINKAR", "Namespace:$namespace Instance:$instance")
                    }
                }
                if (iBeaconManufactureData != null && iBeaconManufactureData.size >= 23) {
                    val iBeaconUUID = Utils.toHexString(iBeaconManufactureData.copyOfRange(2, 18))
                    val major = Integer.parseInt(
                        Utils.toHexString(
                            iBeaconManufactureData.copyOfRange(
                                18,
                                20
                            )
                        ), 16
                    )
                    val minor = Integer.parseInt(
                        Utils.toHexString(
                            iBeaconManufactureData.copyOfRange(
                                20,
                                22
                            )
                        ), 16
                    )
                    beacon.type = Beacon.beaconType.iBeacon
                    beacon.uuid = iBeaconUUID
                    beacon.major = major
                    beacon.minor = minor
                    Log.e("DINKAR", "iBeaconUUID:$iBeaconUUID major:$major minor:$minor")
                }
            }
            if(beacon == null){
                Log.e("Lee","beacon is null")
            }else{
                beaconSet.add(beacon)
                (recyclerView.adapter as BeaconsAdapter).updateData(beaconSet.toList(),beaconTypePositionSelected)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("DINKAR", errorCode.toString())
        }
    }
    }


    fun setBeaconFilter(position: Int) {
        when (position) {
            0 -> {
                beaconAdapter!!.filter.filter(Utils.ALL)
            }
            1 -> {
                beaconAdapter!!.filter.filter(Utils.EDDYSTONE)
            }
            2 -> {
                beaconAdapter!!.filter.filter(Utils.IBEACON)
            }
        }
    }
    private fun storeList(mac: String, rssi: Int){
        val now = System.currentTimeMillis()
        val d = Date(now)
        val sd = SimpleDateFormat("hh:mm:ss")
        val time: String = sd.format(d)

        if(mac==mac1){
            rssilist1.add(rssi)
            timelist1.add(time)
        }
        if(mac==mac2){
            rssilist2.add(rssi)
            timelist2.add(time)
        }
        if(mac==mac3){
            rssilist3.add(rssi)
            timelist3.add(time)
        }

    }

    private fun storeRssi(){
        storeBeaconRssi(mac1, rssilist1, timelist1)
        storeBeaconRssi(mac2, rssilist2, timelist2)
        storeBeaconRssi(mac3, rssilist3, timelist3)

    }


    private fun storeBeaconRssi(mac:String, rssilist: ArrayList<Int>, timelist: ArrayList<String>) {
        val result = rssilist
        val timeL= timelist
        Log.i("Lee", "Store is begin")
        val now = System.currentTimeMillis()
        val d = Date(now)
        val sd = SimpleDateFormat("yyyy-MM-dd-hh:mm:ss")
        val time: String = sd.format(d)
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        //val path = Environment.getExternalStorageState()
        //val filepath = File(path + "/Documents" + "/" + time + " " + name + ".txt")
        //val writer = FileWriter(path + "/Documents" + "/" + time + " " + name + ".txt", true)
        //val filepath = (context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).toString()


        val writer = FileWriter("$path/$time $mac.csv", true)
        var bw: BufferedWriter? = null // 출력 스트림 생성
        bw = BufferedWriter(writer)
        var temp: String
        var j=1;
        try{
            for(i in result){
                temp =i.toString()
                bw.write(temp)
                bw.write(",")

                bw.write(timeL[j]);
                bw.write(",")
                bw.newLine() // 개행
                //writer.write(i)
                j++;
            }
        }
        catch (e:Exception){
        }finally {
            bw.close()
            writer.close()
        }

        /*if(isExternalStorageWritable()==true){
            Log.i("Lee", "Store is begin")
            val now = System.currentTimeMillis()
            val d = Date(now)
            val sd = SimpleDateFormat("hh:mm:ss")
            val time: String = sd.format(d)
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
            //val path = Environment.getExternalStorageState()
            //val filepath = File(path + "/Documents" + "/" + time + " " + name + ".txt")
            //val writer = FileWriter(path + "/Documents" + "/" + time + " " + name + ".txt", true)
            //val filepath = (context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).toString()


            val writer = FileWriter("$path/$time $mac.csv", true)
            var bw: BufferedWriter? = null // 출력 스트림 생성
            bw = BufferedWriter(writer)
            var temp: String
            var j=1;
            try{
                for(i in result){
                    temp =i.toString()
                    bw.write(temp)
                    bw.write(",")

                    bw.write(timeL[j]);
                    bw.write(",")
                    bw.newLine() // 개행
                    //writer.write(i)
                    j++;
                }
            }
            catch (e:Exception){
            }finally {
                bw.close()
                writer.close()
            }

        }
        Log.i("Lee", "Store is not start")*/
    }
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.DIRECTORY_DOCUMENTS
    }
}
