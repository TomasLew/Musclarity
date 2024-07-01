package com.example.musclarity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DeviceListAdapter(private val context: Context, private val deviceList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textName: TextView = v.findViewById<TextView>(R.id.textViewDeviceName)
        var textAddress: TextView = v.findViewById<TextView>(R.id.textViewDeviceAddress)
        var linearLayout: LinearLayout = v.findViewById<LinearLayout>(R.id.linearLayoutDeviceInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_layout, parent, false)
        val vh = ViewHolder(v)
        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ViewHolder
        val deviceInfoModel = deviceList[position] as DeviceInfoModel
        itemHolder.textName.text = deviceInfoModel.deviceName
        itemHolder.textAddress.text = deviceInfoModel.deviceHardwareAddress

        // When a device is selected
        itemHolder.linearLayout.setOnClickListener {
            val intent = Intent(context, GraphActivity::class.java)
            // Send device details to the GraphActivity
            intent.putExtra("deviceName", deviceInfoModel.deviceName)
            intent.putExtra("deviceAddress", deviceInfoModel.deviceHardwareAddress)
            // Call GraphActivity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        val dataCount = deviceList.size
        return dataCount
    }
}