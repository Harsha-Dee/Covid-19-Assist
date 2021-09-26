package com.example.covid.covidtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.covid.MainActivity
import com.example.covid.R
import com.example.covid.news.NewsActivity
import kotlinx.android.synthetic.main.activity_covid19_tracker.*
import org.json.JSONException

class Covid19Tracker : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var worldCasesTV:TextView
    lateinit var worldRecoveredTV:TextView
    lateinit var worldDeathsTv:TextView
    lateinit var countryCasesTV:TextView
    lateinit var countryRecoveredTV:TextView
    lateinit var countryDeathsTV:TextView
    lateinit var stateRV:RecyclerView
    lateinit var stateRVAdapter: StateRVAdapter
    lateinit var stateList: List<StateModal>

    override fun onCreate(savedInstanceState: Bundle?) {

        val actionBar = supportActionBar
        actionBar!!.title = "Covid-19 Cases"
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covid19_tracker)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> {
                    actionBar!!.title = "Covid-19 Cases"
                    //Toast.makeText(this, "item1", Toast.LENGTH_LONG).show()
                }
                R.id.item2 -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //Toast.makeText(this, "item2", Toast.LENGTH_LONG).show()
                }
                R.id.item3 -> {
                    //Toast.makeText(this, "item3", Toast.LENGTH_LONG).show()
                    val cowin_url : String = "https://www.cowin.gov.in/"
                    val builder =  CustomTabsIntent.Builder();
                    val customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(cowin_url));
                }

            }
            true
        }

        worldCasesTV = findViewById(R.id.idTVWorldCases)
        worldRecoveredTV = findViewById(R.id.idTVWorldRecovered)
        worldDeathsTv = findViewById(R.id.idTVWorldDeaths)
        countryCasesTV = findViewById(R.id.idTVIndiaCases)
        countryRecoveredTV = findViewById(R.id.idTVIndiaRecovered)
        countryDeathsTV = findViewById(R.id.idTVIndiaDeaths)
        stateRV = findViewById(R.id.idRVStates)
        stateList = ArrayList<StateModal>()

        getStateInfo()
        getWorldInfo()
    }

    private fun getStateInfo(){
        val url = "https://api.rootnet.in/covid19-in/stats/latest"

        val queue = Volley.newRequestQueue(this@Covid19Tracker)

        val request =
            JsonObjectRequest(Request.Method.GET, url, null, { response->
                try {
                    val dataObj = response.getJSONObject("data")
                    val summaryObj = dataObj.getJSONObject("summary")
                    val cases:Int = summaryObj.getInt("total")
                    val recovered:Int = summaryObj.getInt("discharged")
                    val deaths:Int  = summaryObj.getInt("deaths")

                    countryCasesTV.text  = cases.toString()
                    countryRecoveredTV.text = recovered.toString()
                    countryDeathsTV.text = deaths.toString()


                    val regionalArray = dataObj.getJSONArray("regional")
                    for(i in 0 until  regionalArray.length()){
                        val regionalObj = regionalArray.getJSONObject(i)
                        val stateName:String = regionalObj.getString("loc")
                        val cases:Int = regionalObj.getInt("totalConfirmed")
                        val deaths:Int = regionalObj.getInt("deaths")
                        val recovered:Int = regionalObj.getInt("discharged")

                        val stateModal = StateModal(stateName, recovered, deaths, cases)
                        stateList = stateList+stateModal
                    }
                    stateRVAdapter = StateRVAdapter(stateList)
                    stateRV.layoutManager = LinearLayoutManager(this)
                    stateRV.adapter = stateRVAdapter

                    stateRVAdapter.notifyDataSetChanged()

                }catch (e:JSONException){
                    e.printStackTrace()
                    Log.d("Error", "Cannot Fetch The data")
                }
            },{error->
                Toast.makeText(this, "Try again later", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }

    private fun getWorldInfo(){
        val url = "https://disease.sh/v3/covid-19/all"

        val queue = Volley.newRequestQueue(this@Covid19Tracker)

        val request  =
            JsonObjectRequest(Request.Method.GET, url, null, {response->
                try {
                    Log.d("response", "Success")
                    val worldCases : Int = response.getInt("cases")
                    val worldRecovered : Int = response.getInt("recovered")
                    val worldDeaths : Int = response.getInt("deaths")

                    worldCasesTV.text = worldCases.toString()
                    worldRecoveredTV.text = worldRecovered.toString()
                    worldDeathsTv.text = worldDeaths.toString()


                }catch (e:JSONException){
                    e.printStackTrace()
                }
            },{
                Toast.makeText(this, "Try again later", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
