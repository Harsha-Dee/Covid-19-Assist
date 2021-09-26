package com.example.covid

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    lateinit var pinCodeEdt: EditText
    lateinit var centersRV: RecyclerView
    lateinit var centerRVAdapter: CenterRVAdapter
    lateinit var centerList: List<CenterRvModal>
    lateinit var loadingPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.title = "Vaccine Availability"

        searchButton = findViewById(R.id.idBtnSearch)
        pinCodeEdt = findViewById(R.id.idEdtPinCode)
        centersRV = findViewById(R.id.centersRV)
        loadingPB = findViewById(R.id.idPBLoading)
        centerList = ArrayList<CenterRvModal>()

        searchButton.setOnClickListener {

            val pinCode = pinCodeEdt.text.toString()

            //validating the pincode
            if (pinCode.length != 6) {

                // invalid pincode
                Toast.makeText(this@MainActivity, "Please enter valid pin code", Toast.LENGTH_SHORT).show()
            } else {

                //valid pincode
                (centerList as ArrayList<CenterRvModal>).clear()

                // get instance of Calender Class
                val c = Calendar.getInstance()

                // get year month and date
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                //creating date picker dialog.
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                        //trigger progress bar
                        loadingPB.setVisibility(View.VISIBLE)

                        // creating date
                        val dateStr: String = """$dayOfMonth - ${monthOfYear + 1} - $year"""

                        //api call
                        getAppointments(pinCode, dateStr)
                    },
                    year,
                    month,
                    day
                )
                // calling a method to display
                // our datepicker dialog.
                dpd.show()
            }
        }
    }

    // api function
    private fun getAppointments(pinCode: String, date: String) {
        //api
        val url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode="+pinCode +"&date="+date

        //creating queue
        val queue = Volley.newRequestQueue(this@MainActivity)

        //creating request
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, { response ->
                Log.e("TAG", "SUCCESS RESPONSE IS $response")

                //stop progress bar
                loadingPB.setVisibility(View.GONE)

                try {

                    //get JSONARRAY named centers
                    val centerArray = response.getJSONArray("centers")

                    //if centerArray length is zero the no vaccination center available
                    if (centerArray.length().equals(0)) {
                        Toast.makeText(this, "No Center Found", Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until centerArray.length()) {

                        //getting jsonObject for each element in the array
                        val centerObj = centerArray.getJSONObject(i)

                        //getting the required information from the jsonObject later we will pass to Modal Class
                        val centerName: String = centerObj.getString("name")
                        val centerAddress: String = centerObj.getString("address")
                        val centerFromTime: String = centerObj.getString("from")
                        val centerToTime: String = centerObj.getString("to")
                        val fee_type: String = centerObj.getString("fee_type")

                        // in api,session is jsonArray
                        //so we have to get the jsonArrayObject
                        val sessionObj = centerObj.getJSONArray("sessions").getJSONObject(0)

                        //getting required information session object later we will pass to Modal Class
                        val ageLimit: Int = sessionObj.getInt("min_age_limit")
                        val vaccineName: String = sessionObj.getString("vaccine")
                        val avaliableCapacity: Int = sessionObj.getInt("available_capacity")


                        //passing the extracted information to modal class
                        val center = CenterRvModal(
                            centerName,
                            centerAddress,
                            centerFromTime,
                            centerToTime,
                            fee_type,
                            ageLimit,
                            vaccineName,
                            avaliableCapacity
                        )


                        // adding the above modal to centerList Array of type CenterRvModal
                        centerList = centerList + center
                    }

                    //invoking the Adapter
                    centerRVAdapter = CenterRVAdapter(centerList)

                    //setting the layout manager of recycler view
                    centersRV.layoutManager = LinearLayoutManager(this)

                    //setting the adapter to recyclerview
                    centersRV.adapter = centerRVAdapter

                    //notify adapter about data change
                    centerRVAdapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    // below line is for handling json exception.
                    e.printStackTrace();
                }
            },
                { error ->
                    Log.e("TAG", "RESPONSE IS $error")


                    //Display toast as please try again later
                    Toast.makeText(this@MainActivity, "Please try again later", Toast.LENGTH_SHORT).show()
                })

        //add the request to the queue
        queue.add(request)
    }
}
