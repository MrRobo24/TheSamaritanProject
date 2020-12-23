package com.identity_crisis.thesamaritans.activity.volunteerseeker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.model.Form


class SeekHelpFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var spinnerCategory: Spinner
    lateinit var spinnerType: Spinner
    lateinit var spinnerState: Spinner
    lateinit var edCity: EditText
    lateinit var edPincode: EditText
    lateinit var edDescription: EditText
    lateinit var edContact: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    lateinit var edTitle: EditText

    var title = ""
    var uid = ""
    var category = ""
    var type = ""
    var state = ""
    var city = ""
    var pincode = ""
    var description = ""
    var contact = ""
    var formStatus = "1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_help_form)

        edTitle = findViewById(R.id.edTitle)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerType = findViewById(R.id.spinnerType)
        spinnerState = findViewById(R.id.spinnerState)
        edCity = findViewById(R.id.edCity)
        edPincode = findViewById(R.id.edPincode)
        edDescription = findViewById(R.id.edDescription)
        edContact = findViewById(R.id.edContact)
        btnSubmit = findViewById(R.id.btnSubmit)

        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedPreference), Context.MODE_PRIVATE)

        setUpSpinner()

        btnSubmit.setOnClickListener {
            uploadForm()
        }
    }

    private fun setUpSpinner() {
        //Category
        ArrayAdapter.createFromResource(
            this,
            R.array.category,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }
        spinnerCategory.onItemSelectedListener = this@SeekHelpFormActivity


        //Type
        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }
        spinnerType.onItemSelectedListener = this@SeekHelpFormActivity


        //State
        ArrayAdapter.createFromResource(
            this,
            R.array.states,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerState.adapter = adapter
        }
        spinnerState.onItemSelectedListener = this@SeekHelpFormActivity
    }

    private fun uploadForm() {
        title = edTitle.text.toString()
        uid = sharedPreferences.getString("UID", "").toString()
        category = spinnerCategory.selectedItem.toString()
        type = spinnerType.selectedItem.toString()
        state = spinnerState.selectedItem.toString()
        city = edCity.text.toString()
        pincode = edPincode.text.toString()
        description = edDescription.text.toString()
        contact = edContact.text.toString()

        Log.d("Main", category + "\t" + type + "\t" + state + "\t" + description + "\t" + uid)


        val form = Form(title,category, type, state, city, pincode, description, contact, formStatus)
        val ref = FirebaseDatabase.getInstance().getReference("/forms")


        val formId = ref.child(uid).push().key
        ref.child(uid).child(formId!!).setValue(form)
            .addOnSuccessListener {
                Log.d("Main", "Uploaded form successfully to Firebase Database")
                ref.parent!!.child("/form-owner/$formId").setValue(uid)
                    .addOnSuccessListener {
                        Log.d("Main", "form-owner updated")
                    }

                val intent = Intent(this@SeekHelpFormActivity, PersonalRequestActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to upload form to Firebase Database ${it.message}")
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}