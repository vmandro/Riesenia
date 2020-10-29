package com.example.homework_2


import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import kotlin.collections.HashSet


class Item(var text: String, var date: MyCalendar){
    var wasNotify: Boolean

    init {
        wasNotify = false
    }

    fun setNotify(set: Boolean){
        wasNotify = set
    }

    fun wasCreatedNotification(): Boolean{
        return wasNotify
    }

    override fun toString() : String {
        return text
    }

    fun getDateToString(): String {
        return date.toString()
    }

    fun getCalendarFromDate(): Calendar {
        return date.getCalendar()
    }
}

class MyCalendar(var day: Int, var month: Int, var year: Int, var hour: Int, var minute: Int) {
    var cal = Calendar.getInstance()

    init {
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
    }

    override fun toString(): String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        return "$day.$month.$year $hour:$minute"
    }

    fun getCalendar(): Calendar {
        return cal
    }
}

class MyListAdapter(private val context: Activity, private val items: List<Item>)
    : ArrayAdapter<Item>(context, R.layout.custom_item, items) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_item, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val subtitleText = rowView.findViewById(R.id.dateSelect) as TextView

        titleText.text = items[position].toString()
        subtitleText.text = items[position].getDateToString()

        if(items[position].wasCreatedNotification()) {       //uz je po deadline
            rowView.setBackgroundColor(Color.parseColor("#ffad99"))
        }

        return rowView
    }
}


class MainActivity : AppCompatActivity() {
    var items = ArrayList<Item>()
    var cal = Calendar.getInstance()
    var CHANNEL_ID = "App_channel_ID"
    var notificationId = 1
    lateinit var mainHandler: Handler

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(item: Item) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.alert)
            .setSound(alarmSound)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_text) + item.text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(resources.getString(R.string.notification_text) + item.text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    fun clearTextViews() {
        name.text = Editable.Factory.getInstance().newEditable(resources.getString(R.string.text))
        date.text = ""
        time.text = ""
    }

    fun checkTimeOfItemsToCreateNotification() {
        var c1 = Calendar.getInstance()
        items.forEach {
            if(c1.after(it.getCalendarFromDate()) && !it.wasCreatedNotification()){
                createNotification(it)
                it.setNotify(true)
            }
        }
    }

    fun writeItemNameIntoSharedPreferences(sharedPreferences: SharedPreferences, name: String){
        var editor = sharedPreferences.edit()
        var set = sharedPreferences.getStringSet(resources.getString(R.string.shared_preferences_names), HashSet<String>())

        set?.add(name)

        editor.putStringSet(resources.getString(R.string.shared_preferences_names), set).commit()
    }

    fun writeItemIntoSharedPreferences(sharedPreferences: SharedPreferences, item: Item){
        var editor = sharedPreferences.edit()
        editor.putString(item.toString(), item.getDateToString()).commit()

        writeItemNameIntoSharedPreferences(sharedPreferences, item.toString())
    }

    fun deleteItemNameFromSharedPreferences(sharedPreferences: SharedPreferences, name: String){
        var editor = sharedPreferences.edit()
        var set = sharedPreferences.getStringSet(resources.getString(R.string.shared_preferences_names), HashSet<String>())

        set?.remove(name)

        editor.putStringSet(resources.getString(R.string.shared_preferences_names), set).commit()
    }

    fun deleteItemFromSharedPreferences(sharedPreferences: SharedPreferences, item: Item){
        var editor = sharedPreferences.edit()

        editor.remove(item.toString()).commit()
        deleteItemNameFromSharedPreferences(sharedPreferences, item.toString())
    }

    fun loadItemsFromSharedPreferences(sharedPreferences: SharedPreferences){
        var names = sharedPreferences.getStringSet(resources.getString(R.string.shared_preferences_names), HashSet<String>())

        names?.forEach {
            //itemName = it
            var itemDateAndTime = sharedPreferences.getString(it, "")?.split(' ')

            if(itemDateAndTime?.size!! > 1){
                var dateInfo = itemDateAndTime[0].split('.')   //dd.MM.yyyy
                var timeInfo = itemDateAndTime[1].split(':')   //HH:mm

                var day = dateInfo[0].toInt()
                var month = dateInfo[1].toInt()
                var year = dateInfo[2].toInt()
                var hour = timeInfo[0].toInt()
                var minute = timeInfo[1].toInt()

                var myCalendar = MyCalendar(day, month, year, hour, minute)
                var item = Item(it, myCalendar)

                items.add(item)
                Log.d("ITEM OBNOVENY ZO SHARED PREFERENCES",
                    "Zaznam o Item(${item.toString()}, ${item.getDateToString()}) uspesne obnoveny")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        //bezanie applikacie
        mainHandler = Handler(Looper.getMainLooper())
        val update = object : Runnable {
            override fun run() {
                checkTimeOfItemsToCreateNotification()
                //Toast.makeText(this@MainActivity, "RUN", Toast.LENGTH_LONG).show()
                listView.invalidateViews()
                mainHandler.postDelayed(this, 1000)
            }
        }
        update.run()

        //set adapter
        listView.adapter = MyListAdapter(this, items)

        //shared preferences
        var sharedPrefFile = resources.getString(R.string.shared_pref_file)
        var sharedPref = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        loadItemsFromSharedPreferences(sharedPref)
        listView.invalidateViews()

        //Nastavovanie datumu
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            date.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
            Log.d("DATUM NASTAVENY",
                "Datum nastaveny na ${date.text}")
        }
        datePicker.setOnClickListener {
            DatePickerDialog(
                this@MainActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Nastavovanie casu
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            val myFormat = "HH:mm".format(cal.time)
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            time.text = Editable.Factory.getInstance().newEditable(sdf.format(cal.time))
            Log.d("CAS NASTAVENY",
                "Cas nastaveny na ${time.text}")
        }
        timePicker.setOnClickListener {
            TimePickerDialog(
                this@MainActivity, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        //tlacidlo 'potvrdit'
        confirm.setOnClickListener {
            if (name.text.isNotEmpty()) {
                if (!sharedPref.getStringSet(
                        resources.getString(R.string.shared_preferences_names),
                        HashSet<String>()
                    )?.contains(name.text.toString())!!
                ) {
                    if (date.text.isNotEmpty()) {
                        if (time.text.isNotEmpty()) {
                            var infoFromDate = date.text.split('.')
                            var infoFromTime = time.text.split(':')

                            var myCalendar = MyCalendar(
                                infoFromDate[0].toInt(),        //den
                                infoFromDate[1].toInt(),                                //mesiac
                                infoFromDate[2].toInt(),                                //rok
                                infoFromTime[0].toInt(),                                //hodina
                                infoFromTime[1].toInt()
                            )                                //minuta

                            var item = Item(name.text.toString(), myCalendar)
                            Log.d(
                                "ITEM VYTVORENY",
                                "Vytvoreny zaznam Item(${name.text}, ${date.text} ${time.text})"
                            )

                            items.add(item)
                            writeItemIntoSharedPreferences(sharedPref, item)
                            Log.d(
                                "ITEM PRIDANY DO LISTU",
                                "... pridany"
                            )

                            Toast.makeText(
                                this,
                                "Poznamka s nazvom ${item.text} pridana",
                                Toast.LENGTH_LONG
                            ).show()

                            clearTextViews()
                            listView.invalidateViews()
                        } else {
                            Toast.makeText(this, "Nenastavili ste cas", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Nenastavili ste datum", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Nazov ste uz pouzili", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Nenastavili ste nazov", Toast.LENGTH_LONG).show()
            }
        }

        //vymazanie z listView
        listView.setOnItemLongClickListener { adapterView, view, index, l ->
            var item = items[adapterView.getItemIdAtPosition(index).toInt()]

            Log.d(
                "VYMAZANY ITEM",
                "Zaznam Item(${item.text}, ${item.getDateToString()}) bol vymazany"
            )
            Toast.makeText(this, "Zaznam Item(${item.text}, ${item.date}) bol vymazany", Toast.LENGTH_LONG).show()

            deleteItemFromSharedPreferences(sharedPref, item)
            items.remove(item)
            listView.invalidateViews()

            return@setOnItemLongClickListener true
        }
    }
}
